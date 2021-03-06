/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2009 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.hibernate.AuditUserLoginDao;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.Locale;

/**
 * Servlet for creating a table.
 * 
 * @author Krikor Krumlian
 */
public class AuditUserActivityServlet extends SecureController {

    private static final long serialVersionUID = 1L;
    private AuditUserLoginDao auditUserLoginDao;
    Locale locale;

    /*
     * (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();

        if (!ub.isSysAdmin()) {
            throw new InsufficientPermissionException(Page.MENU, resexception.getString("you_may_not_perform_administrative_functions"), "1");
        }

        return;
    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        AuditUserLoginTableFactory factory = new AuditUserLoginTableFactory();
        factory.setAuditUserLoginDao(getAuditUserLoginDao());
        String auditUserLoginHtml = factory.createTable(request, response).render();
        request.setAttribute("auditUserLoginHtml", auditUserLoginHtml);
        forwardPage(Page.AUDIT_USER_ACTIVITY);

    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }

    public AuditUserLoginDao getAuditUserLoginDao() {
        auditUserLoginDao =
            this.auditUserLoginDao != null ? auditUserLoginDao : (AuditUserLoginDao) SpringServletAccess.getApplicationContext(context).getBean(
                    "auditUserLoginDao");
        return auditUserLoginDao;
    }
}
