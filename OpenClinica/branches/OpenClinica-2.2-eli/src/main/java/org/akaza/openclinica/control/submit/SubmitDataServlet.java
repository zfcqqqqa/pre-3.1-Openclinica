/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.*;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.bean.core.*;
import org.akaza.openclinica.bean.login.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ssachs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubmitDataServlet extends SecureController {
	
	Locale locale;
	//<  ResourceBundleresexception,respage;
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#processRequest()
	 */
	protected void processRequest() throws Exception {
		forwardPage(Page.SUBMIT_DATA);
	}

	public static boolean maySubmitData(UserAccountBean ub, StudyUserRoleBean currentRole) {
		if (currentRole != null) {
			Role r = currentRole.getRole();
			if ((r != null) &&
					(r.equals(Role.COORDINATOR)
							|| r.equals(Role.STUDYDIRECTOR)
							|| r.equals(Role.INVESTIGATOR)
							|| r.equals(Role.RESEARCHASSISTANT))) {
				return true;
			}
		}

		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		
		  locale = request.getLocale();
		  //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);  
		  //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
		
		String exceptionName = resexception.getString("no_permission_to_submit_data");
		String noAccessMessage = respage.getString("may_not_enter_data_for_this_study") + respage.getString("change_study_contact_sysadmin");
		
		if (maySubmitData(ub, currentRole)) {
			return ;
		}
		
		addPageMessage(noAccessMessage);
		throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
	}
	
}
