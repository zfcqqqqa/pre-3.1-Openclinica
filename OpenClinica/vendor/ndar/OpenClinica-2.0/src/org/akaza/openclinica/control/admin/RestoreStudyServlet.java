/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.util.ArrayList;
import java.util.Date;

import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 *
 * Processes the request of restoring a top level study, all the data
 * assoicated with this study will be restored
 */
public class RestoreStudyServlet  extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.STUDY_LIST_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {

    StudyDAO sdao = new StudyDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int studyId = fp.getInt("id");

   
    StudyBean study = (StudyBean) sdao.findByPK(studyId);
    //find all sites
    ArrayList sites = (ArrayList)sdao.findAllByParent(studyId);

    //find all user and roles in the study, include ones in sites
    UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
    ArrayList userRoles = udao.findAllByStudyId(studyId);

    //find all subjects in the study, include ones in sites
    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    ArrayList subjects = ssdao.findAllByStudy(study);

    //find all events in the study, include ones in sites
    StudyEventDefinitionDAO sefdao = new StudyEventDefinitionDAO(sm.getDataSource());
    ArrayList definitions = sefdao.findAllByStudy(study);

    String action = request.getParameter("action");
    if (studyId==0) {
      addPageMessage("Please choose a study to restore.");
      forwardPage(Page.STUDY_LIST_SERVLET);
    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute("studyToRestore", study);
        
        request.setAttribute("sitesToRestore", sites);

        request.setAttribute("userRolesToRestore", userRoles);

        request.setAttribute("subjectsToRestore", subjects);

        request.setAttribute("definitionsToRRestore", definitions);
        forwardPage(Page.RESTORE_STUDY);
      } else {
        logger.info("submit to restore the study");
        //change all statuses to unavailable
        StudyDAO studao = new StudyDAO(sm.getDataSource());
        study.setStatus(Status.AVAILABLE);
        study.setUpdater(ub);
        study.setUpdatedDate(new Date());
        studao.update(study);
        
        //remove all sites
        for (int i = 0; i < sites.size(); i++) {
          StudyBean site = (StudyBean) sites.get(i);
          site.setStatus(Status.AVAILABLE);
          site.setUpdater(ub);
          site.setUpdatedDate(new Date());
          sdao.update(site);
        }
        

        //remove all users and roles
        for (int i = 0; i < userRoles.size(); i++) {
          StudyUserRoleBean role = (StudyUserRoleBean) userRoles.get(i);
          role.setStatus(Status.AVAILABLE);
          role.setUpdater(ub);
          role.setUpdatedDate(new Date());
          udao.updateStudyUserRole(role, role.getUserName());
        }

        //remove all subjects
        for (int i = 0; i < subjects.size(); i++) {
          StudySubjectBean subject = (StudySubjectBean) subjects.get(i);
          subject.setStatus(Status.AVAILABLE);
          subject.setUpdater(ub);
          subject.setUpdatedDate(new Date());
          ssdao.update(subject);
        }

        //remove all study_group
        StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
        SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
        ArrayList groups = sgdao.findAllByStudy(study);
        for (int i = 0; i < groups.size(); i++) {
          StudyGroupBean group = (StudyGroupBean) groups.get(i);
          group.setStatus(Status.AVAILABLE);
          group.setUpdater(ub);
          group.setUpdatedDate(new Date());
          sgdao.update(group);
          //all subject_group_map
          ArrayList subjectGroupMaps = sgmdao.findAllByStudyGroupId(group.getId());
          for (int j = 0; j < subjectGroupMaps.size(); j++) {
            SubjectGroupMapBean sgMap = (SubjectGroupMapBean) subjectGroupMaps.get(j);
            sgMap.setStatus(Status.AVAILABLE);
            sgMap.setUpdater(ub);
            sgMap.setUpdatedDate(new Date());
            sgmdao.update(sgMap);
          }
        }

        //remove all event definitions and event
        EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
        StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
        for (int i = 0; i < definitions.size(); i++) {
          StudyEventDefinitionBean definition = (StudyEventDefinitionBean) definitions.get(i);
          definition.setStatus(Status.AVAILABLE);
          definition.setUpdater(ub);
          definition.setUpdatedDate(new Date());
          sefdao.update(definition);
          ArrayList edcs = (ArrayList) edcdao.findAllByDefinition(definition.getId());
          for (int j = 0; j < edcs.size(); j++) {
            EventDefinitionCRFBean edc = (EventDefinitionCRFBean) edcs.get(j);
            edc.setStatus(Status.AVAILABLE);
            edc.setUpdater(ub);
            edc.setUpdatedDate(new Date());
            edcdao.update(edc);
          }

          ArrayList events = (ArrayList) sedao.findAllByDefinition(definition.getId());
          EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());

          for (int j = 0; j < events.size(); j++) {
            StudyEventBean event = (StudyEventBean) events.get(j);
            event.setStatus(Status.AVAILABLE);
            event.setUpdater(ub);
            event.setUpdatedDate(new Date());
            sedao.update(event);

            ArrayList eventCRFs = ecdao.findAllByStudyEvent(event);

            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            for (int k = 0; k < eventCRFs.size(); k++) {
              EventCRFBean eventCRF = (EventCRFBean)eventCRFs.get(k);
              eventCRF.setStatus(Status.AVAILABLE);
              eventCRF.setUpdater(ub);
              eventCRF.setUpdatedDate(new Date());
              ecdao.update(eventCRF);

              ArrayList itemDatas = iddao.findAllByEventCRFId(eventCRF.getId());
              for (int a = 0; a < itemDatas.size(); a++) {
                ItemDataBean item = (ItemDataBean) itemDatas.get(a);
                item.setStatus(Status.AVAILABLE);
                item.setUpdater(ub);
                item.setUpdatedDate(new Date());
                iddao.update(item);
              }
            }
          }
        }//for definitions

        DatasetDAO datadao = new DatasetDAO(sm.getDataSource());
        ArrayList dataset = datadao.findAllByStudyId(study.getId());
        for (int i = 0; i < dataset.size(); i++) {
          DatasetBean data = (DatasetBean) dataset.get(i);
          data.setStatus(Status.AVAILABLE);
          data.setUpdater(ub);
          data.setUpdatedDate(new Date());
          datadao.update(data);
        }
        
        addPageMessage("This study has been restored successfully.");
        forwardPage(Page.STUDY_LIST_SERVLET);

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
