/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.DisplayEventCRFBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Removes an Event CRF
 * 
 * @author jxu
 *  
 */
public class RemoveEventCRFServlet extends SecureController {
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
    throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int eventCRFId = fp.getInt("id");//eventCRFId
    int studySubId = fp.getInt("studySubId");//studySubjectId

    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

    if (eventCRFId == 0) {
      addPageMessage(respage.getString("please_choose_an_event_CRF_to_remove"));
      request.setAttribute("id", new Integer(studySubId).toString());
      forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
    } else {
      EventCRFBean eventCRF = (EventCRFBean) ecdao.findByPK(eventCRFId);

      StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
      request.setAttribute("studySub", studySub);

      //construct info needed on view event crf page
      CRFDAO cdao = new CRFDAO(sm.getDataSource());
      CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

      int crfVersionId = eventCRF.getCRFVersionId();
      CRFBean cb = cdao.findByVersionId(crfVersionId);
      eventCRF.setCrf(cb);

      CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
      eventCRF.setCrfVersion(cvb);

      // then get the definition so we can call DisplayEventCRFBean.setFlags
      int studyEventId = eventCRF.getStudyEventId();

      StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

      int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);
      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao
          .findByPK(studyEventDefinitionId);
      event.setStudyEventDefinition(sed);
      request.setAttribute("event", event);

      EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());

      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcdao
          .findByStudyEventDefinitionIdAndCRFId(studyEventDefinitionId, cb.getId());

      DisplayEventCRFBean dec = new DisplayEventCRFBean();
      dec.setEventCRF(eventCRF);
      dec.setFlags(eventCRF, ub, currentRole, edc.isDoubleEntry());

      //find all item data
      ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());

      ArrayList itemData = iddao.findAllByEventCRFId(eventCRF.getId());

      request.setAttribute("items", itemData);

      String action = request.getParameter("action");
      if ("confirm".equalsIgnoreCase(action)) {
        if (eventCRF.getStatus().equals(Status.DELETED) || eventCRF.getStatus().equals(Status.AUTO_DELETED) ) {
          addPageMessage(respage.getString("this_event_CRF_is_removed_for_this_study")
              + (" ") + respage.getString("please_contact_sysadmin_for_more_information"));
          request.setAttribute("id", new Integer(studySubId).toString());
          forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
          return;
        }

        request.setAttribute("displayEventCRF", dec);

        forwardPage(Page.REMOVE_EVENT_CRF);
      } else {
        logger.info("submit to remove the event CRF from study");

        eventCRF.setStatus(Status.DELETED);
        eventCRF.setUpdater(ub);
        eventCRF.setUpdatedDate(new Date());
        ecdao.update(eventCRF);

        //remove all the item data
        for (int a = 0; a < itemData.size(); a++) {
          ItemDataBean item = (ItemDataBean) itemData.get(a);
          if(!item.getStatus().equals(Status.DELETED)){
          item.setStatus(Status.AUTO_DELETED);
          item.setUpdater(ub);
          item.setUpdatedDate(new Date());
          iddao.update(item);
          }
        }

        String emailBody = respage.getString("the_event_CRF")+" "+
        		cb.getName() + (" ") + respage.getString("has_been_removed_from_the_event") 
            + event.getStudyEventDefinition().getName() + ".";

        addPageMessage(emailBody);
        sendEmail(emailBody);
        request.setAttribute("id", new Integer(studySubId).toString());
        forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      }
    }
  }

  /**
   * Send email to director and administrator
   * 
   * @param request
   * @param response
   */
  private void sendEmail(String emailBody) throws Exception {

    logger.info("Sending email...");
    try {
      EmailEngine em = new EmailEngine(EmailEngine.getSMTPHost());
      //to study director
      em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(), respage.getString("remove_event_CRF_from_event"),
          emailBody);

      em = null;
      em = new EmailEngine(EmailEngine.getSMTPHost());
      //to admin
      em.process(EmailEngine.getAdminEmail(), EmailEngine.getAdminEmail(),
    		  respage.getString("remove_event_CRF_from_event"), emailBody);
      logger.info("Sending email done..");
    } catch (MessagingException me) {
      addPageMessage(respage.getString("mail_cannot_be_sent_to_admin"));
    }
  }

}
