/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.service.rule.RuleSetService;
import org.akaza.openclinica.view.Page;

/**
 * @author Krikor Krumlian
 */
public class ViewRuleSetServlet extends SecureController {

    private static String RULESET_ID = "ruleSetId";
    private static String RULESET = "ruleSet";
    private RuleSetService ruleSetService;

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
        throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

    }

    @Override
    public void processRequest() throws Exception {

        String ruleSetId = request.getParameter(RULESET_ID);
        if (ruleSetId == null) {
            addPageMessage(respage.getString("please_choose_a_CRF_to_view"));
            forwardPage(Page.CRF_LIST);
        } else {
            RuleSetBean ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId, null);
            request.setAttribute(RULESET, ruleSetBean);
            forwardPage(Page.VIEW_RULES);
        }
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

    private RuleSetService getRuleSetService() {
        ruleSetService =
            this.ruleSetService != null ? ruleSetService : new RuleSetService(sm.getDataSource(), getRequestURLMinusServletPath(), getContextPath());
        return ruleSetService;
    }

}
