/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.rule.AuditableBeanWrapper;
import org.akaza.openclinica.bean.rule.RuleBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.RulesPostImportContainer;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.service.rule.RuleService;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;

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
    RuleService ruleService;
    RuleSetService ruleSetService;

    @Override
    public void processRequest() throws Exception {

        String action = request.getParameter("action");
        FormProcessor fp = new FormProcessor(request);

        // checks which module the requests are from
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

        if ("confirm".equalsIgnoreCase(action)) {
            // session.setAttribute("crf", new CRFBean());
            RulesPostImportContainer rulesContainer = (RulesPostImportContainer) session.getAttribute("importedData");
            logger.info("Size of ruleDefs : " + rulesContainer.getRuleDefs().size());
            logger.info("Size of ruleSets : " + rulesContainer.getRuleSets().size());
            forwardPage(Page.VERIFY_RULES_IMPORT);
        }

        if ("save".equalsIgnoreCase(action)) {
            RulesPostImportContainer rulesContainer = (RulesPostImportContainer) session.getAttribute("importedData");
            // List<RuleBeanWrapper> ruleBeanWrapperList =
            // rulesContainer.getValidRuleDefs();
            for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getValidRuleDefs()) {
                getRuleService().saveRule(ruleBeanWrapper.getAuditableBean());
            }
            for (AuditableBeanWrapper<RuleBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleDefs()) {
                getRuleService().updateRule(ruleBeanWrapper.getAuditableBean());
            }

            for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getValidRuleSetDefs()) {
                getRuleSetService().saveRuleSet(ruleBeanWrapper.getAuditableBean());
            }

            for (AuditableBeanWrapper<RuleSetBean> ruleBeanWrapper : rulesContainer.getDuplicateRuleSetDefs()) {
                getRuleSetService().replaceRuleSet(ruleBeanWrapper.getAuditableBean());
            }

            forwardPage(Page.LIST_RULE_SETS_SERVLET);
        }
    }

    private RuleService getRuleService() {
        ruleService = this.ruleService != null ? ruleService : new RuleService(sm.getDataSource());
        return ruleService;

    }

    private RuleSetService getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : new RuleSetService(sm.getDataSource(), getRequestURLMinusServletPath(), getContextPath());
        return ruleSetService;

    }

    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        if (ub.isSysAdmin()) {
            return;
        }

        Role r = currentRole.getRole();
        if (r.equals(Role.STUDYDIRECTOR) || r.equals(Role.COORDINATOR) || r.equals(Role.INVESTIGATOR) || r.equals(Role.RESEARCHASSISTANT)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
    }
}