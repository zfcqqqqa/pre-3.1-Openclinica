/* OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.domain.rule.action.RuleActionComparator;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletOutputStream;

public class DownloadRuleSetXmlServlet extends SecureController {

    protected final Logger log = LoggerFactory.getLogger(DownloadRuleSetXmlServlet.class);
    private static final long serialVersionUID = 5381321212952389008L;
    RuleSetServiceInterface ruleSetService;

    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, resexception.getString("not_study_director"), "1");

    }

    private FileWriter handleLoadCastor(FileWriter writer, RulesPostImportContainer rpic) {

        try {
            // Create Mapping
            Mapping mapping = new Mapping();
            mapping.loadMapping(SpringServletAccess.getPropertiesDir(context) + "mappingMarshaller.xml");
            // Create XMLContext
            XMLContext xmlContext = new XMLContext();
            xmlContext.addMapping(mapping);

            Marshaller marshaller = xmlContext.createMarshaller();
            marshaller.setWriter(writer);
            marshaller.marshal(rpic);
            return writer;

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
        } catch (Exception e) {
            throw new OpenClinicaSystemException(e.getMessage(), e.getCause());
        }
    }

    private RulesPostImportContainer prepareRulesPostImportRuleSetContainer(String ruleSetId) {
        RulesPostImportContainer rpic = new RulesPostImportContainer();

        RuleSetBean ruleSet = getRuleSetService().getRuleSetDao().findById(Integer.valueOf(ruleSetId), currentStudy);
        rpic.addRuleSet(ruleSet);
        for (RuleSetRuleBean ruleSetRule : ruleSet.getRuleSetRules()) {
            rpic.addRuleDef(ruleSetRule.getRuleBean());
        }
        return rpic;
    }

    private RulesPostImportContainer prepareRulesPostImportRuleSetRuleContainer(String ruleSetRuleIds) {
        HashMap<Integer, RuleSetBean> ruleSets = new HashMap<Integer, RuleSetBean>();
        HashSet<RuleBean> rules = new HashSet<RuleBean>();
        RulesPostImportContainer rpic = new RulesPostImportContainer();

        String[] splitExpression = ruleSetRuleIds.split(",");
        for (String string : splitExpression) {
            RuleSetRuleBean rsr = getRuleSetService().getRuleSetRuleDao().findById(Integer.valueOf(string));
            Collections.sort(rsr.getActions(), new RuleActionComparator());
            Integer key = rsr.getRuleSetBean().getId();
            if (ruleSets.containsKey(key)) {
                RuleSetBean rs = ruleSets.get(key);
                rs.setTarget(rsr.getRuleSetBean().getTarget());
                rs.addRuleSetRule(rsr);
            } else {
                RuleSetBean rs = new RuleSetBean();
                rs.setTarget(rsr.getRuleSetBean().getTarget());
                rs.addRuleSetRule(rsr);
                ruleSets.put(key, rs);
            }
            rules.add(rsr.getRuleBean());
        }

        for (Map.Entry<Integer, RuleSetBean> entry : ruleSets.entrySet()) {
            rpic.addRuleSet(entry.getValue());
        }
        for (RuleBean theRule : rules) {
            rpic.addRuleDef(theRule);
        }
        return rpic;
    }

    @Override
    public void processRequest() throws Exception {

        // String ruleSetId = request.getParameter("ruleSetId");
        String ruleSetRuleIds = request.getParameter("ruleSetRuleIds");

        String dir = SQLInitServlet.getField("filePath") + "rules" + File.separator;
        Long time = System.currentTimeMillis();
        File f = new File(dir + "rules" + currentStudy.getOid() + "-" + time + ".xml");
        FileWriter writer = new FileWriter(f);
        handleLoadCastor(writer, prepareRulesPostImportRuleSetRuleContainer(ruleSetRuleIds));

        response.setHeader("Content-disposition", "attachment; filename=\"" + "rules" + currentStudy.getOid() + "-" + time + ".xml" + "\";");
        response.setContentType("text/xml");
        response.setHeader("Pragma", "public");

        ServletOutputStream op = response.getOutputStream();

        DataInputStream in = null;
        try {
            response.setContentType("text/xml");
            response.setHeader("Pragma", "public");
            response.setContentLength((int) f.length());

            byte[] bbuf = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            int length;
            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                op.write(bbuf, 0, length);
            }

            in.close();
            op.flush();
            op.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (op != null) {
                op.close();
            }
        }

    }

    private RuleSetServiceInterface getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess.getApplicationContext(context).getBean(
                    "ruleSetService");
        // TODO: Add getRequestURLMinusServletPath(),getContextPath()
        return ruleSetService;
    }
}
