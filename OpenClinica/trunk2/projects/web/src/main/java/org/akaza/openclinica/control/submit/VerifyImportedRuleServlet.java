/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.domain.rule.RulesPostImportContainer;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * View the uploaded data and verify what is going to be saved into the system
 * and what is not.
 * 
 * @author Krikor Krumlian
 */
@SuppressWarnings("serial")
public class VerifyImportedRuleServlet extends SecureController {

    Locale locale;
    RuleSetServiceInterface ruleSetService;

    @Override
    public void processRequest() throws Exception {

        String action = request.getParameter("action");
        FormProcessor fp = new FormProcessor(request);

        // checks which module the requests are from
        /*
        String module = fp.getString(MODULE);
        request.setAttribute(MODULE, module);

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);

        setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"));
        setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"));
        setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"));
        setToPanel(resword.getString("CRF_spreadsheet_template"), respage.getString("br_download_blank_CRF_spreadsheet_from"));
        setToPanel(resword.getString("example_CRF_br_spreadsheets"), respage.getString("br_download_example_CRF_instructions_from"));
        */

        if ("confirm".equalsIgnoreCase(action)) {
            // session.setAttribute("crf", new CRFBean());
            RulesPostImportContainer rulesContainer = (RulesPostImportContainer) session.getAttribute("importedData");
            logger.info("Size of ruleDefs : " + rulesContainer.getRuleDefs().size());
            logger.info("Size of ruleSets : " + rulesContainer.getRuleSets().size());
            forwardPage(Page.VERIFY_RULES_IMPORT);
        }

        if ("save".equalsIgnoreCase(action)) {
            RulesPostImportContainer rulesContainer = (RulesPostImportContainer) session.getAttribute("importedData");
            getRuleSetService().saveImport(rulesContainer);
            MessageFormat mf = new MessageFormat("");
            mf.applyPattern(resword.getString("successful_rule_upload"));

            Object[] arguments =
                { rulesContainer.getValidRuleDefs().size() + rulesContainer.getDuplicateRuleDefs().size(),
                    rulesContainer.getValidRuleSetDefs().size() + rulesContainer.getDuplicateRuleSetDefs().size() };
            addPageMessage(mf.format(arguments));
            ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);
            session.setAttribute("pageMessages", pageMessages);
            response.sendRedirect(request.getContextPath() + Page.MANAGE_STUDY_MODULE);
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