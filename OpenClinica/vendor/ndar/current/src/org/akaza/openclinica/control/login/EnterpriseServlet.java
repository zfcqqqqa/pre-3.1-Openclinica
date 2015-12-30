/*
 * Created on Sep 28, 2005
 *
 * 
 */
package org.akaza.openclinica.control.login;

import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author thickerson
 *
 * 
 */
public class EnterpriseServlet extends SecureController {
	  
	  public void mayProceed() throws InsufficientPermissionException {
	  
	  }

	  public void processRequest() throws Exception {
	  	resetPanel();
	  	panel.setStudyInfoShown(false);
	  	panel.setOrderedData(true);
	  	setToPanel("","");
	    forwardPage(Page.ENTERPRISE);
	  }

}
