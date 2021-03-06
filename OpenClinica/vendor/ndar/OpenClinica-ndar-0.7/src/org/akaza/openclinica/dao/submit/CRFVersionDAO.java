/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.submit;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

/**
 * <p>
 * CRFVersionDAO.java, the data access object for versions of instruments in the
 * database. Each of these are related to Sections, a versioning map that links
 * them with Items, and an Event, which then links to a Study.
 * 
 * @author thickerson
 * 
 *  
 */
public class CRFVersionDAO extends AuditableEntityDAO {

  protected void setDigesterName() {
    digesterName = SQLFactory.getInstance().DAO_CRFVERSION;
  }

  public CRFVersionDAO(DataSource ds) {
    super(ds);
  }

  public CRFVersionDAO(DataSource ds, DAODigester digester) {
    super(ds);
    this.digester = digester;
  }

  public EntityBean update(EntityBean eb) {
   //UPDATE CRF_VERSION SET CRF_ID=?,STATUS_ID=?,NAME=?,
	//DESCRIPTION=?,DATE_UPDATED=NOW(),UPDATE_ID=?,REVISION_NOTES =?, PUBLISHER_ID=? WHERE 
	//CRF_VERSION_ID=?
    CRFVersionBean ib = (CRFVersionBean) eb;
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(ib.getCrfId()));
    variables.put(new Integer(2), new Integer(ib.getStatus().getId()));
    variables.put(new Integer(3), ib.getName());
    variables.put(new Integer(4), ib.getDescription());
    variables.put(new Integer(5), new Integer(ib.getUpdater().getId()));
    variables.put(new Integer(6), ib.getRevisionNotes());
    if (ib.getPublisherId() == 0) {
      nullVars.put(new Integer(7), new Integer(Types.INTEGER));
      variables.put(new Integer(7), null);
    } else {
      variables.put(new Integer(7), new Integer(ib.getPublisherId()));
    }

