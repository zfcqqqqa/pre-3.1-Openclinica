/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.mail.MessagingException;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.DisplayStudyEventBean;
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
 * @author jxu
 * 
 * Removes a study event and all its related event CRFs, items
 */
public class RemoveStudyEventServlet extends SecureController {
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

    addPageMessage("You don't have correct privilege in your current active study. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.MENU_SERVLET, "not study director", "1");

  }

  public void processRequest() throws Exception {
    FormProcessor fp = new FormProcessor(request);
    int studyEventId = fp.getInt("id");//studyEventId
    int studySubId = fp.getInt("studySubId");//studySubjectId

    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    StudySubjectDAO subdao = new StudySubjectDAO(sm.getDataSource());

    if (studyEventId == 0) {
      addPageMessage("Please choose a study event to remove.");
      request.setAttribute("id", new Integer(studySubId).toString());
      forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
    } else {

      StudyEventBean event = (StudyEventBean) sedao.findByPK(studyEventId);

      StudySubjectBean studySub = (StudySubjectBean) subdao.findByPK(studySubId);
      request.setAttribute("studySub", studySub);

      StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
      StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event
          .getStudyEventDefinitionId());
      event.setStudyEventDefinition(sed);

      StudyDAO studydao = new StudyDAO(sm.getDataSource());
      StudyBean study = (StudyBean) studydao.findByPK(studySub.getStudyId());
      request.setAttribute("study", study);

      String action = request.getParameter("action");
      if ("confirm".equalsIgnoreCase(action)) {
        if (!event.getStatus().equals(Status.AVAILABLE)) {
          addPageMessage("This event is not available for this study."
              + " Please contact the System Administrator for more information.");
          request.setAttribute("id", new Integer(studySubId).toString());
          forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
          return;
        }

        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        //find all crfs in the definition
        ArrayList eventDefinitionCRFs = (ArrayList) edcdao.findAllByEventDefinitionId(sed.getId());

        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
        ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

        //construct info needed on view study event page
        DisplayStudyEventBean de = new DisplayStudyEventBean();
        de.setStudyEvent(event);
        de.setDisplayEventCRFs(getDisplayEventCRFs(eventCRFs, eventDefinitionCRFs));

        request.setAttribute("displayEvent", de);

        forwardPage(Page.REMOVE_STUDY_EVENT);
      } else {
        logger.info("submit to remove the event from study");
        //remove event from study

        event.setStatus(Status.DELETED);
        event.setUpdater(ub);
        event.setUpdatedDate(new Date());
        sedao.update(event);

        //remove all event crfs
        EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

        ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

        ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
        for (int k = 0; k < eventCRFs.size(); k++) {
          EventCRFBean eventCRF = (EventCRFBean) eventCRFs.get(k);
          eventCRF.setStatus(Status.DELETED);
          eventCRF.setUpdater(ub);
          eventCRF.setUpdatedDate(new Date());
          ecdao.update(eventCRF);
          //remove all the item data
          ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
          for (int a = 0; a < itemDatas.size(); a++) {
            ItemDataBean item = (ItemDataBean) itemDatas.get(a);
            item.setStatus(Status.DELETED);
            item.setUpdater(ub);
            item.setUpdatedDate(new Date());
            iddao.update(item);
          }
        }

        String emailBody = "The Event " + event.getStudyEventDefinition().getName()
            + " has been removed from the Subject Record for " + studySub.getLabel() 
            + " in the Study " + study.getName() + ".";

        addPageMessage(emailBody);
        sendEmail(emailBody);
        request.setAttribute("id", new Integer(studySubId).toString());
        forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
      }
    }
  }

  /**
   * Each of the event CRFs with its corresponding CRFBean. Then generates a
   * list of DisplayEventCRFBeans, one for each event CRF.
   * 
   * @param eventCRFs
   *          The list of event CRFs for this study event.
   * @param eventDefinitionCRFs
   *          The list of event definition CRFs for this study event.
   * @return The list of DisplayEventCRFBeans for this study event.
   */
  private ArrayList getDisplayEventCRFs(ArrayList eventCRFs, ArrayList eventDefinitionCRFs) {
    ArrayList answer = new ArrayList();

    HashMap definitionsById = new HashMap();
    int i;
    for (i = 0; i < eventDefinitionCRFs.size(); i++) {
      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) eventDefinitionCRFs.get(i);
      definitionsById.put(new Integer(edc.getStudyEventDefinitionId()), edc);
    }

    StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());

    for (i = 0; i < eventCRFs.size(); i++) {
      EventCRFBean ecb = (EventCRFBean) eventCRFs.get(i);

      // populate the event CRF with its crf bean
      int crfVersionId = ecb.getCRFVersionId();
      CRFBean cb = cdao.findByVersionId(crfVersionId);
      ecb.setCrf(cb);

      CRFVersionBean cvb = (CRFVersionBean) cvdao.findByPK(crfVersionId);
      ecb.setCrfVersion(cvb);

      // then get the definition so we can call DisplayEventCRFBean.setFlags
      int studyEventId = ecb.getStudyEventId();
      int studyEventDefinitionId = sedao.getDefinitionIdFromStudyEventId(studyEventId);

      EventDefinitionCRFBean edc = (EventDefinitionCRFBean) definitionsById.get(new Integer(
          studyEventDefinitionId));

      DisplayEventCRFBean dec = new DisplayEventCRFBean();
      dec.setFlags(ecb, ub, currentRole, edc.isDoubleEntry());
      answer.add(dec);
    }

    return answer;
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
      em.process(ub.getEmail().trim(), EmailEngine.getAdminEmail(), "Remove Event from Study",
          emailBody);

      em = null;
      em = new EmailEngine(EmailEngine.getSMTPHost());
      //to admin
      em.process(EmailEngine.getAdminEmail(), EmailEngine.getAdminEmail(),
          "Remove Event From Study", emailBody);
      logger.info("Sending email done..");
    } catch (MessagingException me) {
      addPageMessage(" Mail cannot be sent to Administrator due to mail server connection problem.");
    }
  }

}