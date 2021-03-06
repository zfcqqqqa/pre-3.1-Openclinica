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
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ViewSubjectServlet extends SecureController {
  /**
   *  
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage(respage.getString("no_have_correct_privilege_current_study")
            + respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, resexception.getString("not_admin"), "1");

  }

  public void processRequest() throws Exception {

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    int subjectId = fp.getInt("id");

    if (subjectId == 0) {
      addPageMessage(respage.getString("please_choose_a_subject_to_view"));
      forwardPage(Page.SUBJECT_LIST_SERVLET);
    } else {
      SubjectBean subject = (SubjectBean) sdao.findByPK(subjectId);

      //find all study subjects
      StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
      ArrayList studySubs = ssdao.findAllBySubjectId(subjectId);

      request.setAttribute("subject",subject);
      request.setAttribute("studySubs", studySubs);
      forwardPage(Page.VIEW_SUBJECT);

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
