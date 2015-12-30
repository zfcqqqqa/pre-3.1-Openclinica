/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplayTableOfContentsBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.TableOfContentsServlet;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * Views the content of an event CRF
 */
public class ViewEventCRFContentServlet extends SecureController {
  
	public static final String BEAN_STUDY_EVENT = "studyEvent";
	
	
	/**
   * Checks whether the user has the correct privilege
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)
        || currentRole.getRole().equals(Role.INVESTIGATOR)
		|| currentRole.getRole().equals(Role.RESEARCHASSISTANT)) {
      return;
    }    
    
    addPageMessage("You don't have correct privilege in your current active study. "
         + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "no permission", "1");
   
  }
  
  /*
   *  Get the Study Event to display on screen as well as
   *  print some of its information. 
   *  Krikor 10/19/2006
   */
  private StudyEventBean getStudyEvent(int eventId) throws Exception {

		StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
		StudyBean studyWithSED = currentStudy;
		if (currentStudy.getParentStudyId() > 0) {
			studyWithSED = new StudyBean();
			studyWithSED.setId(currentStudy.getParentStudyId());
		}

		AuditableEntityBean aeb = sedao.findByPKAndStudy(eventId, studyWithSED);

		if (!aeb.isActive()) {
			addPageMessage("The study event for which you are attempting to enter data does not belong to the current study.");
			throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET,
					"study event does not belong to the current study", "1");
		}

		StudyEventBean seb = (StudyEventBean) aeb;

		StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm
				.getDataSource());
		StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) seddao
				.findByPK(seb.getStudyEventDefinitionId());
		seb.setStudyEventDefinition(sedb);
		return seb;
	}
  
  public void processRequest() throws Exception { 
    FormProcessor fp = new FormProcessor(request);
    int eventCRFId = fp.getInt("ecId",true);
    int studySubId = fp.getInt("id",true);
    int eventId    = fp.getInt("eventId",true);
    if (eventCRFId==0) {
      addPageMessage("Please choose an Event CRF to view.");
      forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      return;
    }
    
    StudyEventBean seb = getStudyEvent(eventId);
    
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
    StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
    request.setAttribute("studySub", studySub);
    
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    EventCRFBean eventCRF = (EventCRFBean)ecdao.findByPK(eventCRFId);
    DisplayTableOfContentsBean displayBean = TableOfContentsServlet.getDisplayBean(eventCRF,sm.getDataSource(),currentStudy);
    request.setAttribute("toc",displayBean);
    request.getSession().setAttribute(BEAN_STUDY_EVENT, seb);
    forwardPage(Page.VIEW_EVENT_CRF_CONTENT);
    
    
  }

}