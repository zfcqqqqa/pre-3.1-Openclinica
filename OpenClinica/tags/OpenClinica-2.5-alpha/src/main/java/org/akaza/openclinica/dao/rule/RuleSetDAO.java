/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.dao.rule;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.rule.RuleSetBean;
import org.akaza.openclinica.bean.rule.expression.Context;
import org.akaza.openclinica.bean.rule.expression.ExpressionBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

/**
 * <p>
 * Manage RuleSets
 * 
 * 
 * @author Krikor Krumlian
 * 
 */
public class RuleSetDAO extends AuditableEntityDAO {

    private EventCRFDAO eventCrfDao;
    private StudyEventDefinitionDAO studyEventDefinitionDAO;
    private RuleDAO ruleDao;
    private ExpressionDAO expressionDao;

    private void setQueryNames() {
        this.findByPKAndStudyName = "findByPKAndStudy";
        this.getCurrentPKName = "getCurrentPK";
    }

    public RuleSetDAO(DataSource ds) {
        super(ds);
        setQueryNames();
    }

    private StudyEventDefinitionDAO getStudyEventDefinitionDao() {
        return this.studyEventDefinitionDAO != null ? this.studyEventDefinitionDAO : new StudyEventDefinitionDAO(ds);
    }

    private EventCRFDAO getEventCrfDao() {
        return this.eventCrfDao != null ? this.eventCrfDao : new EventCRFDAO(ds);
    }

    private RuleDAO getRuleDao() {
        return this.ruleDao != null ? this.ruleDao : new RuleDAO(ds);
    }

    private ExpressionDAO getExpressionDao() {
        return this.expressionDao != null ? this.expressionDao : new ExpressionDAO(ds);
    }

    public RuleSetDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
        setQueryNames();
    }

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_RULESET;
    }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        this.setTypeExpected(1, TypeNames.INT); // ruleset_id
        this.setTypeExpected(2, TypeNames.INT);// expression_id
        this.setTypeExpected(3, TypeNames.INT);// owner_id
        this.setTypeExpected(4, TypeNames.DATE); // date_created
        this.setTypeExpected(5, TypeNames.DATE);// date_updated
        this.setTypeExpected(6, TypeNames.INT);// updater_id
        this.setTypeExpected(7, TypeNames.INT);// status_id

    }

    public EntityBean update(EntityBean eb) {
        RuleSetBean ruleSetBean = (RuleSetBean) eb;

        ruleSetBean.setActive(false);

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        HashMap nullVars = new HashMap();

        this.execute(digester.getQuery("update"), variables, nullVars);

        if (isQuerySuccessful()) {
            ruleSetBean.setActive(true);
        }

        return ruleSetBean;
    }

    /*
     * I am going to attempt to use this create method as we use the
     * saveOrUpdate method in Hibernate.
     */
    public EntityBean create(EntityBean eb) {
        RuleSetBean ruleSetBean = (RuleSetBean) eb;
        if (eb.getId() == 0) {
            HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
            HashMap<Integer, Object> nullVars = new HashMap<Integer, Object>();
            variables.put(new Integer(1), getExpressionDao().create(ruleSetBean.getTarget()).getId());
            variables.put(new Integer(2), new Integer(ruleSetBean.getOwnerId()));
            variables.put(new Integer(3), new Integer(Status.AVAILABLE.getId()));

            execute(digester.getQuery("create"), variables, nullVars);

            if (isQuerySuccessful()) {
                ruleSetBean.setId(getCurrentPK());
            }
        }
        return ruleSetBean;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        RuleSetBean ruleSetBean = new RuleSetBean();
        this.setEntityAuditInformation(ruleSetBean, hm);

        ruleSetBean.setId(((Integer) hm.get("rule_set_id")).intValue());
        int expressionId = ((Integer) hm.get("rule_expression_id")).intValue();
        ruleSetBean.setTarget((ExpressionBean) getExpressionDao().findByPK(expressionId));

        return ruleSetBean;
    }

    public RuleSetBean findByExpression(RuleSetBean ruleSetBean) {
        RuleSetBean ruleSetBeanInDb = new RuleSetBean();
        Context c = ruleSetBean.getTarget().getContext() == null ? Context.OC_RULES_V1 : ruleSetBean.getTarget().getContext();
        this.setTypesExpected();
        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), c.getCode());
        variables.put(new Integer(2), ruleSetBean.getTarget().getValue());

        String sql = digester.getQuery("findByExpression");
        ArrayList<?> alist = this.select(sql, variables);
        Iterator<?> it = alist.iterator();

        if (it.hasNext()) {
            ruleSetBeanInDb = (RuleSetBean) this.getEntityFromHashMap((HashMap<?, ?>) it.next());
        }
        if (alist.isEmpty()) {
            ruleSetBeanInDb = null;
        }
        return ruleSetBeanInDb;
    }

    public Collection findAll() {
        this.setTypesExpected();
        ArrayList alist = this.select(digester.getQuery("findAll"));
        ArrayList<RuleSetBean> ruleSetBeans = new ArrayList<RuleSetBean>();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            RuleSetBean ruleSet = (RuleSetBean) this.getEntityFromHashMap((HashMap) it.next());
            ruleSetBeans.add(ruleSet);
        }
        return ruleSetBeans;
    }

    public EntityBean findByPK(int ID) {
        RuleSetBean ruleSetBean = null;
        this.setTypesExpected();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            ruleSetBean = (RuleSetBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return ruleSetBean;
    }

    public RuleSetBean findByStudyEventDefinition(StudyEventDefinitionBean studyEventDefinition) {
        RuleSetBean ruleSetBean = null;
        this.setTypesExpected();

        HashMap<Integer, Object> variables = new HashMap<Integer, Object>();
        Integer studyEventDefinitionId = Integer.valueOf(studyEventDefinition.getId());
        variables.put(new Integer(1), studyEventDefinitionId);

        String sql = digester.getQuery("findByStudyEventDefinition");
        ArrayList alist = this.select(sql, variables);
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            ruleSetBean = (RuleSetBean) this.getEntityFromHashMap((HashMap) it.next());
        }
        return ruleSetBean;
    }

    /*
     * Why should we even have these in here if they are not needed? TODO:
     * refactor super class to remove dependency.
     */
    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    /*
     * Why should we even have these in here if they are not needed? TODO:
     * refactor super class to remove dependency.
     */
    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    /*
     * Why should we even have these in here if they are not needed? TODO:
     * refactor super class to remove dependency.
     */
    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

}