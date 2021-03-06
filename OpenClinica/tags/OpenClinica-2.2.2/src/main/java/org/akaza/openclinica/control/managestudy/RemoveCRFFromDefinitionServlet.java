/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.*;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.*;

/**
 * Remove the reference to a CRF from a study event definition
 * 
 * @author jxu 
 */
public class RemoveCRFFromDefinitionServlet extends SecureController {
  
  /**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
  	if (ub.isSysAdmin()) {
  		return ;
  	}
  	if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
         || currentRole.getRole().equals(Role.COORDINATOR)) {
       return;
    }

    addPageMessage(respage.getString("no_have_permission_to_update_study_event_definition")
            + respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET, resexception.getString("not_study_director"), "1");


  }

  public void processRequest() throws Exception {   
    ArrayList edcs = (ArrayList)session.getAttribute("eventDefinitionCRFs");
    ArrayList updatedEdcs = new ArrayList();
    String crfName = "";
    if (edcs != null && edcs.size()>1) {
      String idString = request.getParameter("id");
      logger.info("crf id:" + idString);
      if (StringUtil.isBlank(idString)) {
        addPageMessage(respage.getString("please_choose_a_crf_to_remove"));
        forwardPage(Page.UPDATE_EVENT_DEFINITION1);
      } else {
        //crf id        
        int id = Integer.valueOf(idString.trim()).intValue();
        for (int i =0; i< edcs.size(); i++){
          EventDefinitionCRFBean edc = (EventDefinitionCRFBean)edcs.get(i);
          if (edc.getCrfId() == id) {
            edc.setStatus(Status.DELETED);
            crfName=edc.getCrfName();
          }  
          if (edc.getId()>0 || !edc.getStatus().equals(Status.DELETED)){
            updatedEdcs.add(edc);
            System.out.println("\nversion:" + edc.getDefaultVersionId());
          }
        }     
        session.setAttribute("eventDefinitionCRFs", updatedEdcs);
        addPageMessage(crfName + " " + respage.getString("has_been_removed"));
        forwardPage(Page.UPDATE_EVENT_DEFINITION1);
      }
      
    } else {
      addPageMessage(respage.getString("an_ED_needs_to_have_least_one_CRF"));
      forwardPage(Page.UPDATE_EVENT_DEFINITION1);
    }
  }
}
      
