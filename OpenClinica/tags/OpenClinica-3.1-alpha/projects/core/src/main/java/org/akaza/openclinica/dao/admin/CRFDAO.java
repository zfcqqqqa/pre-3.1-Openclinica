/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.admin;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

/**
 * the data access object for instruments in the database.
 * 
 * @author thickerson
 * 
 */
public class CRFDAO extends AuditableEntityDAO {
    // private DataSource ds;
    // private DAODigester digester;

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_CRF;
    }

    public CRFDAO(DataSource ds) {
        super(ds);
    }

    public CRFDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
    }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.INT);
        // this.setTypeExpected(3,TypeNames.STRING);//label
        this.setTypeExpected(3, TypeNames.STRING);// name
        this.setTypeExpected(4, TypeNames.STRING);// description
        this.setTypeExpected(5, TypeNames.INT);// owner id
        this.setTypeExpected(6, TypeNames.DATE);// created
        this.setTypeExpected(7, TypeNames.DATE);// updated
        this.setTypeExpected(8, TypeNames.INT);// update id
        this.setTypeExpected(9, TypeNames.STRING);// oc_oid
        this.setTypeExpected(10, TypeNames.INT);// study_id
    }

    public EntityBean update(EntityBean eb) {
        CRFBean cb = (CRFBean) eb;
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(cb.getStatus().getId()));
        // variables.put(new Integer(2), cb.getLabel());
        variables.put(new Integer(2), cb.getName());
        variables.put(new Integer(3), cb.getDescription());
        variables.put(new Integer(4), new Integer(cb.getUpdater().getId()));
        variables.put(new Integer(5), new Integer(cb.getId()));
        this.execute(digester.getQuery("update"), variables);
        return eb;
    }

    public EntityBean create(EntityBean eb) {
        CRFBean cb = (CRFBean) eb;
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(cb.getStatus().getId()));
        // variables.put(new Integer(2), cb.getLabel());
        variables.put(new Integer(2), cb.getName());
        variables.put(new Integer(3), cb.getDescription());
        variables.put(new Integer(4), new Integer(cb.getOwner().getId()));
        variables.put(new Integer(5), getValidOid(cb, cb.getName()));
        // am i the only one who runs their daos' unit tests after I change
        // things, tbh?
        this.execute(digester.getQuery("create"), variables);
        if (isQuerySuccessful()) {
            cb.setActive(true);
        }
        return cb;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        CRFBean eb = new CRFBean();
        this.setEntityAuditInformation(eb, hm);
        eb.setId(((Integer) hm.get("crf_id")).intValue());
        eb.setName((String) hm.get("name"));
        eb.setDescription((String) hm.get("description"));
        eb.setOid((String) hm.get("oc_oid"));
        eb.setStudyId(((Integer) hm.get("source_study_id")).intValue());
        return eb;
    }

    public Collection findAll() {

        return findAllByLimit(false);
    }

    public Collection findAllByStudy(int studyId) {
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        ArrayList alist = this.select(digester.getQuery("findAllByStudy"), variables);
        ArrayList al = new ArrayList();

        Iterator it = alist.iterator();
        while (it.hasNext()) {
            CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;

    }

    public Collection findAllByLimit(boolean hasLimit) {
        this.setTypesExpected();
        ArrayList alist = null;
        if (hasLimit) {
            alist = this.select(digester.getQuery("findAllByLimit"));
        } else {
            alist = this.select(digester.getQuery("findAll"));
        }
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    public Collection findAllByStatus(Status status) {
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(status.getId()));
        ArrayList alist = this.select(digester.getQuery("findAllByStatus"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    public Collection findAllActiveByDefinition(StudyEventDefinitionBean definition) {
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(definition.getId()));
        ArrayList alist = this.select(digester.getQuery("findAllActiveByDefinition"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    public Collection findAllActiveByDefinitions(int studyId) {
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));
        ArrayList alist = this.select(digester.getQuery("findAllActiveByDefinitions"), variables);

        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            CRFBean eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
            al.add(eb);
        }
        return al;
    }

    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public EntityBean findByPK(int ID) {
        CRFBean eb = new CRFBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));

        // String sql = digester.getQuery("findByPK");
        // logger.warn("found findbypk query: "+sql);
        ArrayList alist = this.select(digester.getQuery("findByPK"), variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return eb;
    }

    public CRFBean findByItemOid(String itemOid) {
        CRFBean eb = new CRFBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), itemOid);

        ArrayList alist = this.select(digester.getQuery("findByItemOid"), variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return eb;
    }

    public EntityBean findByName(String name) {
        CRFBean eb = new CRFBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), name);

        String sql = digester.getQuery("findByName");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return eb;
    }

    public EntityBean findAnotherByName(String name, int crfId) {
        CRFBean eb = new CRFBean();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), name);
        variables.put(new Integer(2), new Integer(crfId));

        String sql = digester.getQuery("findAnotherByName");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return eb;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

    public CRFBean findByVersionId(int crfVersionId) {
        CRFBean answer = new CRFBean();

        this.unsetTypeExpected();
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(crfVersionId));

        String sql = digester.getQuery("findByVersionId");
        ArrayList rows = select(sql, variables);

        if (rows.size() > 0) {
            HashMap row = (HashMap) rows.get(0);
            answer = (CRFBean) getEntityFromHashMap(row);
        }

        return answer;
    }

    private String getOid(CRFBean crfBean, String crfName) {

        String oid;
        try {
            oid = crfBean.getOid() != null ? crfBean.getOid() : crfBean.getOidGenerator().generateOid(crfName);
            return oid;
        } catch (Exception e) {
            throw new RuntimeException("CANNOT GENERATE OID");
        }
    }

    public String getValidOid(CRFBean crfBean, String crfName) {

        String oid = getOid(crfBean, crfName);
        logger.info(oid);
        String oidPreRandomization = oid;
        while (findAllByOid(oid).size() > 0) {
            oid = crfBean.getOidGenerator().randomizeOid(oidPreRandomization);
        }
        return oid;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<CRFBean> findAllByOid(String oid) {
        HashMap<Integer, String> variables = new HashMap<Integer, String>();
        variables.put(new Integer(1), oid);

        return executeFindAllQuery("findByOID", variables);
    }

    public CRFBean findByOid(String oid) {
        CRFBean crf = new CRFBean();
        this.unsetTypeExpected();
        setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), oid);
        String sql = digester.getQuery("findByOID");

        ArrayList rows = this.select(sql, variables);
        Iterator it = rows.iterator();

        if (it.hasNext()) {
            crf = (CRFBean) this.getEntityFromHashMap((HashMap) it.next());
            return crf;
        } else {
            return null;
        }
    }

}
