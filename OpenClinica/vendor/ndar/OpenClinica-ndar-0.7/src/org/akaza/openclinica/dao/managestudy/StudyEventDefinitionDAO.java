/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.managestudy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
//import org.akaza.openclinica.bean.core.Status;

import org.akaza.openclinica.dao.core.*;

import org.akaza.openclinica.bean.managestudy.*;

/**
 * @author thickerson
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StudyEventDefinitionDAO extends AuditableEntityDAO {

  private void setQueryNames() {
    findAllByStudyName = "findAllByStudy";
    findAllActiveByStudyName = "findAllActiveByStudy";
    findByPKAndStudyName = "findByPKAndStudy";
  }

  public StudyEventDefinitionDAO(DataSource ds) {
    super(ds);
    setQueryNames();
  }

  public StudyEventDefinitionDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
    setQueryNames();
  }

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_STUDYEVENTDEFNITION;
  }

  public void setTypesExpected() {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.STRING);
    this.setTypeExpected(4, TypeNames.STRING);
    this.setTypeExpected(5, TypeNames.BOOL);
    this.setTypeExpected(6, TypeNames.STRING);
    this.setTypeExpected(7, TypeNames.STRING);
    //int int date date int
    this.setTypeExpected(8, TypeNames.INT);
    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.DATE);
    this.setTypeExpected(11, TypeNames.DATE);
    this.setTypeExpected(12, TypeNames.INT);
    this.setTypeExpected(13, TypeNames.INT);
  }

  /**
   * <P>
   * findNextKey, a method to return a simple int from the database.
   * 
   * @return int, which is the next primary key for creating a study event
   *         definition.
   */
  public int findNextKey() {
    this.unsetTypeExpected();
    Integer keyInt = new Integer(0);
    this.setTypeExpected(1, TypeNames.INT);
    ArrayList alist = this.select(digester.getQuery("findNextKey"));
    Iterator it = alist.iterator();
    if (it.hasNext()) {
      HashMap key = (HashMap) it.next();
      keyInt = (Integer) key.get("key");
    }
    return keyInt.intValue();
  }

  public EntityBean create(EntityBean eb) {
    //study_event_definition_id ,
    //STUDY_ID, NAME,DESCRIPTION, REPEATING, STUDY_EVENT_DEFINITION_TYPE, CATEGORY, OWNER_ID,
    // STATUS_ID, DATE_CREATED,ordinal
    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) eb;
    sedb.setId(this.findNextKey());
    logger.info("***id:" + sedb.getId());
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(sedb.getId()));
    variables.put(new Integer(2), new Integer(sedb.getStudyId()));
    variables.put(new Integer(3), sedb.getName());
    variables.put(new Integer(4), sedb.getDescription());
    variables.put(new Integer(5), new Boolean(sedb.isRepeating()));
    variables.put(new Integer(6), sedb.getType());
    variables.put(new Integer(7), sedb.getCategory());
    variables.put(new Integer(8), new Integer(sedb.getOwnerId()));
    variables.put(new Integer(9), new Integer(sedb.getStatus().getId()));
    variables.put(new Integer(10), new Integer(sedb.getOrdinal()));
    this.execute(digester.getQuery("create"), variables);

    return sedb;
  }

  public EntityBean update(EntityBean eb) {
    StudyEventDefinitionBean sedb = (StudyEventDefinitionBean) eb;
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(sedb.getStudyId()));
    variables.put(new Integer(2), sedb.getName());
    variables.put(new Integer(3), sedb.getDescription());
    variables.put(new Integer(4), new Boolean(sedb.isRepeating()));
    variables.put(new Integer(5), sedb.getType());
    variables.put(new Integer(6), sedb.getCategory());
    variables.put(new Integer(7), new Integer(sedb.getStatus().getId()));
    variables.put(new Integer(8), new Integer(sedb.getUpdaterId()));
    variables.put(new Integer(9), new Integer(sedb.getOrdinal()));
    variables.put(new Integer(10), new Integer(sedb.getId()));
    this.execute(digester.getQuery("update"), variables);
    return eb;
  }

  public Object getEntityFromHashMap(HashMap hm) {
    StudyEventDefinitionBean eb = new StudyEventDefinitionBean();

    this.setEntityAuditInformation(eb, hm);
    //set dates and ints first, then strings
    //create a sub-function in auditable entity dao that can do this?
    Integer sedId = (Integer) hm.get("study_event_definition_id");
    eb.setId(sedId.intValue());

    Integer studyId = (Integer) hm.get("study_id");
    eb.setStudyId(studyId.intValue());
    Integer ordinal = (Integer) hm.get("ordinal");
    eb.setOrdinal(ordinal.intValue());
    Boolean repeating = (Boolean) hm.get("repeating");
    eb.setRepeating(repeating.booleanValue());

    //below functions changed by get entity audit information functions

    /*
     * Integer ownerId = (Integer)hm.get("owner_id");
     * eb.setOwnerId(ownerId.intValue()); Integer updaterId =
     * (Integer)hm.get("update_id"); eb.setUpdaterId(updaterId.intValue());
     * Integer statusId = (Integer)hm.get("status_id");
     * eb.setStatus(Status.get(statusId.intValue()));
     * 
     * Date dateCreated = (Date)hm.get("date_created"); Date dateUpdated =
     * (Date)hm.get("date_updated"); eb.setCreatedDate(dateCreated);
     * eb.setUpdatedDate(dateUpdated);
     */
    eb.setName((String) hm.get("name"));
    eb.setDescription((String) hm.get("description"));
    eb.setType((String) hm.get("study_event_definition_type"));
    eb.setCategory((String) hm.get("category"));
    return eb;
  }

  public ArrayList findAllWithStudyEvent(StudyBean currentStudy) {
    ArrayList answer = new ArrayList();

    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(currentStudy.getId()));
    variables.put(new Integer(2), new Integer(currentStudy.getId()));

    ArrayList alist = this.select(digester.getQuery("findAllWithStudyEvent"), variables);

    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventDefinitionBean seb = (StudyEventDefinitionBean) this
          .getEntityFromHashMap((HashMap) it.next());
      answer.add(seb);
    }

    return answer;
  }

  public Collection findAll() {
    this.setTypesExpected();
    ArrayList alist = this.select(digester.getQuery("findAll"));
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this
          .getEntityFromHashMap((HashMap) it.next());
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
//    System.out.println("reached findbypk, id is " + ID);
    StudyEventDefinitionBean eb = new StudyEventDefinitionBean();
    this.setTypesExpected();
//    System.out.println("reached vars");

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));
//    System.out.println("reached getquery, id is " + ID);

    String sql = digester.getQuery("findByPK");