    //where id=
    variables.put(new Integer(8), new Integer(ib.getId()));
    this.execute(digester.getQuery("update"), variables);
    return eb;
  }

  public EntityBean create(EntityBean eb) {   
	
    return eb;
   
  }

  public void setTypesExpected() {
    //crf_version_id serial NOT NULL,
    //crf_id numeric NOT NULL,
    //name varchar(255),
    //description varchar(4000),

    //revision_notes varchar(255),
    //status_id numeric,
    //date_created date,

    //date_updated date,
    //owner_id numeric,
    //update_id numeric,
	//publisher_id numeric
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.INT);
    this.setTypeExpected(3, TypeNames.STRING);
    this.setTypeExpected(4, TypeNames.STRING);

    this.setTypeExpected(5, TypeNames.STRING);
    this.setTypeExpected(6, TypeNames.INT);
    this.setTypeExpected(7, TypeNames.DATE);

    this.setTypeExpected(8, TypeNames.DATE);
    this.setTypeExpected(9, TypeNames.INT);
    this.setTypeExpected(10, TypeNames.INT);
    this.setTypeExpected(11, TypeNames.INT);

  }

  public Object getEntityFromHashMap(HashMap hm) {
    //CRF_VERSION_ID NAME DESCRIPTION
    //CRF_ID STATUS_ID PUBLISHER_ID DATE_CREATED DATE_UPDATED
    // OWNER_ID REVISION_NUMBER UPDATE_ID
    CRFVersionBean eb = new CRFVersionBean();
    super.setEntityAuditInformation(eb, hm);
    eb.setId(((Integer) hm.get("crf_version_id")).intValue());

    eb.setName((String) hm.get("name"));
    eb.setDescription((String) hm.get("description"));
    eb.setCrfId(((Integer) hm.get("crf_id")).intValue());
    eb.setRevisionNotes((String) hm.get("revision_notes"));
    eb.setPublisherId(hm.get("publisher_id")==null ? 0 : ((Integer) hm.get("publisher_id")).intValue());
    return eb;
  }

  /**
   * Do not use!  Only here to satisfy requirements of superclass
   */
  public Collection<CRFVersionBean> findAll() {
    ArrayList<CRFVersionBean> al = null;

    return al;
  }

  /**
   * Do not use!  Only here to satisfy requirements of superclass
   */
  public Collection<CRFVersionBean> findAll(String strOrderByColumn, boolean blnAscendingSort,
      String strSearchPhrase) {
    ArrayList<CRFVersionBean> al = null;

    return al;
  }

  public Collection<CRFVersionBean> findAllByCRF(int crfId) {
    this.setTypesExpected();
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(crfId));
    String sql = digester.getQuery("findAllByCRF");
    ArrayList alist = this.select(sql, variables);
    Collection<CRFVersionBean> al = new ArrayList<CRFVersionBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      CRFVersionBean eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection<CRFVersionBean> findAllActiveByCRF(int crfId) {
    this.setTypesExpected();
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(crfId));
    String sql = digester.getQuery("findAllActiveByCRF");
    ArrayList alist = this.select(sql, variables);
    Collection<CRFVersionBean> al = new ArrayList<CRFVersionBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      CRFVersionBean eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) it.next());
      al.add(eb);
    }
    return al;
  }

  public Collection<ItemBean> findItemFromMap(int versionId) {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.STRING);
    this.setTypeExpected(3, TypeNames.INT);
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(versionId));
    String sql = digester.getQuery("findItemFromMap");
    ArrayList alist = this.select(sql, variables);
    Collection<ItemBean> al = new ArrayList<ItemBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      ItemBean eb = new ItemBean();
      HashMap hm = (HashMap) it.next();
      eb.setId(((Integer) hm.get("item_id")).intValue());
      eb.setName((String) hm.get("name"));
      Integer ownerId = (Integer) hm.get("owner_id");
      eb.setOwnerId(ownerId.intValue());

      al.add(eb);
    }
    return al;
  }

  public Collection<ItemBean> findItemUsedByOtherVersion(int versionId) {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);
    this.setTypeExpected(2, TypeNames.STRING);
    this.setTypeExpected(3, TypeNames.INT);
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(versionId));
    String sql = digester.getQuery("findItemUsedByOtherVersion");
    ArrayList alist = this.select(sql, variables);
    Collection<ItemBean> al = new ArrayList<ItemBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      ItemBean eb = new ItemBean();
      HashMap hm = (HashMap) it.next();
      eb.setId(((Integer) hm.get("item_id")).intValue());
      eb.setName((String) hm.get("name"));
      eb.setOwnerId(((Integer) hm.get("owner_id")).intValue());
      al.add(eb);
    }
    return al;
  }
  
  public ArrayList<ItemBean> findNotSharedItemsByVersion(int versionId) {
    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);    
    this.setTypeExpected(2, TypeNames.STRING);
    this.setTypeExpected(3, TypeNames.INT);
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(versionId));
    variables.put(new Integer(2), new Integer(versionId));
    String sql = digester.getQuery("findNotSharedItemsByVersion");
    ArrayList alist = this.select(sql, variables);
    ArrayList<ItemBean> al = new ArrayList<ItemBean>();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      ItemBean eb = new ItemBean();
      HashMap hm = (HashMap) it.next();
      eb.setId(((Integer) hm.get("item_id")).intValue());  
      eb.setName((String) hm.get("name"));
      eb.setOwnerId(((Integer) hm.get("owner_id")).intValue());
      al.add(eb);
    }
    return al;
  }

  public boolean isItemUsedByOtherVersion(int versionId) {
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(versionId));
    String sql = digester.getQuery("isItemUsedByOtherVersion");
    ArrayList alist = this.select(sql, variables);
    ArrayList al = new ArrayList();
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      return true;
    }
    return false;
  }

  public boolean hasItemData(int itemId) {
    this.setTypesExpected();
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(itemId));
    String sql = digester.getQuery("hasItemData");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();
    while (it.hasNext()) {
      return true;
    }
    return false;
  }

  public EntityBean findByPK(int ID) {
    CRFVersionBean eb = new CRFVersionBean();
    this.setTypesExpected();

    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(ID));

    String sql = digester.getQuery("findByPK");
    ArrayList alist = this.select(sql, variables);
    Iterator it = alist.iterator();

    if (it.hasNext()) {
      eb = (CRFVersionBean) this.getEntityFromHashMap((HashMap) it.next());
    }
    return eb;

  }

  /**
   * Deletes a CRF version
   */
  public void delete(int id) {
    HashMap variables = new HashMap();
    variables.put(new Integer(1), new Integer(id));

    String sql = digester.getQuery("delete");
    this.execute(sql, variables);
  }

  /**
   * Generates all the delete queries for deleting a version
   * 
   * @param versionId
   * @param items  
   */
  public ArrayList generateDeleteQueries(int versionId, ArrayList items) {
    ArrayList sqls = new ArrayList();
    String sql = digester.getQuery("deleteItemMetaDataByVersion") + versionId;
    sqls.add(sql);
    sql = digester.getQuery("deleteSectionsByVersion") + versionId;
    sqls.add(sql);
    sql = digester.getQuery("deleteItemMapByVersion") + versionId;
    sqls.add(sql);
    sql = digester.getQuery("deleteSkipRulesByVersion") + versionId;
    sqls.add(sql);
    sql = digester.getQuery("deleteScoreRefTableByVersion") + versionId;
    sqls.add(sql);
    
    for (int i = 0; i < items.size(); i++) {
      ItemBean item = (ItemBean) items.get(i);
      sql = digester.getQuery("deleteItemsByVersion") + item.getId();
      sqls.add(sql);
    }
    
    sql = digester.getQuery("deleteResponseSetByVersion") + versionId;
    sqls.add(sql);

    sql = digester.getQuery("deleteResponseSetByVersion") + versionId;
    sqls.add(sql);
    

    sql = digester.getQuery("delete") + versionId;
    sqls.add(sql);
    return sqls;

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

  public int getCRFIdFromCRFVersionId(int CRFVersionId) {
    int answer = 0;

    this.unsetTypeExpected();
    this.setTypeExpected(1, TypeNames.INT);

    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(CRFVersionId));

    String sql = digester.getQuery("getCRFIdFromCRFVersionId");
    ArrayList rows = select(sql, variables);

    if (rows.size() > 0) {
      HashMap h = (HashMap) rows.get(0);
      answer = ((Integer) h.get("crf_id")).intValue();
    }

    return answer;
  }

  public ArrayList findAllByCRFId(int CRFId) {
    HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
    variables.put(new Integer(1), new Integer(CRFId));

    return executeFindAllQuery("findAllByCRFId", variables);
  }
}