/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionRow;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Processes user reuqest to generate study event definition list
 * 
 * @author jxu 
 * 
 */
public class ListEventDefinitionServlet extends SecureController {
	
	Locale locale;
	//<  ResourceBundleresword, resworkflow, respage,resexception;
	
	/**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {

	locale = request.getLocale();
	//< resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);
	//< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);  
	//< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
	//< resworkflow = ResourceBundle.getBundle("org.akaza.openclinica.i18n.workflow",locale);
	
    if (ub.isSysAdmin()) {
      return;
    }

    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }

    addPageMessage(respage.getString("no_have_correct_privilege_current_study")
			+ respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET,
    		resexception.getString("not_study_director"), "1");


  }

  /**
   * Processes the request
   */
  public void processRequest() throws Exception {

    StudyEventDefinitionDAO edao = new StudyEventDefinitionDAO(sm.getDataSource());
    UserAccountDAO sdao = new UserAccountDAO(sm.getDataSource());
    ArrayList seds = (ArrayList) edao.findAllByStudy(currentStudy);

    //request.setAttribute("seds", seds);
    
    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
    for (int i = 0; i < seds.size(); i++) {
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seds.get(i);
      if (sed.getUpdater().getId()==0){
        sed.setUpdater(sed.getOwner());
        sed.setUpdatedDate(sed.getCreatedDate());
      }
      if (isLockable(sed,sedao,ecdao,iddao)) {
        sed.setLockable(true);
      } 
      if (isPopulated(sed,sedao)) {
        sed.setPopulated(true);
      } 
    }
    
    FormProcessor fp = new FormProcessor(request);
    EntityBeanTable table = fp.getEntityBeanTable();
    ArrayList allStudyRows = StudyEventDefinitionRow.generateRowsFromBeans(seds);
    
    String[] columns = {resword.getString("order"),resword.getString("name"), 
    		resword.getString("repeating"), resword.getString("type"),
    		resword.getString("category"), resword.getString("populated"), 
    		resword.getString("date_updated"),resword.getString("actions") };
	table.setColumns(new ArrayList(Arrays.asList(columns)));
	table.hideColumnLink(5);
	table.hideColumnLink(7);
	table.setQuery("ListEventDefinition", new HashMap());
	table.addLink(resworkflow.getString("create_a_new_study_event_definition"),"DefineStudyEvent");
	
	table.setRows(allStudyRows);
	table.computeDisplay();

	
	request.setAttribute("table", table);
	request.setAttribute("defSize", new Integer(seds.size()));

    forwardPage(Page.STUDY_EVENT_DEFINITION_LIST);
  }
  
  
  /**
   * Checked whether a definition is available to be locked
   * @param sed
   * @return
   */
  private boolean isPopulated(StudyEventDefinitionBean sed, StudyEventDAO sedao) {
      
      
    //checks study event
    ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());
    for (int j = 0; j < events.size(); j++) {
      StudyEventBean event = (StudyEventBean) events.get(j);
      if (!(event.getStatus().equals(Status.DELETED)) && !(event.getStatus().equals(Status.AUTO_DELETED)) ) {
         return true;
      }     
    }
    return false;
  }

  /**
   * Checked whether a definition is available to be locked
   * @param sed
   * @return
   */
  private boolean isLockable(StudyEventDefinitionBean sed, StudyEventDAO sedao,
      EventCRFDAO ecdao,ItemDataDAO iddao) {
      
      
    //checks study event
    ArrayList events = (ArrayList) sedao.findAllByDefinition(sed.getId());
    for (int j = 0; j < events.size(); j++) {
      StudyEventBean event = (StudyEventBean) events.get(j);
      if (!(event.getStatus().equals((Status.AVAILABLE)) || event.getStatus().equals(
          (Status.DELETED)))) {
         return false;
      }     
      
      
      ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);
    
      for (int k = 0; k < eventCRFs.size(); k++) {
        EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
        if (!(eventCRF.getStatus().equals((Status.UNAVAILABLE))||
            eventCRF.getStatus().equals((Status.DELETED)))){
          return false;
        }
       

        ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
        for (int a = 0; a < itemDatas.size(); a++) {
          ItemDataBean item = (ItemDataBean) itemDatas.get(a);
          if (!(item.getStatus().equals((Status.UNAVAILABLE))||
              item.getStatus().equals((Status.DELETED)))){
            return false;
          }
         
        }
      }
    }
    
    return true;
  }

}
