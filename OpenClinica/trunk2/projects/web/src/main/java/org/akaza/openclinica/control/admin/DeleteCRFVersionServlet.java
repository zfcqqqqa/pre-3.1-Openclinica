/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DeleteCRFVersionServlet extends SecureController {
    public static final String VERSION_ID = "verId";

    public static final String VERSION_TO_DELETE = "version";

    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        int versionId = fp.getInt(VERSION_ID, true);
        String action = request.getParameter("action");
        if (versionId == 0) {
            addPageMessage(respage.getString("please_choose_a_CRF_version_to_delete"));
            forwardPage(Page.CRF_LIST_SERVLET);
        } else {
            CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
            CRFVersionBean version = (CRFVersionBean) cvdao.findByPK(versionId);
            EventDefinitionCRFDAO edcdao = new EventDefinitionCRFDAO(sm.getDataSource());
            StudyEventDefinitionDAO sedDao = new StudyEventDefinitionDAO(sm.getDataSource());

            // find definitions using this version
            ArrayList definitions = edcdao.findByDefaultVersion(version.getId());
            for (Object edcBean: definitions) {
                StudyEventDefinitionBean sedBean = (StudyEventDefinitionBean)sedDao.findByPK(((EventDefinitionCRFBean)edcBean).getStudyEventDefinitionId());
                ((EventDefinitionCRFBean)edcBean).setEventName(sedBean.getName());
            }

            // find event crfs using this version
            EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
            ArrayList eventCRFs = ecdao.findAllByCRFVersion(versionId);
            boolean canDelete = true;
            if (!definitions.isEmpty()) {// used in definition
                canDelete = false;
                request.setAttribute("definitions", definitions);
                addPageMessage(respage.getString("this_CRF_version") + version.getName()
                    + respage.getString("has_associated_study_events_definitions_cannot_delete"));

            } else if (!eventCRFs.isEmpty()) {
                canDelete = false;
                request.setAttribute("eventsForVersion", eventCRFs);
                addPageMessage(respage.getString("this_CRF_version") + version.getName() + respage.getString("has_associated_study_events_cannot_delete"));
            }
            if ("confirm".equalsIgnoreCase(action)) {
                request.setAttribute(VERSION_TO_DELETE, version);
                forwardPage(Page.DELETE_CRF_VERSION);
            } else {
                // submit
                if (canDelete) {
                    ArrayList items = cvdao.findNotSharedItemsByVersion(versionId);
                    NewCRFBean nib = new NewCRFBean(sm.getDataSource(), version.getCrfId());
                    nib.setDeleteQueries(cvdao.generateDeleteQueries(versionId, items));
                    nib.deleteFromDB();
                    addPageMessage(respage.getString("the_CRF_version_has_been_deleted_succesfully"));
                } else {
                    addPageMessage(respage.getString("the_CRF_version_cannot_be_deleted"));
                }
                forwardPage(Page.CRF_LIST_SERVLET);
            }

        }

    }

}
