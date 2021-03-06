/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.managestudy;

import java.util.ArrayList;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

/**
 * @author jxu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayStudySubjectBean extends AuditableEntityBean {
  private StudySubjectBean studySubject;
  private ArrayList studyGroups;
  private ArrayList studyEvents;
  
  //YW 
  private int sedId;
  
  /**
   * 
   * @return sedId
   */
  public int getSedId() {
	  return sedId;
  }
  /**
   * 
   * @param sedId
   */
  public void setSedId(int sedId) {
	  this.sedId = sedId;
  }

  /**
   * @return Returns the studyEvents.
   */
  public ArrayList getStudyEvents() {
    return studyEvents;
  }
  /**
   * @param studyEvents The studyEvents to set.
   */
  public void setStudyEvents(ArrayList studyEvents) {
    this.studyEvents = studyEvents;
  }
  /**
   * @return Returns the studySubject.
   */
  public StudySubjectBean getStudySubject() {
    return studySubject;
  }
  /**
   * @param studySubject The studySubject to set.
   */
  public void setStudySubject(StudySubjectBean studySubject) {
    this.studySubject = studySubject;
  }
  /**
   * @return Returns the studyGroups.
   */
  public ArrayList getStudyGroups() {
    return studyGroups;
  }
  /**
   * @param studyGroups The studyGroups to set.
   */
  public void setStudyGroups(ArrayList studyGroups) {
    this.studyGroups = studyGroups;
  }
}
