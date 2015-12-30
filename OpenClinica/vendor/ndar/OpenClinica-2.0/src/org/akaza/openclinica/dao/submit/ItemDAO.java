/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.submit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
/**
 * @author thickerson
 *
 * 
 */
public class ItemDAO extends AuditableEntityDAO {
	// private DAODigester digester;
	
	public ItemDAO(DataSource ds) {
		super(ds);
	}
	
	public ItemDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}
	
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEM;
	}

	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1,TypeNames.INT);
		this.setTypeExpected(2,TypeNames.STRING);
		this.setTypeExpected(3,TypeNames.STRING);
		this.setTypeExpected(4,TypeNames.STRING);
		this.setTypeExpected(5,TypeNames.BOOL);//phi status
		this.setTypeExpected(6,TypeNames.INT);//data type id
		this.setTypeExpected(7,TypeNames.INT);//reference type id
		this.setTypeExpected(8,TypeNames.INT);//status id
		this.setTypeExpected(9,TypeNames.INT);//owner id
		this.setTypeExpected(10,TypeNames.DATE);//created
		this.setTypeExpected(11,TypeNames.DATE);//updated
		this.setTypeExpected(12,TypeNames.INT);//update id
	}
	
	public EntityBean update(EntityBean eb) {
		ItemBean ib = (ItemBean)eb;
		HashMap variables = new HashMap();
		variables.put(new Integer(1), ib.getName());
		variables.put(new Integer(2), ib.getDescription());
		variables.put(new Integer(3), ib.getUnits());
		variables.put(new Integer(4), new Boolean(ib.isPhiStatus()));
		variables.put(new Integer(5), new Integer(ib.getItemDataTypeId()));
		variables.put(new Integer(6), new Integer(ib.getItemReferenceTypeId()));
		variables.put(new Integer(7), new Integer(ib.getStatus().getId()));
		variables.put(new Integer(8), new Integer(ib.getUpdaterId()));
		variables.put(new Integer(9), new Integer(ib.getId()));
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}
	
	public EntityBean create(EntityBean eb) {
		ItemBean ib = (ItemBean)eb;
		//per the create sql statement
		HashMap variables = new HashMap();
		variables.put(new Integer(1), ib.getName());
		variables.put(new Integer(2), ib.getDescription());
		variables.put(new Integer(3), ib.getUnits());
		variables.put(new Integer(4), new Boolean(ib.isPhiStatus()));
		variables.put(new Integer(5), new Integer(ib.getItemDataTypeId()));
		variables.put(new Integer(6), new Integer(ib.getItemReferenceTypeId()));
		variables.put(new Integer(7), new Integer(ib.getStatus().getId()));
		variables.put(new Integer(8), new Integer(ib.getOwnerId()));
		//date_created=now() in Postgres
		this.execute(digester.getQuery("create"), variables);
		//set the id here????
		return eb;
	}
	
	public Object getEntityFromHashMap(HashMap hm) {
		ItemBean eb = new ItemBean();
		//below inserted to find out a class cast exception, tbh
		Date dateCreated = (Date) hm.get("date_created");
	    Date dateUpdated = (Date) hm.get("date_updated");
	    Integer statusId = (Integer) hm.get("status_id");
	    Integer ownerId = (Integer) hm.get("owner_id");
	    Integer updateId = (Integer) hm.get("update_id");

	    eb.setCreatedDate(dateCreated);
	    eb.setUpdatedDate(dateUpdated);
	    eb.setStatus(Status.get(statusId.intValue()));
	    eb.setOwnerId(ownerId.intValue());
	    eb.setUpdaterId(updateId.intValue());
		//eb = (ItemBean)this.getEntityAuditInformation(hm);
		eb.setName((String)hm.get("name"));
		eb.setId(((Integer)hm.get("item_id")).intValue());
		eb.setDescription((String)hm.get("description"));
		eb.setUnits((String)hm.get("units"));
		eb.setPhiStatus(((Boolean)hm.get("phi_status")).booleanValue());
		eb.setItemDataTypeId(
				((Integer)hm.get("item_data_type_id")).intValue());
		eb.setItemReferenceTypeId(
				((Integer)hm.get("item_reference_type_id")).intValue());
        //System.out.println("item name|date type id" + eb.getName() + "|" + eb.getItemDataTypeId());
		eb.setDataType(ItemDataType.get(eb.getItemDataTypeId()));
		//the rest should be all set
		return eb;
	}
	
	public Collection findAll() {
		this.setTypesExpected();
	    ArrayList alist = this.select(digester.getQuery("findAll"));
	    ArrayList al = new ArrayList();
	    Iterator it = alist.iterator();
	    while (it.hasNext()) {
	      ItemBean eb = 
	      	(ItemBean) this.getEntityFromHashMap((HashMap) it.next());
	      al.add(eb);
	    }
	    return al;
	}
	
	public Collection findAll(String strOrderByColumn,
			boolean blnAscendingSort,
			String strSearchPhrase) {
		ArrayList al = new ArrayList();
		
		return al;
	}
	
	public ArrayList findAllParentsBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(sectionId));
		
		return this.executeFindAllQuery("findAllParentsBySectionId", variables);
	}
	
	public ArrayList findAllItemsByVersionId(int versionId) {
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(versionId));
		
		return this.executeFindAllQuery("findAllItemsByVersionId", variables);
	}
	
	public ArrayList findAllVersionsByItemId(int itemId) {
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(itemId));
		String sql = digester.getQuery("findAllVersionsByItemId");
	    ArrayList alist = this.select(sql, variables);
	    ArrayList al = new ArrayList();
	    Iterator it = alist.iterator();
	    while (it.hasNext()) {
	      Integer versionId = (Integer)(((HashMap)it.next()).get("crf_version_id"));
	      al.add(versionId);
	    }
	    return al;
		
		
	}
	
	public ArrayList findAllActiveByCRF(CRFBean crf) {
		HashMap variables = new HashMap();
		this.setTypesExpected();	
		this.setTypeExpected(13,TypeNames.INT);//crf_version_id
		this.setTypeExpected(14,TypeNames.STRING);//version name
		variables.put(new Integer(1), new Integer(crf.getId()));
		String sql = digester.getQuery("findAllActiveByCRF");
	    ArrayList alist = this.select(sql, variables);
	    ArrayList al = new ArrayList();
	    Iterator it = alist.iterator();
	    while (it.hasNext()) {	     
	      HashMap hm = (HashMap)it.next();
	      ItemBean eb = 
	      	(ItemBean) this.getEntityFromHashMap(hm); 
	      Integer versionId = (Integer)(hm.get("crf_version_id"));
	      String versionName = (String)(hm.get("cvname"));
	      ItemFormMetadataBean imf = new ItemFormMetadataBean();
	      imf.setCrfVersionName(versionName);
	      //System.out.println("versionName" + imf.getCrfVersionName());
	      imf.setCrfVersionId(versionId.intValue());
	      eb.setItemMeta(imf);
	      al.add(eb);
	    }
	    return al;
		
		
	}
	
	
	public EntityBean findByPK(int ID) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

	    HashMap variables = new HashMap();
	    variables.put(new Integer(1), new Integer(ID));

	    String sql = digester.getQuery("findByPK");
	    ArrayList alist = this.select(sql, variables);
	    Iterator it = alist.iterator();

	    if (it.hasNext()) {
	      eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
	    }
		return eb;
	}
	
	public EntityBean findByName(String name) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

	    HashMap variables = new HashMap();
	    variables.put(new Integer(1), name);

	    String sql = digester.getQuery("findByName");
	    ArrayList alist = this.select(sql, variables);
	    Iterator it = alist.iterator();

	    if (it.hasNext()) {
	      eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
	    }
		return eb;
	}
	
	public EntityBean findByNameAndCRFId(String name,int crfId) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

	    HashMap variables = new HashMap();
	    variables.put(new Integer(1), name);
	    variables.put(new Integer(2), new Integer(crfId));

	    String sql = digester.getQuery("findByNameAndCRFId");
	    ArrayList alist = this.select(sql, variables);
	    Iterator it = alist.iterator();

	    if (it.hasNext()) {
	      eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
	    }
		return eb;
	}
	
	public Collection findAllByPermission(Object objCurrentUser,
			int intActionType, 
			String strOrderByColumn,
			boolean blnAscendingSort,
			String strSearchPhrase) {
		ArrayList al = new ArrayList();
		
		return al;
	}
	
	public Collection findAllByPermission(Object objCurrentUser,
			int intActionType) {
		ArrayList al = new ArrayList();
		
		return al;
	}
	
	/**
	 * Finds the children of an item in a given CRF Version, sorted by columnNumber in ascending order.
	 * @param parentId The id of the children's parent.
	 * @param crfVersionId The id of the event CRF in which the children belong to this parent.
	 * @return An array of ItemBeans, where each ItemBean represents a child of the parent and the array is sorted by columnNumber in ascending order.
	 */
	public ArrayList findAllByParentIdAndCRFVersionId(int parentId, int crfVersionId) {
		HashMap variables = new HashMap();
		variables.put(new Integer(1), new Integer(parentId));
		variables.put(new Integer(2), new Integer(crfVersionId));
		
		return this.executeFindAllQuery("findAllByParentIdAndCRFVersionId", variables);		
	}

}
