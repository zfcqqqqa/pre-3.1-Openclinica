/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.GroupClassType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyGroupBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 * Servlet to create a new subject group class
 */
public class CreateSubjectGroupClassServlet extends SecureController {
  public void mayProceed() throws InsufficientPermissionException {
	  
	  if (currentStudy.getParentStudyId() > 0) {
	      addPageMessage("Subject group class may only be added to top-level Studies. "
	          + "Please contact the System Administrator if you have questions.");
	      throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST, "not top study",
	          "1");
	    }
	  
    if (ub.isSysAdmin()) {
      return;
    }
    if (currentRole.getRole().equals(Role.STUDYDIRECTOR)
        || currentRole.getRole().equals(Role.COORDINATOR)) {
      return;
    }
    addPageMessage(respage.getString("no_have_correct_privilege_current_study")
            + respage.getString("change_study_contact_sysadmin"));
    throw new InsufficientPermissionException(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET,
    		resexception.getString("not_study_director"), "1");

  }

  public void processRequest() throws Exception {
    String action = request.getParameter("action");

    if (StringUtil.isBlank(action)) {
      ArrayList studyGroups = new ArrayList();
      for (int i = 0; i < 10; i++) {
        studyGroups.add(new StudyGroupBean());
      }
      StudyGroupClassBean group = new StudyGroupClassBean();
      request.setAttribute("groupTypes", GroupClassType.toArrayList());
      session.setAttribute("group", group);
      session.setAttribute("studyGroups", studyGroups);
      forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);

    } else {
      if ("confirm".equalsIgnoreCase(action)) {
        confirmGroup();

      } else if ("submit".equalsIgnoreCase(action)) {
        submitGroup();

      }
    }

  }

  /**
   * Validates the first section of study inputs and save it into study bean
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  private void confirmGroup() throws Exception {
    Validator v = new Validator(request);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("name", Validator.NO_BLANKS);    
    
    v.addValidation("subjectAssignment", Validator.NO_BLANKS);

    v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
    v.addValidation("subjectAssignment", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);

    ArrayList studyGroups = new ArrayList();
    for (int i = 0; i < 10; i++) {
      String name = fp.getString("studyGroup" + i);
      String description = fp.getString("studyGroupDescription" + i);
      if (!StringUtil.isBlank(name)) {
        StudyGroupBean group = new StudyGroupBean();
        group.setName(name);
        group.setDescription(description);
        studyGroups.add(group);
        if (name.length() > 255) {
          request.setAttribute("studyGroupError", respage.getString("group_name_cannot_be_more_255"));
          break;
        }
        if (description.length() > 1000) {
          request.setAttribute("studyGroupError", respage.getString("group_description_cannot_be_more_100"));
          break;
        }
      }

    }

    errors = v.validate();
    
    if (fp.getInt("groupClassTypeId")==0) {
      Validator.addError(errors, "groupClassTypeId",
      resexception.getString("group_class_type_is_required"));
    }

    StudyGroupClassBean group = new StudyGroupClassBean();
    group.setName(fp.getString("name"));
    group.setGroupClassTypeId(fp.getInt("groupClassTypeId"));
    group.setSubjectAssignment(fp.getString("subjectAssignment"));

    session.setAttribute("group", group);
    session.setAttribute("studyGroups", studyGroups);

    if (errors.isEmpty()) {
      logger.info("no errors in the first section");
      group.setGroupClassTypeName((GroupClassType.get(group.getGroupClassTypeId()).getName()));

      forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS_CONFIRM);

    } else {
      logger.info("has validation errors in the first section");
      request.setAttribute("formMessages", errors);
      request.setAttribute("groupTypes", GroupClassType.toArrayList());

      forwardPage(Page.CREATE_SUBJECT_GROUP_CLASS);
    }

  }

  /**
   * Saves study group information into database
   * @throws OpenClinicaException
   */
  private void submitGroup() throws OpenClinicaException {
    StudyGroupClassBean group = (StudyGroupClassBean) session.getAttribute("group");
    ArrayList studyGroups = (ArrayList) session.getAttribute("studyGroups");
    StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
    group.setStudyId(currentStudy.getId());
    group.setOwner(ub);
    group.setStatus(Status.AVAILABLE);
    group = (StudyGroupClassBean) sgcdao.create(group);

    if (!group.isActive()) {
      addPageMessage(respage.getString("the_subject_group_class_not_created_database"));
    } else {
      StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());
      for (int i = 0; i < studyGroups.size(); i++) {       
        StudyGroupBean sg = (StudyGroupBean)studyGroups.get(i);       
        sg.setStudyGroupClassId(group.getId());
        sg.setOwner(ub);
        sg.setStatus(Status.AVAILABLE);
        sgdao.create(sg);

      }
      addPageMessage(respage.getString("the_subject_group_class_created_succesfully"));
    }
    forwardPage(Page.SUBJECT_GROUP_CLASS_LIST_SERVLET);

  }

}
