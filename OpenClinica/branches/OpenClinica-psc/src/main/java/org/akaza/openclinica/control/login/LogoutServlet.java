/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.login;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * @version CVS: $Id: LogoutServlet.java 11428 2008-07-21 14:46:23Z kkrumlian $
 *
 * Performs Log out action
 */
public class LogoutServlet extends SecureController {

    @Override
    public void mayProceed() throws InsufficientPermissionException {

    }

    @Override
    public void processRequest() throws Exception {
        sm = null;// set sm to null after user logs out
        logger.info("User  : {} , email address : {} Logged Out ", ub.getName(), ub.getEmail());
        session.removeAttribute("userBean");
        session.removeAttribute("study");
        session.removeAttribute("userRole");
        session.removeAttribute("passwordExpired");
        session.invalidate();
        // forwardPage is set to false to avoid checking the session, tbh
        // 01.2005
        // forwardPage(Page.MENU, false);
        forwardPage(Page.LOGOUT, false);
    }

}