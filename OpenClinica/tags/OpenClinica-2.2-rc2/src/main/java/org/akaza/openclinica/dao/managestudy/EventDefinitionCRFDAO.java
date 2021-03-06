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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * @author jxu
 *  
 */
public class EventDefinitionCRFDAO extends AuditableEntityDAO {
  // private DAODigester digester;

  private void setQueryNames() {
    getCurrentPKName = "getCurrentPK";    
  }
  
  public EventDefinitionCRFDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public EventDefinitionCRFDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_EVENTDEFINITIONCRF;
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.INT);
    this.setTypeExpected(4, TypeNames.INT);
    this.setTypeExpected(5, TypeNames.BOOL);

    this.setTypeExpected(6, TypeNames.BOOL);
    this.setTypeExpected(7, TypeNames.BOOL);
    this.setTypeExpected(8, TypeNames.BOOL);
    this.setTypeExpected(9, TypeNames.STRING);
    this.setTypeExpected(10, TypeNames.INT);
    
    this.setTypeExpected(11, TypeNames.INT);
    this.setTypeExpected(12, TypeNames.INT);
    this.setTypeExpected(13, TypeNames.DATE);
    this.setTypeExpected(14, TypeNames.DATE);
    this.setTypeExpected(15, TypeNames.INT);
    this.setTypeExpected(16, TypeNames.INT);
  }

  /**
   * <p>
   * getEntityFromHashMap, the method that gets the object from the database
   * query.
   */
  public Object getEntityFromHashMap(HashMap hm) {
    EventDefinitionCRFBean eb = new EventDefinitionCRFBean();
    super.setEntityAuditInformation(eb, hm);
    //EVENT_DEFINITION_CRF_ID STUDY_EVENT_DEFINITION_ID STUDY_ID
    //CRF_ID REQUIRED_CRF DOUBLE_ENTRY REQUIRE_ALL_TEXT_FILLED
    //DECISION_CONDITIONS DEFAULT_VERSION_ID STATUS_ID OWNER_ID
    //DATE_CREATED DATE_UPDATED UPDATE_ID
    eb.setId(((Integer) hm.get("event_definition_crf_id")).intValue());
    eb.setStudyEventDefinitionId(((Integer) hm.get("study_event_definition_id")).intValue());
    eb.setStudyId(((Integer) hm.get("study_id")).intValue());
    eb.setCrfId(((Integer) hm.get("crf_id")).intValue());
    eb.setRequiredCRF(((Boolean) hm.get("required_crf")).booleanValue());
    eb.setDoubleEntry(((Boolean) hm.get("double_entry")).booleanValue());
    eb.setRequireAllTextFilled(((Boolean) hm.get("require_all_text_filled")).booleanValue());
    eb.setDecisionCondition(((Boolean) hm.get("decision_conditions")).booleanValue());
    eb.setNullValues((String)hm.get("null_values"));
    eb.setDefaultVersionId(((Integer) hm.get("default_version_id")).intValue());
    eb.setOrdinal(((Integer) hm.get("ordinal")).intValue());
    return eb;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it
          .next());
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
      EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it
          .next());
      al.add(eb);
    }
    return al;
  }
  
  public Collection findAllByCRF(int crfId) {
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(crfId));

    String sql = digester.getQuery("findByCRFId");
    ArrayList alist = this.select(sql, variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it
          .next());
      al.add(eb);
    }
    return al;
  }

  public Collection findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList al = new ArrayList();

    return al;
  }

  public EntityBean findByPK(int ID) {
    EventDefinitionCRFBean eb = new EventDefinitionCRFBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return eb;
  }

  /**
   * Creates a new studysubject
   */
  public EntityBean create(EntityBean eb) {
    EventDefinitionCRFBean sb = (EventDefinitionCRFBean) eb;
    HashMap variables = new HashMap();
    //INSERT INTO EVENT_DEFINITION_CRF
    // (STUDY_EVENT_DEFINITION_ID,STUDY_ID,CRF_ID,REQUIRED_CRF,
    //DOUBLE_ENTRY,REQUIRE_ALL_TEXT_FILLED,DECISION_CONDITIONS,
    //NULL_VALUES,DEFAULT_VERSION_ID,STATUS_ID,OWNER_ID,DATE_CREATED)
    // VALUES (?,?,?,?,?,?,?,?,?,?,NOW())
    variables.put(new Integer(1), new Integer(sb.getStudyEventDefinitionId()));
    variables.put(new Integer(2), new Integer(sb.getStudyId()));
    variables.put(new Integer(3), new Integer(sb.getCrfId()));
    variables.put(new Integer(4), new Boolean(sb.isRequiredCRF()));
    variables.put(new Integer(5), new Boolean(sb.isDoubleEntry()));
    variables.put(new Integer(6), new Boolean(sb.isRequireAllTextFilled()));
    variables.put(new Integer(7), new Boolean(sb.isDecisionCondition()));
    variables.put(new Integer(8), sb.getNullValues());
    variables.put(new Integer(9), new Integer(sb.getDefaultVersionId()));   
    variables.put(new Integer(10), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(11), new Integer(sb.getOwnerId()));
    variables.put(new Integer(12), new Integer(sb.getOrdinal()));
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
    EventDefinitionCRFBean sb = (EventDefinitionCRFBean) eb;
    HashMap variables = new HashMap();
    //UPDATE EVENT_DEFINITION_CRF SET
    //STUDY_EVENT_DEFINITION_ID=?,STUDY_ID=?,CRF_ID=?, REQUIRED_CRF=?,
    //DOUBLE_ENTRY=?,REQUIRE_ALL_TEXT_FILLED=?,DECISION_CONDITIONS=?,
    //DEFAULT_VERSION_ID=?,STATUS_ID=?,DATE_UPDATED=?,UPDATE_ID=?,ordinal=?
    //WHERE EVENT_DEFINITION_CRF_ID=?
    variables.put(new Integer(1), new Integer(sb.getStudyEventDefinitionId()));
    variables.put(new Integer(2), new Integer(sb.getStudyId()));
    variables.put(new Integer(3), new Integer(sb.getCrfId()));
    variables.put(new Integer(4), new Boolean(sb.isRequiredCRF()));
    variables.put(new Integer(5), new Boolean(sb.isDoubleEntry()));
    variables.put(new Integer(6), new Boolean(sb.isRequireAllTextFilled()));
    variables.put(new Integer(7), new Boolean(sb.isDecisionCondition()));
    variables.put(new Integer(8), sb.getNullValues());
    variables.put(new Integer(9), new Integer(sb.getDefaultVersionId()));
    variables.put(new Integer(10), new Integer(sb.getStatus().getId()));
    variables.put(new Integer(11), new java.util.Date());//DATE_Updated
    variables.put(new Integer(12), new Integer(sb.getUpdater().getId()));
    variables.put(new Integer(13), new Integer(sb.getOrdinal()));
    variables.put(new Integer(14), new Integer(sb.getId()));

    String sql = digester.getQuery("update");
    this.execute(sql, variables);

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

  public ArrayList findByDefaultVersion(int versionId) {
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(versionId));

    String sql = digester.getQuery("findByDefaultVersion");
    ArrayList alist = this.select(sql, variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      EventDefinitionCRFBean eb = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it
          .next());
      al.add(eb);
    }
    return al;
  }
  
  public ArrayList findAllByEventDefinitionId(int eventDefinitionId) {
  	HashMap variables = new HashMap();
  	variables.put(new Integer(1), new Integer(eventDefinitionId));
  	
  	return executeFindAllQuery("findAllByEventDefinitionId", variables);
  }
  
  public ArrayList findAllActiveByEventDefinitionId(int eventDefinitionId) {
  	HashMap variables = new HashMap();
  	variables.put(new Integer(1), new Integer(eventDefinitionId));
  	
  	return executeFindAllQuery("findAllActiveByEventDefinitionId", variables);
  }
  
  /**
   * isRequiredInDefinition, looks at a specific EventCRF and determines if it's required or not
   * @return boolean to tell us if it's required or not.
   */
  public boolean isRequiredInDefinition(int crfVersionId, int studyEventId) {
	  
	  /*select distinct event_definition_crf.required_crf from event_definition_crf, event_crf, crf_version, study_event where 
		crf_version.crf_version_id = 29 and 
		crf_version.crf_version_id = event_crf.crf_version_id and 
		crf_version.crf_id = event_definition_crf.crf_id and
		event_definition_crf.study_event_definition_id = study_event.study_event_definition_id
		and study_event.study_event_id = 91*/
	  
	  this.unsetTypeExpected();
	  this.setTypeExpected(1, TypeNames.BOOL);
	  HashMap variables = new HashMap();
	  variables.put(new Integer(2), new Integer(studyEventId));
	  variables.put(new Integer(1), new Integer(crfVersionId));
	  
	  String sql = digester.getQuery("isRequiredInDefinition");

	  ArrayList alist = this.select(sql, variables);
	  Iterator it = alist.iterator();
	  Boolean answer = false;
	  while (it.hasNext()) {
		  HashMap hm = (HashMap) it.next();
	      answer = (Boolean) hm.get("required_crf");
	  }

	  System.out.println("We are returning "+answer.toString()+
			  " for crfVersionId "+crfVersionId+
			  " and studyEventId "+studyEventId);
	  return answer.booleanValue();
  }
  /**
   * @param studyEventId The requested study event id.
   * @param crfVersionId The requested CRF version id.
   * @return The event definition crf which defines the study event and crf version.
   */
  public EventDefinitionCRFBean findByStudyEventIdAndCRFVersionId(int studyEventId, int crfVersionId) {
  	EventDefinitionCRFBean answer = new EventDefinitionCRFBean();
  	
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyEventId));
    variables.put(new Integer(2), new Integer(crfVersionId));

    String sql = digester.getQuery("findByStudyEventIdAndCRFVersionId");

    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    while (it.hasNext()) {
      answer = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it.next());
    }
  	
  	return answer;
  }
  
  /**
   * @param studyEventDefinitionId The study event definition of the desired event definition crf.
   * @param crfId The CRF of the desired event definition crf.
   * @return The event definition crf for the specified study event definition and CRF.
   */
  public EventDefinitionCRFBean findByStudyEventDefinitionIdAndCRFId(int studyEventDefinitionId, int crfId) {
  	EventDefinitionCRFBean answer = new EventDefinitionCRFBean();
  	
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyEventDefinitionId));
    variables.put(new Integer(2), new Integer(crfId));

    String sql = digester.getQuery("findByStudyEventDefinitionIdAndCRFId");

    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    while (it.hasNext()) {
      answer = (EventDefinitionCRFBean) this.getEntityFromHashMap((HashMap) it.next());
    }
  	
  	return answer;  	
  }
}