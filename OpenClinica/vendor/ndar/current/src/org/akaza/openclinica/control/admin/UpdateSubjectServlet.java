/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.submit.AddNewSubjectServlet;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;

/**
 * @author jxu
 * 
 */
public class UpdateSubjectServlet extends SecureController {
  public static final String INPUT_FATHER = "fatherId";

  public static final String INPUT_MOTHER = "motherId";

  public static final String BEAN_FATHERS = "fathers";

  public static final String BEAN_MOTHERS = "mothers";

  public static final String YEAR_DOB = "yearOfBirth";

  public static final String DATE_DOB = "dateOfBirth";

  /**
   * 
   */
  public void mayProceed() throws InsufficientPermissionException {
    if (ub.isSysAdmin()) {
      return;
    }

    addPageMessage("You don't have correct privilege. "
        + "Please change your active study or contact your sysadmin.");
    throw new InsufficientPermissionException(Page.SUBJECT_LIST_SERVLET, "not admin", "1");

  }

  public void processRequest() throws Exception {
    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    FormProcessor fp = new FormProcessor(request);
    FormDiscrepancyNotes discNotes = null;

    int subjectId = fp.getInt("id", true);
    int studySubId = fp.getInt("studySubId", true);

    if (subjectId == 0) {
      addPageMessage("Please choose a subject to edit.");
      forwardPage(Page.LIST_SUBJECT_SERVLET);
    } else {
      String action = fp.getString("action", true);

      if (StringUtil.isBlank("action")) {
        addPageMessage("No action specified.");
        forwardPage(Page.LIST_SUBJECT_SERVLET);
        return;
      }
      SubjectBean sub = (SubjectBean) sdao.findByPK(subjectId);

      request.setAttribute("studySubId", new Integer(studySubId));
      ArrayList fathers = sdao.findAllByGenderNotSelf('m', sub.getId());
      ArrayList mothers = sdao.findAllByGenderNotSelf('f', sub.getId());
      ArrayList dsFathers = new ArrayList();
      ArrayList dsMothers = new ArrayList();

      StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
      StudyDAO stdao = new StudyDAO(sm.getDataSource());

      AddNewSubjectServlet.displaySubjects(dsFathers, fathers, ssdao, stdao);
      AddNewSubjectServlet.displaySubjects(dsMothers, mothers, ssdao, stdao);

      if (!sub.isDobCollected()) {
        Date dob = sub.getDateOfBirth();
        Calendar cal = Calendar.getInstance();
        int year = 0;
        if (dob != null) {
          cal.setTime(dob);
          year = cal.get(Calendar.YEAR);
          request.setAttribute(YEAR_DOB, new Integer(year));
        } else {
          request.setAttribute(DATE_DOB, "");
        }
      }
      if ("show".equalsIgnoreCase(action)) {
        session.setAttribute("subjectToUpdate", sub);
        request.setAttribute(BEAN_FATHERS, dsFathers);
        request.setAttribute(BEAN_MOTHERS, dsMothers);

        discNotes = new FormDiscrepancyNotes();
        session.setAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME, discNotes);
        forwardPage(Page.UPDATE_SUBJECT);
      } else if ("confirm".equalsIgnoreCase(action)) {
        request.setAttribute(BEAN_FATHERS, dsFathers);
        request.setAttribute(BEAN_MOTHERS, dsMothers);
        confirm();
      } else {
        SubjectBean subject = (SubjectBean) session.getAttribute("subjectToUpdate");
        subject.setUpdater(ub);
        EntityBean sb = sdao.update(subject);

		if (!sdao.isQuerySuccessful() || !sb.isActive()) {
		    throw new OpenClinicaException("Could not update subject.", "3");
		}

        // save discrepancy notes into DB
        FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session
            .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        AddNewSubjectServlet.saveFieldNotes("gender", fdn, dndao, subject.getId(), "subject",
            currentStudy);
        AddNewSubjectServlet.saveFieldNotes(YEAR_DOB, fdn, dndao, subject.getId(), "subject",
            currentStudy);
        AddNewSubjectServlet.saveFieldNotes(DATE_DOB, fdn, dndao, subject.getId(), "subject",
            currentStudy);

        session.removeAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
        addPageMessage("The subject has been updated successfully.");
        session.removeAttribute("subjectToUpdate");
        if (studySubId > 0) {
          request.setAttribute("id", new Integer(studySubId).toString());
          forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
        } else {
          forwardPage(Page.LIST_SUBJECT_SERVLET);
        }
      }

    }
  }

  /**
   * Processes 'confirm' request, validate the subject object
   * 
   * @throws Exception
   */
  private void confirm() throws Exception {
    SubjectBean sub = (SubjectBean) session.getAttribute("subjectToUpdate");
    FormDiscrepancyNotes discNotes = (FormDiscrepancyNotes) session
        .getAttribute(AddNewSubjectServlet.FORM_DISCREPANCY_NOTES_NAME);
    DiscrepancyValidator v = new DiscrepancyValidator(request, discNotes);
    FormProcessor fp = new FormProcessor(request);

    v.addValidation("uniqueIdentifier", Validator.LENGTH_NUMERIC_COMPARISON,
        NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
    v.alwaysExecuteLastValidation("uniqueIdentifier");

    if (!sub.isDobCollected()) {
      if (!StringUtil.isBlank(fp.getString(YEAR_DOB))) {
        v.addValidation(YEAR_DOB, Validator.IS_AN_INTEGER);
        v.alwaysExecuteLastValidation(YEAR_DOB);
      }
      // if the original DOB is null, but user entered a new DOB
      if (!StringUtil.isBlank(fp.getString(DATE_DOB))) {
        v.addValidation(DATE_DOB, Validator.IS_A_DATE);
        v.alwaysExecuteLastValidation(DATE_DOB);
      }
    } else {
      if (!StringUtil.isBlank(fp.getString(DATE_DOB))) {
        v.addValidation(DATE_DOB, Validator.IS_A_DATE);
        v.alwaysExecuteLastValidation(DATE_DOB);
      }

    }

    errors = v.validate();
    
    //uniqueIdentifier must be unique in the system
    if (!StringUtil.isBlank(fp.getString("uniqueIdentifier"))) {
      SubjectDAO sdao = new SubjectDAO(sm.getDataSource());      
     
      SubjectBean sub1 = (SubjectBean) sdao.findAnotherByIdentifier(fp.getString("uniqueIdentifier").trim(),sub.getId());
      
      if (sub1.getId() > 0) {
        Validator.addError(errors, "uniqueIdentifier", "Person ID has been used by another subject, please choose a unique one.");
      }
    }

    SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
    SubjectBean mother = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_MOTHER));
    SubjectBean father = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_FATHER));

    StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
    StudyDAO stdao = new StudyDAO(sm.getDataSource());
    // display mother
    ArrayList studySubs = ssdao.findAllBySubjectId(mother.getId());
    String protocolSubjectIds = "";
    for (int j = 0; j < studySubs.size(); j++) {
      StudySubjectBean studySub = (StudySubjectBean) studySubs.get(j);
      int studyId = studySub.getStudyId();
      StudyBean stu = (StudyBean) stdao.findByPK(studyId);
      String protocolId = stu.getIdentifier();
      if (j == (studySubs.size() - 1)) {
        protocolSubjectIds += protocolId + "-" + studySub.getLabel();
      } else {
        protocolSubjectIds += protocolId + "-" + studySub.getLabel() + ", ";
      }
    }
    DisplaySubjectBean dsbm = new DisplaySubjectBean();
    dsbm.setSubject(mother);
    dsbm.setStudySubjectIds(protocolSubjectIds);

    // display father
    studySubs = ssdao.findAllBySubjectId(father.getId());
    protocolSubjectIds = "";
    for (int j = 0; j < studySubs.size(); j++) {
      StudySubjectBean studySub = (StudySubjectBean) studySubs.get(j);
      int studyId = studySub.getStudyId();
      StudyBean stu = (StudyBean) stdao.findByPK(studyId);
      String protocolId = stu.getIdentifier();
      if (j == (studySubs.size() - 1)) {
        protocolSubjectIds += protocolId + "-" + studySub.getLabel();
      } else {
        protocolSubjectIds += protocolId + "-" + studySub.getLabel() + ", ";
      }
    }
    DisplaySubjectBean dsbf = new DisplaySubjectBean();
    dsbf.setSubject(father);
    dsbf.setStudySubjectIds(protocolSubjectIds);

    String uniqueIdentifier = fp.getString("uniqueIdentifier");
    if (!StringUtil.isBlank(uniqueIdentifier)) {
      SubjectBean subjectWithSameId = sdao.findByUniqueIdentifier(uniqueIdentifier);
      if (subjectWithSameId!=null && subjectWithSameId.isActive() && subjectWithSameId.getId() != sub.getId()) {
        Validator
            .addError(errors, "uniqueIdentifier",
                "Another subject has been assigned this Person ID.  Please choose a unique identifier.");
      }
    }

    boolean insertWithParents = ((fp.getInt(INPUT_MOTHER) > 0) || (fp.getInt(INPUT_FATHER) > 0));

    if (insertWithParents) {
      if ((mother == null) || !mother.isActive() || (mother.getGender() != 'f')) {
        Validator.addError(errors, INPUT_MOTHER,
            "Please choose a valid female subject as the mother.");
      }

      if ((father == null) || !father.isActive() || (father.getGender() != 'm')) {
        Validator.addError(errors, INPUT_FATHER,
            "Please choose a valid male subject as the father.");
      }
    }
    
    boolean newDobInput = false;
    if (!sub.isDobCollected()) {
      if (!StringUtil.isBlank(fp.getString(YEAR_DOB))) {
        int yob = fp.getInt(YEAR_DOB);

        v.addValidation(YEAR_DOB, Validator.IS_AN_INTEGER);
        v.alwaysExecuteLastValidation(YEAR_DOB);

        v.addValidation(YEAR_DOB, Validator.COMPARES_TO_STATIC_VALUE,
            NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 1000);
        v.addValidation(YEAR_DOB, Validator.COMPARES_TO_STATIC_VALUE,
            NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 9999);

        String dobString = "01/01/" + yob;
        try {
          Date fakeDOB = sdf.parse(dobString);
          sub.setDateOfBirth(fakeDOB);
        } catch (ParseException pe) {
          logger.info("Parse exception happened.");          
          Validator.addError(errors, YEAR_DOB, "Please enter a valid Year Of Birth.");
        }
        request.setAttribute(YEAR_DOB, fp.getString(YEAR_DOB));
      } else if (!StringUtil.isBlank(fp.getString(DATE_DOB))) {
        // DOB is null orginally, and user entered a new DOB
        v.addValidation(DATE_DOB, Validator.IS_A_DATE);
        v.alwaysExecuteLastValidation(DATE_DOB);       
        request.setAttribute(DATE_DOB, fp.getString(DATE_DOB));
        newDobInput = true;

      } else {
        sub.setDateOfBirth(null);
        
      }
    } else {
      sub.setDateOfBirth(fp.getDate(DATE_DOB));
    }

    if (!StringUtil.isBlank(fp.getString("gender"))) {
      sub.setGender(fp.getString("gender").charAt(0));
    } else {
      sub.setGender(' ');
    }
    sub.setFatherId(fp.getInt(INPUT_FATHER));
    sub.setMotherId(fp.getInt(INPUT_MOTHER));
    sub.setUniqueIdentifier(uniqueIdentifier);
    session.setAttribute("subjectToUpdate", sub);

    if (errors.isEmpty()) {
      logger.info("no errors");

      request.setAttribute("father", father);
      request.setAttribute("mother", mother);

      request.setAttribute("disFather", dsbf);
      request.setAttribute("disMother", dsbm);
      if (newDobInput) {
        sub.setDobCollected(true);
      }
      forwardPage(Page.UPDATE_SUBJECT_CONFIRM);
    } else {
      logger.info("validation errors");
      request.setAttribute(YEAR_DOB, fp.getString(YEAR_DOB));
      setInputMessages(errors);
      forwardPage(Page.UPDATE_SUBJECT);
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