//    System.out.println("sql is: " + sql);
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();
//    System.out.println("finished selecting");
    if (it.hasNext()) {
//      System.out.println("getting from hashmap");
      eb = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
    }
//    System.out.println("finished, id is " + eb.getId());
    return eb;
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

  /**
   * @param eventDefinitionCRFId
   *          The id of an event definition crf.
   * @return the study event definition bean for the specified event definition
   *         crf.
   */
  public StudyEventDefinitionBean findByEventDefinitionCRFId(int eventDefinitionCRFId) {
    StudyEventDefinitionBean answer = new StudyEventDefinitionBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(eventDefinitionCRFId));

    String sql = digester.getQuery("findByEventDefinitionCRFId");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      answer = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return answer;
  }

  public Collection findAllByStudyAndLimit(int studyId) {
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(studyId));
    variables.put(new Integer(2), new Integer(studyId));
    ArrayList alist = this.select(digester.getQuery("findAllByStudyAndLimit"), variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      StudyEventDefinitionBean eb = (StudyEventDefinitionBean) this
          .getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;

  }
  
  /**
   * Finds the definition by definition name and study id
   * @param name
   * @param studyId
   * @return
   */
  public StudyEventDefinitionBean findByNameAndStudy(String name,int studyId) {
    StudyEventDefinitionBean answer = new StudyEventDefinitionBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), name);
    variables.put(new Integer(2), new Integer(studyId));
    variables.put(new Integer(3), new Integer(studyId));

    String sql = digester.getQuery("findByNameAndStudy");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      answer = (StudyEventDefinitionBean) this.getEntityFromHashMap((HashMap) it.next());
    }

    return answer;
  }


}