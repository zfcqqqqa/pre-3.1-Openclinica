/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SectionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SectionDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Removes a crf version 
 * 
 * @author jxu
 *
 */
public class RemoveCRFVersionServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage(respage.getString("no_have_correct_privilege_current_study")
            + respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_admin"), "1");


  }
  
  public void processRequest() throws Exception {

    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int versionId = fp.getInt("id",true);    

    String action = fp.getString("action");
    if (versionId==0) {
      addPageMessage(respage.getString("please_choose_a_CRF_version_to_remove"));
      forwardPage(Page.CRF_LIST_SERVLET);
    } else {
      if (StringUtil.isBlank(action)) {
        addPageMessage(respage.getString("no_action_specified"));
        forwardPage(Page.CRF_LIST_SERVLET);
        return;
      }
      CRFVersionBean version = (CRFVersionBean)cvdao.findByPK(versionId);    
      
      SectionDAO secdao = new SectionDAO(sm.getDataSource());           
      
      EventCRFDAO evdao = new EventCRFDAO(sm.getDataSource());
      //find all event crfs by version id
      ArrayList eventCRFs = evdao.findAllByCRFVersion(versionId);
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("versionToRemove", version);
        request.setAttribute("eventCRFs", eventCRFs);        
        forwardPage(Page.REMOVE_CRF_VERSION);
      } else {
        logger.info("submit to remove the crf version");  
        //version
        version.setStatus(Status.DELETED);
        version.setUpdater(ub);
        version.setUpdatedDate(new Date());
        cvdao.update(version);      
        //added below tbh 092007, seems that we don't remove the event crfs in the second pass
        for (int ii=0; ii<eventCRFs.size(); ii++) {
        	EventCRFBean ecbean = (EventCRFBean)eventCRFs.get(ii);
        	ecbean.setStatus(Status.AUTO_DELETED);
        	ecbean.setUpdater(ub);
        	ecbean.setUpdatedDate(new Date());
        	evdao.update(ecbean);
        }
        //added above tbh 092007, to fix task
        //all sections  
        ArrayList sections = secdao.findAllByCRFVersionId(version.getId());
        for (int j=0; j<sections.size(); j++) {
          SectionBean section = (SectionBean)sections.get(j);
          if (!section.getStatus().equals(Status.DELETED)){
            section.setStatus(Status.AUTO_DELETED);
            section.setUpdater(ub);
            section.setUpdatedDate(new Date());
            secdao.update(section);
          }
        }
       
        //all item data related to event crfs
        ItemDataDAO idao = new ItemDataDAO(sm.getDataSource());
        for (int i=0; i<eventCRFs.size();i++){
          EventCRFBean eventCRF= (EventCRFBean)eventCRFs.get(i);
          if (!eventCRF.getStatus().equals(Status.DELETED)){
          eventCRF.setStatus(Status.AUTO_DELETED);
          eventCRF.setUpdater(ub);
          eventCRF.setUpdatedDate(new Date());
          evdao.update(eventCRF);
          
          ArrayList items = idao.findAllByEventCRFId(eventCRF.getId());
          for (int j=0; j<items.size();j++){
            ItemDataBean item =(ItemDataBean)items.get(j);
            if (!item.getStatus().equals(Status.DELETED)){
              item.setStatus(Status.AUTO_DELETED);
              item.setUpdater(ub);
              item.setUpdatedDate(new Date());
              idao.update(item);
            }
           }   
         }
        }
        
        addPageMessage(respage.getString("the_CRF") + version.getName() + " "+ respage.getString("has_been_removed_succesfully"));
        forwardPage(Page.CRF_LIST_SERVLET);

      }
    }

  }
  
  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }


}
