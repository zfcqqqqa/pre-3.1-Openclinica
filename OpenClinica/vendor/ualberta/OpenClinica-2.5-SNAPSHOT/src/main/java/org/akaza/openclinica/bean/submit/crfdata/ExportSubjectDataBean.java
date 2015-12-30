/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2008 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.submit.crfdata;

import java.util.ArrayList;

/**
 * OpenClinica subject attributes have been included in addition to ODM
 * SubjectData attributes
 * 
 * @author ywang (Nov, 2008)
 */

public class ExportSubjectDataBean extends SubjectDataBean {
    private String studySubjectId;
    private String uniqueIdentifier;
    private String status;
    private String secondaryId;
    private Integer ageAtEvent;
    private Integer yearOfBirth;
    private String dateOfBirth;
    private String subjectGender;

    private ArrayList<ExportStudyEventDataBean> exportStudyEventData;

    public ExportSubjectDataBean() {
        super();
        this.exportStudyEventData = new ArrayList<ExportStudyEventDataBean>();
    }

    public void setStudySubjectId(String studySubjectId) {
        this.studySubjectId = studySubjectId;
    }

    public String getStudySubjectId() {
        return this.studySubjectId;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getUniqueIdentifier() {
        return this.uniqueIdentifier;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }

    public String getSecondaryId() {
        return this.secondaryId;
    }

    public void setAgeAtEvent(Integer ageAtEvent) {
        this.ageAtEvent = ageAtEvent;
    }

    public Integer getAgeAtEvent() {
        return this.ageAtEvent;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getYearOfBirth() {
        return this.yearOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setSubjectGender(String gender) {
        this.subjectGender = gender;
    }

    public String getSubjectGender() {
        return this.subjectGender;
    }

    public ArrayList<ExportStudyEventDataBean> getExportStudyEventData() {
        return exportStudyEventData;
    }

    public void setExportStudyEventData(ArrayList<ExportStudyEventDataBean> studyEventData) {
        this.exportStudyEventData = studyEventData;
    }
}
