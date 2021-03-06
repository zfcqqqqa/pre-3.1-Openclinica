/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.admin.DeletedEventCRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

/**
 * @author Krikor Krumlian 2/10/2007
 * @author jsampson
 * 
 */
public class AuditDAO extends EntityDAO {
    // private DAODigester digester;
    // YW 12-06-2007 <<!!! Be careful when there is item with data-type as
    // "Date".
    // You have to make sure that string pattern conversion has been done once
    // you fetched items from database.
    // The correct patterns are:
    // in database, it should be oc_date_format_string
    // in application, it should be local date_format_string
    // If your method makes use of "getEntityFromHashMap", conversion has been
    // handled.
    // And as at this point, "getEntityFromHashMap" is used for fetched data
    // from database,
    // conversion is from oc_date_format pattern to local date_format pattern.
    // YW >>

    public AuditDAO(DataSource ds) {
        super(ds);
    }

    public AuditDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_AUDIT;
    }

    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT); // audit_id
        this.setTypeExpected(2, TypeNames.TIMESTAMP); // audit_date
        this.setTypeExpected(3, TypeNames.STRING); // audit_table
        this.setTypeExpected(4, TypeNames.INT); // user_id
        this.setTypeExpected(5, TypeNames.INT); // entity_id
        this.setTypeExpected(6, TypeNames.STRING); // entity_name
        this.setTypeExpected(7, TypeNames.STRING); // reason_for_change
        this.setTypeExpected(8, TypeNames.INT); // audit_event_type_id
        this.setTypeExpected(9, TypeNames.STRING); // old_value
        this.setTypeExpected(10, TypeNames.STRING); // new_value
        this.setTypeExpected(11, TypeNames.INT); // event_crf_id
        this.setTypeExpected(12, TypeNames.STRING); // USER NAME
        this.setTypeExpected(13, TypeNames.STRING); // AUDIT EVENT NAME

    }

    public void setTypesExpectedWithItemDataType() {
        this.setTypesExpected();
        this.setTypeExpected(14, TypeNames.INT); // item_data_type_id
    }

    /**
     * <p>
     * getEntityFromHashMap, the method that gets the object from the database
     * query.
     */
    public Object getEntityFromHashMap(HashMap hm) {
        AuditBean eb = new AuditBean();
        // AUDIT_ID AUDIT_DATE AUDIT_TABLE USER_ID ENTITY_ID
        // REASON_FOR_CHANGE
        eb.setId(((Integer) hm.get("audit_id")).intValue());
        eb.setAuditDate((java.util.Date) hm.get("audit_date"));
        eb.setAuditTable((String) hm.get("audit_table"));
        eb.setUserId(((Integer) hm.get("user_id")).intValue());
        eb.setEntityId(((Integer) hm.get("entity_id")).intValue());
        eb.setEntityName((String) hm.get("entity_name"));
        eb.setReasonForChange((String) hm.get("reason_for_change"));
        // YW 12-07-2007 <<
        if (eb.getAuditTable().equalsIgnoreCase("item_data")) {
            eb.setOldValue(Utils.convertedItemDateValue((String) hm.get("old_value"), oc_df_string, local_df_string));
            eb.setNewValue(Utils.convertedItemDateValue((String) hm.get("new_value"), oc_df_string, local_df_string));
        } else {
            eb.setOldValue((String) hm.get("old_value"));
            eb.setNewValue((String) hm.get("new_value"));
        }
        // YW >>
        eb.setEventCRFId(((Integer) hm.get("event_crf_id")).intValue());
        eb.setAuditEventTypeId(((Integer) hm.get("audit_log_event_type_id")).intValue());
        eb.setUserName((String) hm.get("user_name"));
        eb.setAuditEventTypeName((String) hm.get("name"));

        return eb;
    }

    public Object getEntityFromHashMapWithItemDataType(HashMap hm) {
        AuditBean eb = (AuditBean) this.getEntityFromHashMap(hm);
        eb.setItemDataTypeId((Integer) hm.get("item_data_type_id"));
        return eb;
    }

    /*
     * Find By Primary Key
     * 
     * @see org.akaza.openclinica.dao.core.DAOInterface#findByPK(int)
     */
    public EntityBean findByPK(int id) {
        AuditBean eb = new AuditBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(id));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return eb;
    }

    /*
     * Find All Audit Beans
     * 
     * @see org.akaza.openclinica.dao.core.DAOInterface#findAll()
     */
    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    /*
     * Find audit log events for a study subject
     * 
     */
    public Collection findStudySubjectAuditEvents(int studySubjectId) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studySubjectId));

        String sql = digester.getQuery("findStudySubjectAuditEvents");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    /*
     * Find audit log events type for a subject
     * 
     */
    public Collection findSubjectAuditEvents(int subjectId) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(subjectId));

        String sql = digester.getQuery("findSubjectAuditEvents");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    /*
     * Find audit log events type for an event CRF
     * 
     */
    public Collection findEventCRFAuditEvents(int eventCRFId) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFId));

        String sql = digester.getQuery("findEventCRFAuditEvents");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    public Collection findEventCRFAuditEventsWithItemDataType(int eventCRFId) {
        this.setTypesExpectedWithItemDataType();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(eventCRFId));

        String sql = digester.getQuery("findEventCRFAuditEventsWithItemDataType");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMapWithItemDataType((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    /*
     * Find audit log events type for an Study Event
     * 
     */
    public Collection findStudyEventAuditEvents(int studyEventId) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyEventId));

        String sql = digester.getQuery("findStudyEventAuditEvents");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    /*
     * Find deleted Event CRFs from audit log
     * 
     */
    public List findDeletedEventCRFsFromAuditEvent(int studyEventId) {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT); // study_event_id
        //this.setTypeExpected(2, TypeNames.INT); // study_event_id
        this.setTypeExpected(2, TypeNames.STRING); // crf name
        this.setTypeExpected(3, TypeNames.STRING); // crf version
        this.setTypeExpected(4, TypeNames.STRING); // user name
        this.setTypeExpected(5, TypeNames.DATE); // delete date

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(13)); // audit_log_event_type_id
        // 13 means deleted
        // items
        variables.put(new Integer(2), new Integer(studyEventId));

        String sql = digester.getQuery("findDeletedEventCRFsFromAuditEvent");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        logger.info("alist size [" + alist.size() + "]");
        while (it.hasNext()) {
            DeletedEventCRFBean bean = new DeletedEventCRFBean();
            HashMap map = (HashMap) it.next();
            bean.setStudyEventId(studyEventId);
            bean.setCrfName((String) map.get("crf_name"));
            bean.setCrfVersion((String) map.get("crf_version_name"));
            bean.setDeletedBy((String) map.get("user_name"));
            bean.setDeletedDate((Date) map.get("audit_date"));
            al.add(bean);
        }
        return al;

    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: This method not fully implemented
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /
    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();
        return al;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: This method not fully implemented
    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();
        return al;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: This method not fully implemented
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /
    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();
        return al;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: This method not fully implemented
    // Audit events should not be writable
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /
    public EntityBean update(EntityBean eb) {
        AuditBean sb = (AuditBean) eb;
        return sb;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: This method not fully implemented
    // Audit events should not be created in code, they are only created by
    // database triggers
    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // /
    public EntityBean create(EntityBean eb) {
        AuditBean sb = (AuditBean) eb;
        return sb;
    }

    /*
     * Find audit group assignment log events for a study subject
     * 
     */
    public Collection findStudySubjectGroupAssignmentAuditEvents(int studySubjectId) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studySubjectId));

        String sql = digester.getQuery("findStudySubjectGroupAssignmentAuditEvents");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    /*
        Find audit events for a single Item 
     */

    public ArrayList findItemAuditEvents(int entityId, String auditTable) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(entityId));
        variables.put(new Integer(2), auditTable);

        String sql = digester.getQuery("findSingleItemAuditEvents");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            //3 6 12 32
            if(eb.getAuditEventTypeId()==3 || eb.getAuditEventTypeId()==6 || eb.getAuditEventTypeId()==12 || eb.getAuditEventTypeId()==32){
                eb.setOldValue(Status.get(new Integer(eb.getOldValue())).getName());
                eb.setNewValue(Status.get(new Integer(eb.getNewValue())).getName());
            }

            al.add(eb);
        }
        return al;

    }

    public ArrayList checkItemAuditEventsExist(int itemId, String auditTable,int ecbId) {
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(itemId));
        variables.put(new Integer(2), auditTable);
        variables.put(new Integer(3), ecbId);

        String sql = digester.getQuery("checkItemAuditEventsExist");
        ArrayList alist = this.select(sql, variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            AuditBean eb = (AuditBean) this.getEntityFromHashMap((HashMap) it.next());
            if(eb.getAuditEventTypeId()==3 || eb.getAuditEventTypeId()==6 || eb.getAuditEventTypeId()==12 || eb.getAuditEventTypeId()==32){
                eb.setOldValue(Status.get(new Integer(eb.getOldValue())).getName());
                eb.setNewValue(Status.get(new Integer(eb.getNewValue())).getName());
            }
            al.add(eb);
        }
        return al;
    }
}
