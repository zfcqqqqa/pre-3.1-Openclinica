/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.managestudy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;

/**
 * @author jxu
 *  
 */
public class StudyEventDAO extends AuditableEntityDAO {
  // private DAODigester digester;

  private void setQueryNames() {
    findByPKAndStudyName = "findByPKAndStudy";
  }

  public StudyEventDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public StudyEventDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_STUDYEVENT;
  }

  public void setTypesExpected() {
    //SERIAL NUMERIC NUMERIC VARCHAR(2000)
    //NUMERIC DATE DATE NUMERIC
    //NUMERIC DATE DATE NUMERIC
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.STRING);

    this.setTypeExpected(5, TypeNames.INT);
    this.setTypeExpected(6, TypeNames.DATE);
    this.setTypeExpected(7, TypeNames.DATE);
    this.setTypeExpected(8, TypeNames.INT);

    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.DATE);
    this.setTypeExpected(11, TypeNames.DATE);
    this.setTypeExpected(12, TypeNames.INT);
    this.setTypeExpected(13, TypeNames.INT);

  }
  
  public void setTypesExpected(boolean withSubject) {
    //SERIAL NUMERIC NUMERIC VARCHAR(2000)
    //NUMERIC DATE DATE NUMERIC
    //NUMERIC DATE DATE NUMERIC
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.STRING);

    this.setTypeExpected(5, TypeNames.INT);
    this.setTypeExpected(6, TypeNames.DATE);
    this.setTypeExpected(7, TypeNames.DATE);
    this.setTypeExpected(8, TypeNames.INT);

    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.DATE);
    this.setTypeExpected(11, TypeNames.DATE);
    this.setTypeExpected(12, TypeNames.INT);
    this.setTypeExpected(13, TypeNames.INT);
    if (withSubject){
      this.setTypeExpected(14, TypeNames.STRING);
    }

  }

  public void setCRFTypesExpected() {
    /*
     * <sql>SELECT C.CRF_ID, C.STATUS_ID, C.NAME, C.DESCRIPTION,
     * V.CRF_VERSION_ID, V.NAME, V.REVISION_NOTES FROM CRF C, CRF_VERSION V,
     * EVENT_DEFINITION_CRF EDC WHERE C.CRF_ID = V.CRF_ID AND EDC.CRF_ID =
     * C.CRF_ID AND EDC.STUDY_EVENT_DEFINITION_ID =? </sql>
     */
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.STRING);
    this.setTypeExpected(4, TypeNames.STRING);
    this.setTypeExpected(5, TypeNames.INT);
    this.setTypeExpected(6, TypeNames.STRING);
    this.setTypeExpected(7, TypeNames.STRING);
  }

  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm) {
    StudyEventBean eb = new StudyEventBean();
    super.setEntityAuditInformation(eb, hm);
    //STUDY_EVENT_ID STUDY_EVENT_DEFINITION_ID SUBJECT_ID LOCATION
    //SAMPLE_ORDINAL DATE_START DATE_END OWNER_ID
    //STATUS_ID DATE_CREATED DATE_UPDATED UPDATE_ID
    eb.setId(((Integer) hm.get("study_event_id")).intValue());
    eb.setStudyEventDefinitionId(((Integer) hm.get("study_event_definition_id")).intValue());
    eb.setStudySubjectId(((Integer) hm.get("study_subject_id")).intValue());
    eb.setLocation((String) hm.get("location"));
    eb.setSampleOrdinal(((Integer) hm.get("sample_ordinal")).intValue());
    eb.setDateStarted((Date) hm.get("date_start"));
    eb.setDateEnded((Date) hm.get("date_end"));
    //eb.setStatus(eb.getStatus());
    int subjectEventStatuId = ((Integer) hm.get("subject_event_status_id")).intValue();
    eb.setSubjectEventStatus(SubjectEventStatus.get(subjectEventStatuId)); 
   

    return eb;
  }
  
  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm, boolean withSubject) {
    StudyEventBean eb = new StudyEventBean();
    super.setEntityAuditInformation(eb, hm);
    //STUDY_EVENT_ID STUDY_EVENT_DEFINITION_ID SUBJECT_ID LOCATION
    //SAMPLE_ORDINAL DATE_START DATE_END OWNER_ID
    //STATUS_ID DATE_CREATED DATE_UPDATED UPDATE_ID
    eb.setId(((Integer) hm.get("study_event_id")).intValue());
    eb.setStudyEventDefinitionId(((Integer) hm.get("study_event_definition_id")).intValue());
    eb.setStudySubjectId(((Integer) hm.get("study_subject_id")).intValue());
    eb.setLocation((String) hm.get("location"));
    eb.setSampleOrdinal(((Integer) hm.get("sample_ordinal")).intValue());
    eb.setDateStarted((Date) hm.get("date_start"));
    eb.setDateEnded((Date) hm.get("date_end"));
    //eb.setStatus(eb.getStatus());
    int subjectEventStatuId = ((Integer) hm.get("subject_event_status_id")).intValue();
    eb.setSubjectEventStatus(SubjectEventStatus.get(subjectEventStatuId)); 
    if (withSubject){
      eb.setStudySubjectLabel((String)hm.get("label"));
    }

    return eb;
  }

  //public HashMap getListOfStudyEvents()

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection findAllByDefinition(int definitionId) {
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(definitionId));

    String sql = digester.getQuery("findAllByDefinition");
    ArrayList alist = this.select(sql, variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }
  
  
  public ArrayList findAllWithSubjectLabelByDefinition(int definitionId) {
    this.setTypesExpected(true);
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(definitionId));

    String sql = digester.getQuery("findAllWithSubjectLabelByDefinition");
    ArrayList alist = this.select(sql, variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next(),true);
      al.add(eb);
    }
    return al;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public ArrayList findAllByDefinitionAndSubject(StudyEventDefinitionBean definition,
      StudySubjectBean subject) {
    ArrayList answer = new ArrayList();

    setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(definition.getId()));
    variables.put(new Integer(2), new Integer(subject.getId()));

    String sql = digester.getQuery("findAllByDefinitionAndSubject");

    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    while (it.hasNext()) {
      StudyEventBean studyEvent = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(studyEvent);
    }

    return answer;
  }
  
  public ArrayList findAllByDefinitionAndSubjectOrderByOrdinal(StudyEventDefinitionBean definition,
      StudySubjectBean subject) {
    ArrayList answer = new ArrayList();

    setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(definition.getId()));
    variables.put(new Integer(2), new Integer(subject.getId()));

    String sql = digester.getQuery("findAllByDefinitionAndSubjectOrderByOrdinal");

    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    while (it.hasNext()) {
      StudyEventBean studyEvent = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(studyEvent);
    }

    return answer;
  }

  public EntityBean findByPK(int ID) {
    StudyEventBean eb = new StudyEventBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  /**
   * Creates a new studysubject
   */
  public EntityBean create(EntityBean eb) {
    StudyEventBean sb = (StudyEventBean) eb;
    HashMap variables = new HashMap();
    HashMap nullVars = new HashMap();
//	   INSERT INTO STUDY_EVENT 
//	   (STUDY_EVENT_DEFINITION_ID,SUBJECT_ID,LOCATION,SAMPLE_ORDINAL,
//        DATE_START,DATE_END,OWNER_ID,STATUS_ID,DATE_CREATED) 
//        VALUES (?,?,?,?,?,?,?,?,NOW(),subject_event_status_id)
    variables.put(new Integer(1), new Integer(sb.getStudyEventDefinitionId()));
    variables.put(new Integer(2), new Integer(sb.getStudySubjectId()));
    variables.put(new Integer(3), sb.getLocation());
    variables.put(new Integer(4), new Integer(sb.getSampleOrdinal()));
    if (sb.getDateStarted() == null) {
      nullVars.put(new Integer(5), new Integer(TypeNames.DATE));
      variables.put(new Integer(5), null);
    } else {
      variables.put(new Integer(5), sb.getDateStarted());
    }
    if (sb.getDateEnded()== null) {
      nullVars.put(new Integer(6), new Integer(TypeNames.DATE));
      variables.put(new Integer(6), null);
    } else {
      variables.put(new Integer(6), sb.getDateEnded());
    }
    variables.put(new Integer(7), new Integer(sb.getOwner().getId()));
    variables.put(new Integer(8), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(9), new Integer(sb.getSubjectEventStatus().getId()));
    
    
    this.execute(digester.getQuery("create"), variables);

    if (isQuerySuccessful()) {
      sb.setId(getCurrentPK());
    }

    return sb;
  }

  /**
   * Updates a Study event
   */
  public EntityBean update(EntityBean eb) {
    StudyEventBean sb = (StudyEventBean) eb;
    HashMap variables = new HashMap();
    //UPDATE study_event SET
    // STUDY_EVENT_DEFINITION_ID=?,SUBJECT_ID=?,LOCATION=?,
    //SAMPLE_ORDINAL=?, DATE_START=?,DATE_END=?,STATUS_ID=?,DATE_UPDATED=?,
    //UPDATE_ID=?, subject_event_status_id=?  WHERE STUDY_EVENT_ID=?
    
    sb.setActive(false);
    
    variables.put(new Integer(1), new Integer(sb.getStudyEventDefinitionId()));
    variables.put(new Integer(2), new Integer(sb.getStudySubjectId()));
    variables.put(new Integer(3), sb.getLocation());
    variables.put(new Integer(4), new Integer(sb.getSampleOrdinal()));
    variables.put(new Integer(5), sb.getDateStarted());
    variables.put(new Integer(6), sb.getDateEnded());
    variables.put(new Integer(7), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(8), new java.util.Date());//DATE_Updated
    variables.put(new Integer(9), new Integer(sb.getUpdater().getId()));
    variables.put(new Integer(10), new Integer(sb.getSubjectEventStatus().getId()));
    variables.put(new Integer(11), new Integer(sb.getId()));

    String sql = digester.getQuery("update");
    this.execute(sql, variables);

    if (isQuerySuccessful()) {
    	sb.setActive(true);
    }
    
    return sb;
  }

  public Collection findAllByPermission(Object objCurrentUser, int intActionType,
      String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
    ArrayList al = new ArrayList();

    return al;
  }

  public int getCurrentPK() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);

    int pk = 0;
    ArrayList al = select(digester.getQuery("getCurrentPrimaryKey"));

    if (al.size() > 0) {
      HashMap h = (HashMap) al.get(0);
      pk = ((Integer) h.get("key")).intValue();
    }

    return pk;
  }

  public ArrayList findAllByStudyAndStudySubjectId(StudyBean study, int studySubjectId) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(study.getId()));
    variables.put(new Integer(2), new Integer(study.getId()));
    variables.put(new Integer(3), new Integer(studySubjectId));

    ArrayList alist = this.select(digester.getQuery("findAllByStudyAndStudySubjectId"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(seb);
    }

    return answer;
  }

  public ArrayList findAllByStudyAndEventDefinitionId(StudyBean study, int eventDefinitionId) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(study.getId()));
    variables.put(new Integer(2), new Integer(study.getId()));
    variables.put(new Integer(3), new Integer(eventDefinitionId));

    ArrayList alist = this.select(digester.getQuery("findAllByStudyAndEventDefinitionId"),
        variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(seb);
    }

    return answer;
  }
  
  /**
   * Get the maximum sample ordinal over all study events for the provided StudyEventDefinition /
   * StudySubject combination.  Note that the maximum may be zero but must be non-negative.
   * @param sedb The study event definition whose ordinal we're looking for.
   * @param studySubject The study subject whose ordinal we're looking for.
   * @return The maximum sample ordinal over all study events for the provided combination, or 0 if no such combination exists.
   */
  public int getMaxSampleOrdinal(StudyEventDefinitionBean sedb, StudySubjectBean studySubject) {
  	ArrayList answer = new ArrayList();
  	
  	this.unsetTypeExpected();
  	this.setTypeExpected(1, TypeNames.INT);
  	
  	HashMap variables = new HashMap();
  	variables.put(new Integer(1), new Integer(sedb.getId()));
  	variables.put(new Integer(2), new Integer(studySubject.getId()));
  	
  	ArrayList alist = this.select(digester.getQuery("getMaxSampleOrdinal"), variables);
  	Iterator it = alist.iterator();
  	if (it.hasNext()) {
  		try {
  			HashMap hm = (HashMap) it.next();
  			Integer max = (Integer) hm.get("max_ord");
  			return max.intValue();
  		}
  		catch (Exception e) { }
  	}
  	
  	return 0;
  }

  public ArrayList findAllByStudy(StudyBean study) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(study.getId()));

    ArrayList alist = this.select(digester.getQuery("findAllByStudy"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(seb);
    }

    return answer;
  }

  /**
   * @deprecated
   * @param subjectId
   * @param studyId   
   */
  public ArrayList findAllBySubjectAndStudy(int subjectId, int studyId) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(subjectId));
    variables.put(new Integer(2), new Integer(studyId));
    variables.put(new Integer(3), new Integer(studyId));

    ArrayList alist = this.select(digester.getQuery("findAllBySubjectAndStudy"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(seb);
    }

    return answer;

  }
  
  public ArrayList findAllBySubjectId(int subjectId) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(subjectId));
    
    ArrayList alist = this.select(digester.getQuery("findAllBySubjectId"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventBean seb = (StudyEventBean) this.getEntityFromHashMap((HashMap) it.next());
      answer.add(seb);
    }

    return answer;

  }

  public void setNewCRFTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.STRING);
    this.setTypeExpected(3, TypeNames.STRING);
    this.setTypeExpected(4, TypeNames.INT);
    this.setTypeExpected(5, TypeNames.STRING);
    this.setTypeExpected(6, TypeNames.STRING);
    this.setTypeExpected(7, TypeNames.INT);
    this.setTypeExpected(8, TypeNames.STRING);
    

  }

  /**
   * Using the HashMaps returned from a <code>select</code> call in
   * findCRFsByStudy, prepare a HashMap whose keys are study event definitions
   * and whose values are ArrayLists of CRF versions included in those
   * definitions.
   * 
   * @param rows
   *          The HashMaps retured by the <code>select</code> call in
   *          findCRFsByStudy.
   * @return a HashMap whose keys are study event definitions and whose values
   *         are ArrayLists of CRF versions included in those definitions. Both
   *         the keys of the HashMap and the elements of the ArrayLists are
   *         actually EntitBeans.
   */
  public HashMap getEventsAndMultipleCRFVersionInformation(ArrayList rows) {
    HashMap returnMe = new HashMap();
    Iterator it = rows.iterator();
    EntityBean event = new EntityBean();
    EntityBean crf = new EntityBean();
    EntityBean version = new EntityBean();
    while (it.hasNext()) {
      HashMap answers = (HashMap) it.next();

      // removed setActive since the setId calls automatically result in
      // setActive calls
      event = new EntityBean();
      event.setName((String) answers.get("sed_name"));
      event.setId(((Integer) answers.get("study_event_definition_id")).intValue());

      crf = new EntityBean();
      crf.setName((String) answers.get("crf_name") + " " + (String) answers.get("ver_name"));
      crf.setId(((Integer) answers.get("crf_version_id")).intValue());

      ArrayList crfs = new ArrayList();
      if (this.findDouble(returnMe, event)) {//(returnMe.containsKey(event)) {
        //TODO create custom checker, this does not work
        //logger.warning("putting a crf into an OLD event: " + crf.getName() + " into "
        //    + event.getName());
        //logger.warning("just entered the if statement");
        crfs = this.returnDouble(returnMe, event);//(ArrayList)
                                                  // returnMe.get(event);
        //logger.warning("just got the array list from the hashmap");
        crfs.add(crf);
        //logger.warning("just added the crf to the array list");
        returnMe = this.removeDouble(returnMe, event);
        //.remove(event);
        //not sure the above will work, tbh
        returnMe.put(event, crfs);
      } else {
        crfs = new ArrayList();
        logger.warning("put a crf into a NEW event: " + crf.getName() + " into " + event.getName());
        crfs.add(crf);
        returnMe.put(event, crfs);//maybe combine the two crf + version?
      }
    }//end of cycling through answers

    return returnMe;
  }

  // TODO: decide whether to use getEventsAndMultipleCRFVersionInformation
  // instead of this method
  public HashMap getEventAndCRFVersionInformation(ArrayList al) {
    HashMap returnMe = new HashMap();
    Iterator it = al.iterator();
    EntityBean event = new EntityBean();
    EntityBean crf = new EntityBean();
    EntityBean version = new EntityBean();
    while (it.hasNext()) {
      HashMap answers = (HashMap) it.next();
      logger
          .warning("***Study Event Def ID: " + (Integer) answers.get("study_event_definition_id"));
      logger.warning("***CRF ID: " + (Integer) answers.get("crf_id"));
      logger.warning("***CRFVersion ID: " + (Integer) answers.get("crf_version_id"));
      event = new EntityBean();
      event.setActive(true);
      event.setName((String) answers.get("sed_name"));
      event.setId(((Integer) answers.get("study_event_definition_id")).intValue());
      crf = new EntityBean();
      crf.setActive(true);
      crf.setName((String) answers.get("crf_name") + " " + (String) answers.get("ver_name"));
      crf.setId(((Integer) answers.get("crf_version_id")).intValue());
      returnMe.put(event, crf);//maybe combine the two crf + version?
    }//end of cycling through answers

    return returnMe;
  }

  // TODO: decide whether to use SQL below in place of other sql. they're pretty
  // similar
  /*
   * ssachs - this is meant for use with
   * getEventsAndMultipleCRFVersionInformation; in particular the column names
   * "study_event_name", "crf_name" and "crf_version_name" should be maintained
   * if the SQL changes SELECT SED.name AS study_event_name ,
   * SED.study_event_definition_id , C.name AS crf_name , CV.name AS
   * crf_version_name , CV.crf_version_id FROM study_event_definition SED ,
   * event_definition_crf EDC , crf C , crf_version CV WHERE SED.study_id = ?
   * AND SED.study_event_definition_id = EDC.study_event_definition_id AND
   * C.crf_id = EDC.crf_id AND C.crf_id = CV.crf_id
   */

  public HashMap findCRFsByStudy(StudyBean sb) {
	//	SELECT DISTINCT
	//		C.CRF_ID
	//		, C.NAME AS CRF_NAME
	//		, C.DESCRIPTION
	//		, V.CRF_VERSION_ID
	//		, V.NAME AS VER_NAME
	//		, V.REVISION_NOTES
	//		, SED.STUDY_EVENT_DEFINITION_ID
	//		, SED.NAME AS SED_NAME
	//	FROM
	//		CRF C
	//		, CRF_VERSION V
	//		, EVENT_DEFINITION_CRF EDC
	//		, STUDY_EVENT_DEFINITION SED
	//	WHERE
	//			C.CRF_ID = V.CRF_ID
	//			AND EDC.CRF_ID = C.CRF_ID
	//			AND EDC.STUDY_EVENT_DEFINITION_ID = SED.STUDY_EVENT_DEFINITION_ID
	//			AND SED.STATUS_ID = 1
	//			AND SED.STUDY_ID = ? 
	//	ORDER BY C.CRF_ID, V.CRF_VERSION_ID
  	
  	HashMap crfs = new HashMap();
  	this.setNewCRFTypesExpected();
  	HashMap variables = new HashMap();
  	variables.put(new Integer(1), new Integer(sb.getId()));
  	ArrayList alist = this.select(digester.getQuery("findCRFsByStudy"), variables);
  	//TODO make sure this other statement for eliciting crfs works, tbh
  	//switched from getEventAndCRFVersionInformation
  	//to getEventsAndMultipleCRFVersionInformation
  	//crfs = this.getEventAndCRFVersionInformation(alist);
  	crfs = this.getEventsAndMultipleCRFVersionInformation(alist);
  	return crfs;
  }

  public HashMap findCRFsByStudyEvent(StudyEventBean seb) {
    //Soon-to-be-depreciated, replaced by find crfs by study, tbh 11-26
    //returns a hashmap of crfs + arraylist of crfversions,
    //for creating a checkbox list of crf versions all collected by
    //the study event primary key, tbh
    HashMap crfs = new HashMap();
    this.setCRFTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(seb.getStudyEventDefinitionId()));
    ArrayList alist = this.select(digester.getQuery("findCRFsByStudyEvent"), variables);
    Iterator it = alist.iterator();
    CRFDAO cdao = new CRFDAO(this.ds);
    CRFVersionDAO cvdao = new CRFVersionDAO(this.ds);
    while (it.hasNext()) {
      HashMap answers = (HashMap) it.next();
      logger.warning("***First CRF ID: " + (Integer) answers.get("crf_id"));
      logger.warning("***Next CRFVersion ID: " + (Integer) answers.get("crf_version_id"));
      //here's the logic:
      //grab a crf,
      //iterate through crfs in hashmap,
      //if one matches, grab it;
      //take a look at its arraylist of versions;
      //if there is no version correlating, add it;
      //else, add the crf with a fresh arraylist + one version.
      //how long could this take to run???
      CRFBean cbean = (CRFBean) cdao.findByPK(((Integer) answers.get("crf_id")).intValue());
      CRFVersionBean cvb = (CRFVersionBean) cvdao
          .findByPK(((Integer) answers.get("crf_version_id")).intValue());
      Set se = crfs.entrySet();
      boolean found = false;
      boolean versionFound = false;
      for (Iterator itse = se.iterator(); itse.hasNext();) {
        Map.Entry me = (Map.Entry) itse.next();
        CRFBean checkCrf = (CRFBean) me.getKey();
        if (checkCrf.getId() == cbean.getId()) {
          found = true;
          ArrayList oldList = (ArrayList) me.getValue();
          Iterator itself = oldList.iterator();
          while (itself.hasNext()) {
            CRFVersionBean cvbCheck = (CRFVersionBean) itself.next();
            if (cvbCheck.getId() == cvb.getId()) {
              versionFound = true;
            }
          }//end of iteration through versions
          if (!versionFound) {
            oldList.add(cvb);
            crfs.put(cbean, oldList);
          }//end of adding new version to old crf
        }//end of check to see if current crf is in list
      }//end of iterating
      if (!found) {
        //add new crf here with version
        //CRFVersionBean cvb = (CRFVersionBean)cvdao.findByPK(
        //	((Integer)answers.get("crf_version_id")).intValue());
        ArrayList newList = new ArrayList();
        newList.add(cvb);
        crfs.put(cbean, newList);
      }
    }//end of cycling through answers

    return crfs;
  }

  //TODO make sure we are returning the correct boolean, tbh
  public boolean findDouble(HashMap hm, EntityBean event) {
    boolean returnMe = false;
    Set s = hm.entrySet();
    for (Iterator it = s.iterator(); it.hasNext();) {
      Map.Entry me = (Map.Entry) it.next();
      EntityBean eb = (EntityBean) me.getKey();
      if ((eb.getId() == event.getId()) && (eb.getName().equals(event.getName()))) {
        logger.warning("found OLD bean, return true");
        returnMe = true;
      }
    }
    return returnMe;
  }

  //so as not to get null pointer returns, tbh
  public ArrayList returnDouble(HashMap hm, EntityBean event) {
    ArrayList al = new ArrayList();
    Set s = hm.entrySet();
    for (Iterator it = s.iterator(); it.hasNext();) {
      Map.Entry me = (Map.Entry) it.next();
      EntityBean eb = (EntityBean) me.getKey();
      if ((eb.getId() == event.getId()) && (eb.getName().equals(event.getName()))) {
        //logger.warning("found OLD bean, return true");
        al = (ArrayList) me.getValue();
      }
    }
    return al;
  }

  //so as to remove the object correctly, tbh
  public HashMap removeDouble(HashMap hm, EntityBean event) {
    ArrayList al = new ArrayList();
    Set s = hm.entrySet();
    EntityBean removeMe = new EntityBean();
    for (Iterator it = s.iterator(); it.hasNext();) {
      Map.Entry me = (Map.Entry) it.next();
      EntityBean eb = (EntityBean) me.getKey();
      if ((eb.getId() == event.getId()) && (eb.getName().equals(event.getName()))) {
        logger.warning("found OLD bean, remove it");
        removeMe = eb;
      }
    }
    hm.remove(removeMe);
    return hm;
  }

  public int getDefinitionIdFromStudyEventId(int studyEventId) {
    int answer = 0;

    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyEventId));

    ArrayList rows = select(digester.getQuery("getDefinitionIdFromStudyEventId"), variables);

    if (rows.size() > 0) {
      HashMap row = (HashMap) rows.get(0);
      answer = ((Integer) row.get("study_event_definition_id")).intValue();
    }

    return answer;
  }
  
  public ArrayList findAllByStudySubject(StudySubjectBean ssb) {
  	HashMap variables = new HashMap();
  	variables.put(new Integer(1), new Integer(ssb.getId()));
  	
  	return executeFindAllQuery("findAllByStudySubject", variables);
  }
  
  public ArrayList findAllByStudySubjectAndDefinition(StudySubjectBean ssb,
      StudyEventDefinitionBean sed) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ssb.getId()));
    variables.put(new Integer(2), new Integer(sed.getId()));
    
    return executeFindAllQuery("findAllByStudySubjectAndDefinition", variables);
  }
  
  private HashMap subjDefs;
  public void updateSampleOrdinals_v092() {
  	subjDefs = new HashMap();
  	this.unsetTypeExpected();
  	this.setTypeExpected(1, TypeNames.INT);
  	this.setTypeExpected(2, TypeNames.INT);
  	this.setTypeExpected(3, TypeNames.INT);
  	this.setTypeExpected(3, TypeNames.INT);
  	
  	ArrayList rows = select("SELECT study_event_id, study_event_definition_id, study_subject_id, sample_ordinal FROM study_event ORDER BY study_subject_id ASC, study_event_definition_id ASC, sample_ordinal ASC");
  	
  	for (int i = 0; i < rows.size(); i++) {
  		HashMap row = (HashMap) rows.get(i);
  		
  		Integer studyEventId = (Integer) row.get("study_event_id");
  		Integer studyEventDefinitionId = (Integer) row.get("study_event_definition_id");
  		Integer studySubjectId = (Integer) row.get("study_subject_id");
  		
  		addEvent(studySubjectId, studyEventDefinitionId, studyEventId);
  	}
  	
  	Iterator keysIt = subjDefs.keySet().iterator();
  	while (keysIt.hasNext()) {
  		String key = (String) keysIt.next();
  		ArrayList events = (ArrayList) subjDefs.get(key);
  		
  		for (int i = 0; i < events.size(); i++) {
  			Integer id = (Integer) events.get(i);
  			if (id != null) {
  				int ordinal = i + 1;
  				System.out.println("UPDATE study_event SET sample_ordinal = " + ordinal + " WHERE study_event_id = " + id);
//  				execute("UPDATE study_event SET sample_ordinal = " + ordinal + " WHERE study_event_id = " + id);
  			}
  		}
  	}
  }
  
  private void addEvent(Integer studySubjectId, Integer studyEventDefinitionId, Integer studyEventId) {
  	if ((studySubjectId == null) || (studyEventDefinitionId == null) || (studyEventId == null)) {
  		return ;
  	}
  	
  	String key = studySubjectId + "-" + studyEventDefinitionId;
  	
	ArrayList events;
  	if (subjDefs.containsKey(key)) {
  		events = (ArrayList) subjDefs.get(key);
  	}
  	else {
  		events = new ArrayList();
  	}
  	events.add(studyEventId);
  	System.out.println("putting in key: " + key + " seid: " + studyEventId);
  	subjDefs.put(key, events);
  }
}