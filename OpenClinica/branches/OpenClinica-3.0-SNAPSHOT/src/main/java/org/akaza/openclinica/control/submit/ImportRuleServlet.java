/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.rule.FileUploadHelper;
import org.akaza.openclinica.bean.rule.XmlSchemaValidationHelper;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.RulesPostImportContainerService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * Verify the Rule import , show records that have Errors as well as records
 * that will be saved.
 * 
 * @author Krikor krumlian
 */
public class ImportRuleServlet extends SecureController {
    private static final long serialVersionUID = 9116068126651934226L;
    protected final Logger log = LoggerFactory.getLogger(ImportRuleServlet.class);

    Locale locale;
    FileUploadHelper uploadHelper = new FileUploadHelper();
    XmlSchemaValidationHelper schemaValidator = new XmlSchemaValidationHelper();
    RulesPostImportContainerService rulesPostImportContainerService;

    @Override
    public void processRequest() throws Exception {
        String action = request.getParameter("action");

        if (StringUtil.isBlank(action)) {
            forwardPage(Page.IMPORT_RULES);

        }
        if ("downloadrulesxsd".equalsIgnoreCase(action)) {
            File xsdFile = new File(SpringServletAccess.getPropertiesDir(context) + "rules.xsd");
            dowloadFile(xsdFile, "text/xml");
        }
        if ("downloadtemplate".equalsIgnoreCase(action)) {
            File file = new File(SpringServletAccess.getPropertiesDir(context) + "rules_template.xml");
            dowloadFile(file, "text/xml");
        }
        if ("downloadtemplateWithNotes".equalsIgnoreCase(action)) {
            File file = new File(SpringServletAccess.getPropertiesDir(context) + "rules_template_with_notes.xml");
            dowloadFile(file, "text/xml");
        }
        if ("confirm".equalsIgnoreCase(action)) {

            try {
                File f = uploadHelper.returnFiles(request, context, getDirToSaveUploadedFileIn()).get(0);
                //File xsdFile = new File(getServletContext().getInitParameter("propertiesDir") + "rules.xsd");
                File xsdFile = new File(SpringServletAccess.getPropertiesDir(context) + "rules.xsd");

                schemaValidator.validateAgainstSchema(f, xsdFile);
                RulesPostImportContainer importedRules = handleLoadCastor(f);
                logger.info(ub.getFirstName());
                importedRules = getRulesPostImportContainerService().validateRuleDefs(importedRules);
                importedRules = getRulesPostImportContainerService().validateRuleSetDefs(importedRules);
                session.setAttribute("importedData", importedRules);
                provideMessage(importedRules);
                forwardPage(Page.VERIFY_RULES_IMPORT_SERVLET);
            } catch (OpenClinicaSystemException re) {
                MessageFormat mf = new MessageFormat("");
                mf.applyPattern(respage.getString("OCRERR_0016"));
                Object[] arguments = { re.getMessage() };
                addPageMessage(mf.format(arguments));
                forwardPage(Page.IMPORT_RULES);
            }
        }
    }

    private void provideMessage(RulesPostImportContainer rulesContainer) {
        int validRuleSetDefs = rulesContainer.getValidRuleSetDefs().size();
        int duplicateRuleSetDefs = rulesContainer.getDuplicateRuleSetDefs().size();
        int invalidRuleSetDefs = rulesContainer.getInValidRuleSetDefs().size();

        int validRuleDefs = rulesContainer.getValidRuleDefs().size();
        int duplicateRuleDefs = rulesContainer.getDuplicateRuleDefs().size();
        int invalidRuleDefs = rulesContainer.getInValidRuleDefs().size();

        if (validRuleSetDefs > 0 && duplicateRuleSetDefs == 0 && invalidRuleSetDefs == 0 && duplicateRuleDefs == 0 && invalidRuleDefs == 0) {
            addPageMessage(respage.getString("rules_Import_message1"));
        }
        if (duplicateRuleSetDefs > 0 && invalidRuleSetDefs == 0 && duplicateRuleDefs >= 0 && invalidRuleDefs == 0) {
            addPageMessage(respage.getString("rules_Import_message2"));
        }
        if (invalidRuleSetDefs > 0 && invalidRuleDefs >= 0) {
            addPageMessage(respage.getString("rules_Import_message3"));
        }
    }

    private String getDirToSaveUploadedFileIn() throws OpenClinicaSystemException {
        String dir = SQLInitServlet.getField("filePath");
        if (!new File(dir).exists()) {
            throw new OpenClinicaSystemException(respage.getString("filepath_you_defined_not_seem_valid"));
        }
        String theDir = dir + "rules" + File.separator + "original" + File.separator;
        return theDir;
    }

    private RulesPostImportContainer handleLoadCastor(File xmlFile) {

        RulesPostImportContainer ruleImport = null;
        try {
            // create an XMLContext instance
            XMLContext xmlContext = new XMLContext();
            // create and set a Mapping instance
            Mapping mapping = xmlContext.createMapping();
            mapping.loadMapping(SpringServletAccess.getPropertiesDir(context) + "mapping.xml");

            xmlContext.addMapping(mapping);
            // create a new Unmarshaller
            Unmarshaller unmarshaller = xmlContext.createUnmarshaller();
            unmarshaller.setClass(RulesPostImportContainer.class);
            // Create a Reader to the file to unmarshal from
            FileReader reader = new FileReader(xmlFile);
            ruleImport = (RulesPostImportContainer) unmarshaller.unmarshal(reader);
            ruleImport.initializeRuleDef();
            logRuleImport(ruleImport);
            return ruleImport;
        } catch (FileNotFoundException ex) {
            throw new OpenClinicaSystemException(ex.getMessage(), ex.getCause());
        } catch (IOException ex) {
            throw new OpenClinicaSystemException(ex.getMessage(), ex.getCause());
        } catch (MarshalException e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
        } catch (ValidationException e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
        } catch (MappingException e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
        }
    }

    private void logRuleImport(RulesPostImportContainer ruleImport) {
        logger.info("Total Number of RuleDefs Being imported : {} ", ruleImport.getRuleDefs().size());
        logger.info("Total Number of RuleAssignments Being imported : {} ", ruleImport.getRuleSets().size());
    }

    private RulesPostImportContainerService getRulesPostImportContainerService() {
        rulesPostImportContainerService =
            this.rulesPostImportContainerService != null ? rulesPostImportContainerService : (RulesPostImportContainerService) SpringServletAccess
                    .getApplicationContext(context).getBean("rulesPostImportContainerService");
        rulesPostImportContainerService.setCurrentStudy(currentStudy);
        rulesPostImportContainerService.setRespage(respage);
        rulesPostImportContainerService.setUserAccount(ub);
        return rulesPostImportContainerService;
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

    @Override
    public void mayProceed() throws InsufficientPermissionException {
        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }
        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR)) {
            return;
        }
        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }
}
