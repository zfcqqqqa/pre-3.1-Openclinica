/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.managestudy.DisplayStudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.submit.SubmitDataServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 * 
 * Processes the reuqest of 'view study details'
 */
public class ViewStudyServlet extends SecureController {
    @Override
    public void processRequest() throws Exception {

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        FormProcessor fp = new FormProcessor(request);
        int studyId = fp.getInt("studyId");
        if (studyId == 0) {
            addPageMessage(respage.getString("please_choose_a_study_to_view"));
            forwardPage(Page.STUDY_LIST_SERVLET);
        } else {
            if (currentStudy.getId() != studyId && currentStudy.getParentStudyId() != studyId) {
                checkRoleByUserAndStudy(ub, studyId, 0);
            }

            String viewFullRecords = fp.getString("viewFull");
            StudyBean study = (StudyBean) sdao.findByPK(studyId);


            StudyConfigService scs = new StudyConfigService(sm.getDataSource());
            study = scs.setParametersForStudy(study);

            request.setAttribute("studyToView", study);
            if ("yes".equalsIgnoreCase(viewFullRecords)) {
                UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
                StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                ArrayList sites = new ArrayList();
                ArrayList userRoles = new ArrayList();
                ArrayList subjects = new ArrayList();
                if (this.currentStudy.getParentStudyId() > 0 && this.currentRole.getRole().getId() > 3) {
                    sites.add(this.currentStudy);
                    userRoles = udao.findAllUsersByStudy(currentStudy.getId());
                    subjects = ssdao.findAllByStudy(currentStudy);
                } else {
                    sites = (ArrayList) sdao.findAllByParent(studyId);
                    userRoles = udao.findAllUsersByStudy(studyId);
                    subjects = ssdao.findAllByStudy(study);
                }

                // find all subjects in the study, include ones in sites
                StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
                EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
               
                // find all events in the study, include ones in sites
                ArrayList definitions = seddao.findAllByStudy(study);

                for (int i = 0; i < definitions.size(); i++) {
                    StudyEventDefinitionBean def = (StudyEventDefinitionBean) definitions.get(i);
                    ArrayList crfs = (ArrayList) edcdao.findAllActiveParentsByEventDefinitionId(def.getId());
                    def.setCrfNum(crfs.size());

                }

                request.setAttribute("sitesToView", sites);
                request.setAttribute("siteNum", sites.size() + "");

                request.setAttribute("userRolesToView", userRoles);
                request.setAttribute("userNum", userRoles.size() + "");

                // request.setAttribute("subjectsToView", displayStudySubs);
                // request.setAttribute("subjectNum", subjects.size() + "");

                request.setAttribute("definitionsToView", definitions);
                request.setAttribute("defNum", definitions.size() + "");
                forwardPage(Page.VIEW_FULL_STUDY);

            } else {
                forwardPage(Page.VIEW_STUDY);
            }
        }
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }

}
