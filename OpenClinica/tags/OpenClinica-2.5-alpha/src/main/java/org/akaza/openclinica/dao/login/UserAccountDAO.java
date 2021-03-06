/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.dao.login;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.Privilege;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.sql.DataSource;

/**
 * <P>
 * UserAccountDAO, the data access object for the User_Account table in the
 * OpenClinica 2.0 database.
 *
 * @author thickerson
 *
 * TODO
 * <P>
 * add functions for admin use cases such as assign user to study, remove user
 * from study, etc.
 * <P>
 * add ability to get role and priv objects from database when U select
 * <P>
 * expand on query to get all that from a select star?
 */
public class UserAccountDAO extends AuditableEntityDAO {
    // private DataSource ds;
    // private DAODigester digester;

    @Override
    protected void setDigesterName() {
        digesterName = SQLFactory.getInstance().DAO_USERACCOUNT;
    }

    protected void setQueryNames() {
        getCurrentPKName = "getCurrentPK";
        getNextPKName = "getNextPK";
    }

    public UserAccountDAO(DataSource ds) {
        super(ds);
        setQueryNames();
    }

    public UserAccountDAO(DataSource ds, DAODigester digester) {
        super(ds);
        this.digester = digester;
        setQueryNames();
    }

    @Override
    public void setTypesExpected() {
        this.unsetTypeExpected();
        // assuming select star query on user_account
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.STRING);
        this.setTypeExpected(3, TypeNames.STRING);
        this.setTypeExpected(4, TypeNames.STRING);
        this.setTypeExpected(5, TypeNames.STRING);
        this.setTypeExpected(6, TypeNames.STRING);
        this.setTypeExpected(7, TypeNames.INT);
        this.setTypeExpected(8, TypeNames.STRING);
        this.setTypeExpected(9, TypeNames.INT);
        this.setTypeExpected(10, TypeNames.INT);
        this.setTypeExpected(11, TypeNames.DATE);// created
        this.setTypeExpected(12, TypeNames.DATE);// updated
        this.setTypeExpected(13, TypeNames.TIMESTAMP);// lastvisit, changed
        // from date
        this.setTypeExpected(14, TypeNames.DATE);// passwd timestamp
        this.setTypeExpected(15, TypeNames.STRING);
        this.setTypeExpected(16, TypeNames.STRING);
        this.setTypeExpected(17, TypeNames.STRING);
        this.setTypeExpected(18, TypeNames.INT);
        this.setTypeExpected(19, TypeNames.INT);
    }

    public void setPrivilegeTypesExpected() {
        this.unsetTypeExpected();
        // assuming we are selecting privs from a join on privilege and
        // role_priv_map tables, tbh
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.STRING);
        this.setTypeExpected(3, TypeNames.STRING);
        this.setTypeExpected(4, TypeNames.STRING);

    }

    public void setRoleTypesExpected() {
        this.unsetTypeExpected();
        // assuming select star from study_user_role
        this.setTypeExpected(1, TypeNames.STRING);
        this.setTypeExpected(2, TypeNames.INT);
        this.setTypeExpected(3, TypeNames.INT);
        this.setTypeExpected(4, TypeNames.INT);
        this.setTypeExpected(5, TypeNames.DATE);
        this.setTypeExpected(6, TypeNames.DATE);
        this.setTypeExpected(7, TypeNames.INT);
        this.setTypeExpected(8, TypeNames.STRING);
    }

    public EntityBean update(EntityBean eb) {
        UserAccountBean uab = (UserAccountBean) eb;
        HashMap variables = new HashMap();
        HashMap nullVars = new HashMap();

        /*
         * update user_account set
         *
         * date_lastvisit=?, passwd_timestamp=?, passwd_challenge_question=?,
         * passwd_challenge_answer=?, phone=? where user_name=?
         */

        variables.put(new Integer(1), uab.getName());
        variables.put(new Integer(2), uab.getPasswd());
        variables.put(new Integer(3), uab.getFirstName());
        variables.put(new Integer(4), uab.getLastName());
        variables.put(new Integer(5), uab.getEmail());
        if (uab.getActiveStudyId() == 0) {
            nullVars.put(new Integer(6), new Integer(TypeNames.INT));
            variables.put(new Integer(6), null);
        } else {
            variables.put(new Integer(6), new Integer(uab.getActiveStudyId()));
        }
        variables.put(new Integer(7), uab.getInstitutionalAffiliation());
        variables.put(new Integer(8), new Integer(uab.getStatus().getId()));
        variables.put(new Integer(9), new Integer(uab.getUpdaterId()));
        if (uab.getLastVisitDate() == null) {
            nullVars.put(new Integer(10), new Integer(TypeNames.TIMESTAMP));
            variables.put(new Integer(10), null);
        } else {
            variables.put(new Integer(10), new Timestamp(uab.getLastVisitDate().getTime()));
        }
        if (uab.getPasswdTimestamp() == null) {
            nullVars.put(new Integer(11), new Integer(TypeNames.DATE));
            variables.put(new Integer(11), null);
        } else {
            variables.put(new Integer(11), uab.getPasswdTimestamp());
        }
        variables.put(new Integer(12), uab.getPasswdChallengeQuestion());
        variables.put(new Integer(13), uab.getPasswdChallengeAnswer());
        variables.put(new Integer(14), uab.getPhone());

        if (uab.isTechAdmin()) {
            variables.put(new Integer(15), new Integer(UserType.TECHADMIN.getId()));
        } else if (uab.isSysAdmin()) {
            variables.put(new Integer(15), new Integer(UserType.SYSADMIN.getId()));
        } else {
            variables.put(new Integer(15), new Integer(UserType.USER.getId()));
        }

        variables.put(new Integer(16), new Integer(uab.getId()));

        String sql = digester.getQuery("update");
        this.execute(sql, variables, nullVars);

        if (!uab.isTechAdmin()) {
            setSysAdminRole(uab, false);
        }

        if (!this.isQuerySuccessful()) {
            eb.setId(0);
            logger.warning("query failed: " + sql);
        } else {
            // for bug testing, tbh
            // logger.warning("query succeeded: "+sql+" variables:
            // "+variables.toString());
        }

        return eb;
    }

    /**
     * deleteTestOnly, used only to clean up after unit testing, tbh
     *
     * @param name
     */
    public void deleteTestOnly(String name) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), name);
        this.execute(digester.getQuery("deleteTestOnly"), variables);
    }

    public void delete(UserAccountBean u) {
        HashMap variables = new HashMap();

        variables = new HashMap();
        variables.put(new Integer(1), u.getName());
        this.execute(digester.getQuery("deleteStudyUserRolesByUserID"), variables);

        variables.put(new Integer(1), new Integer(u.getUpdaterId()));
        variables.put(new Integer(2), new Integer(u.getId()));
        this.execute(digester.getQuery("delete"), variables);
    }

    public void restore(UserAccountBean u) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), u.getPasswd());
        variables.put(new Integer(2), new Integer(u.getUpdaterId()));
        variables.put(new Integer(3), new Integer(u.getId()));
        this.execute(digester.getQuery("restore"), variables);

        variables = new HashMap();
        variables.put(new Integer(1), u.getName());
        this.execute(digester.getQuery("restoreStudyUserRolesByUserID"), variables);
    }

    public EntityBean create(EntityBean eb) {
        UserAccountBean uab = (UserAccountBean) eb;
        HashMap variables = new HashMap();
        int id = getNextPK();
        variables.put(new Integer(1), new Integer(id));
        variables.put(new Integer(2), uab.getName());
        variables.put(new Integer(3), uab.getPasswd());
        variables.put(new Integer(4), uab.getFirstName());
        variables.put(new Integer(5), uab.getLastName());
        variables.put(new Integer(6), uab.getEmail());
        variables.put(new Integer(7), new Integer(uab.getActiveStudyId()));
        variables.put(new Integer(8), uab.getInstitutionalAffiliation());
        variables.put(new Integer(9), new Integer(uab.getStatus().getId()));
        variables.put(new Integer(10), new Integer(uab.getOwnerId()));
        variables.put(new Integer(11), uab.getPasswdChallengeQuestion());
        variables.put(new Integer(12), uab.getPasswdChallengeAnswer());
        variables.put(new Integer(13), uab.getPhone());

        if (uab.isTechAdmin()) {
            variables.put(new Integer(14), new Integer(UserType.TECHADMIN.getId()));
        } else if (uab.isSysAdmin()) {
            variables.put(new Integer(14), new Integer(UserType.SYSADMIN.getId()));
        } else {
            variables.put(new Integer(14), new Integer(UserType.USER.getId()));
        }

        boolean success = true;
        this.execute(digester.getQuery("insert"), variables);
        success = success && isQuerySuccessful();

        setSysAdminRole(uab, true);

        ArrayList userRoles = uab.getRoles();
        for (int i = 0; i < userRoles.size(); i++) {
            StudyUserRoleBean studyRole = (StudyUserRoleBean) userRoles.get(i);

            if (studyRole.equals(Role.ADMIN)) {
                continue;
            }

            createStudyUserRole(uab, studyRole);
            success = success && isQuerySuccessful();
        }

        if (success) {
            uab.setId(id);
        }

        return uab;
    }

    public StudyUserRoleBean createStudyUserRole(UserAccountBean user, StudyUserRoleBean studyRole) {
        Locale currentLocale = ResourceBundleProvider.getLocale();
        ResourceBundleProvider.updateLocale(Locale.US);
        HashMap variables = new HashMap();
        variables.put(new Integer(1), studyRole.getRoleName());
        variables.put(new Integer(2), new Integer(studyRole.getStudyId()));
        variables.put(new Integer(3), new Integer(studyRole.getStatus().getId()));
        variables.put(new Integer(4), user.getName());
        variables.put(new Integer(5), new Integer(studyRole.getOwnerId()));
        this.execute(digester.getQuery("insertStudyUserRole"), variables);
        ResourceBundleProvider.updateLocale(currentLocale);
        return studyRole;
    }

    public Object getEntityFromHashMap(HashMap hm) {
        UserAccountBean uab = (UserAccountBean) this.getEntityFromHashMap(hm, true);
        return uab;
    }

    public StudyUserRoleBean getRoleFromHashMap(HashMap hm) {
        StudyUserRoleBean surb = new StudyUserRoleBean();

        Date dateCreated = (Date) hm.get("date_created");
        Date dateUpdated = (Date) hm.get("date_updated");
        Integer statusId = (Integer) hm.get("status_id");
        Integer studyId = (Integer) hm.get("study_id");
        Integer ownerId = (Integer) hm.get("owner_id");
        Integer updateId = (Integer) hm.get("update_id");

        surb.setName((String) hm.get("user_name"));
        surb.setUserName((String) hm.get("user_name"));
        surb.setRoleName((String) hm.get("role_name"));
        surb.setCreatedDate(dateCreated);
        surb.setUpdatedDate(dateUpdated);
        surb.setStatus(Status.get(statusId.intValue()));
        surb.setStudyId(studyId.intValue());
        // surb.setOwner()
        // surb.setUpdater()
        return surb;
    }

    public Privilege getPrivilegeFromHashMap(HashMap hm) {
        Integer privId = (Integer) hm.get("priv_id");

        return Privilege.get(privId.intValue());
    }

    public Object getEntityFromHashMap(HashMap hm, boolean findOwner) {
        UserAccountBean eb = new UserAccountBean();

        // pull out objects from hashmap
        String firstName = (String) hm.get("first_name");
        String lastName = (String) hm.get("last_name");
        String userName = (String) hm.get("user_name");
        eb.setEmail((String) hm.get("email"));
        eb.setPasswd((String) hm.get("passwd"));
        Integer userId = (Integer) hm.get("user_id");
        Integer activeStudy = (Integer) hm.get("active_study");
        Integer statusId = (Integer) hm.get("status_id");
        Date dateCreated = (Date) hm.get("date_created");
        Date dateUpdated = (Date) hm.get("date_updated");
        Date dateLastVisit = (Date) hm.get("date_lastvisit");
        Date pwdTimestamp = (Date) hm.get("passwd_timestamp");
        String passwdChallengeQuestion = (String) hm.get("passwd_challenge_question");
        String passwdChallengeAnswer = (String) hm.get("passwd_challenge_answer");
        Integer userTypeId = (Integer) hm.get("user_type_id");
        Integer ownerId = (Integer) hm.get("owner_id");
        Integer updateId = (Integer) hm.get("update_id");

        // begin to set objects in the bean
        eb.setId(userId.intValue());
        eb.setActiveStudyId(activeStudy.intValue());
        eb.setInstitutionalAffiliation((String) hm.get("institutional_affiliation"));
        eb.setStatus(Status.get(statusId.intValue()));
        eb.setCreatedDate(dateCreated);
        eb.setUpdatedDate(dateUpdated);
        eb.setLastVisitDate(dateLastVisit);
        eb.setPasswdTimestamp(pwdTimestamp);
        eb.setPhone((String) hm.get("phone"));
        eb.addUserType(UserType.get(userTypeId.intValue()));
        // for testing, tbh
        if (eb.isTechAdmin()) {
            // logger.warning("&&& is TECH ADMIN &&&");
        }
        eb.setOwnerId(ownerId.intValue());
        eb.setUpdaterId(updateId.intValue());

        // below block is set up to avoid recursion, etc.
        if (findOwner) {
            UserAccountBean owner = (UserAccountBean) this.findByPK(ownerId.intValue(), false);
            eb.setOwner(owner);
            UserAccountBean updater = (UserAccountBean) this.findByPK(updateId.intValue(), false);
            eb.setUpdater(updater);
        }
        // end of if block to avoid recursion

        eb.setFirstName(firstName);
        eb.setLastName(lastName);
        eb.setName(userName);
        eb.setPasswdChallengeQuestion(passwdChallengeQuestion);
        eb.setPasswdChallengeAnswer(passwdChallengeAnswer);

        // pull out the roles and privs here, tbh
        ArrayList userRoleBeans = (ArrayList) this.findAllRolesByUserName(eb.getName());
        eb.setRoles(userRoleBeans);

        // the role-privilege mapping is now statically fixed in Role,
        // so we don't need the block below

        // Iterator it = userRoleBeans.iterator();
        // HashMap studyRoleMap = new HashMap();
        // while (it.hasNext()) {
        // StudyUserRoleBean surb = (StudyUserRoleBean)it.next();
        // studyRoleMap.put(new Integer(surb.getStudyId()),surb);
        // if (surb.getStudyId()==eb.getActiveStudyId()) {
        // ArrayList privilegeBeans =
        // (ArrayList)this.findPrivilegesByRoleName(surb.getRoleName());
        // eb.setUserPrivileges(privilegeBeans);
        // //set this user up with active privs based on active study notation
        // }
        // }
        // eb.setRoles(studyRoleMap);

        eb.setActive(true);
        return eb;
    }

    public Collection findAll() {
        return findAllByLimit(false);
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
            UserAccountBean eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
            al.add(eb);
        }
        return al;
    }

    /*
     * next on our list, how can we affect the query??? SELECT * FROM
     * USER_ACCOUNT ORDER BY ? DESC?
     *
     * @see org.akaza.openclinica.dao.core.DAOInterface#findAll(java.lang.String,
     *      boolean, java.lang.String)
     */
    public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public EntityBean findByPK(int ID) {
        this.setTypesExpected();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));

        String sql = digester.getQuery("findByPK");
        ArrayList alist = this.select(sql, variables);
        UserAccountBean eb = new UserAccountBean();
        Iterator it = alist.iterator();

        if (it.hasNext()) {
            eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
        }

        return eb;
    }

    public EntityBean findByPK(int ID, boolean findOwner) {
        this.setTypesExpected();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(ID));
        ArrayList alist = this.select(digester.getQuery("findByPK"), variables);
        UserAccountBean eb = new UserAccountBean();
        Iterator it = alist.iterator();
        if (it.hasNext()) {
            eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), findOwner);
        }
        return eb;
    }

    public EntityBean findByUserName(String name) {
        this.setTypesExpected();
        HashMap variables = new HashMap();

        variables.put(new Integer(1), name);

        ArrayList alist = this.select(digester.getQuery("findByUserName"), variables);
        UserAccountBean eb = new UserAccountBean();
        Iterator it = alist.iterator();
        if (it.hasNext()) {
            eb = (UserAccountBean) this.getEntityFromHashMap((HashMap) it.next(), true);
        }
        return eb;
    }

    /**
     * Finds all the studies with roles for a user
     *
     * @param userName
     * @param allStudies
     *            The result of calling StudyDAO.findAll();
     */
    public ArrayList findStudyByUser(String userName, ArrayList allStudies) {
        this.unsetTypeExpected();

        this.setTypeExpected(1, TypeNames.STRING);
        this.setTypeExpected(2, TypeNames.INT);
        this.setTypeExpected(3, TypeNames.STRING);
        HashMap allStudyUserRoleBeans = new HashMap();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), userName);
        ArrayList alist = this.select(digester.getQuery("findStudyByUser"), variables);
        Iterator it = alist.iterator();

        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            String roleName = (String) hm.get("role_name");
            String studyName = (String) hm.get("name");
            Integer studyId = (Integer) hm.get("study_id");
            StudyUserRoleBean sur = new StudyUserRoleBean();
            sur.setRoleName(roleName);
            sur.setStudyId(studyId.intValue());
            sur.setStudyName(studyName);
            sur.setRole(Role.getByName(roleName));
            allStudyUserRoleBeans.put(studyId, sur);
        }

        // pseudocode:
        // for each parent study P in the system
        // if the user has a role in that study, add it to the answer
        // otherwise, let parentAdded = false
        //
        // for each study, C, which is a child of P
        // if the user has a role in C,
        // if parentAdded = false
        // add a StudyUserRole with study = P, role = invalid to the answer
        // let parentAdded = true
        // add the user's role in C to the answer

        ArrayList answer = new ArrayList();

        StudyDAO sdao = new StudyDAO(ds);

        HashMap childrenByParentId = sdao.getChildrenByParentIds(allStudies);

        for (int i = 0; i < allStudies.size(); i++) {
            StudyBean parent = (StudyBean) allStudies.get(i);

            if (parent == null || parent.getParentStudyId() > 0) {
                continue;
            }

            boolean parentAdded = false;
            Integer studyId = new Integer(parent.getId());
            StudyUserRoleBean roleInStudy;

            ArrayList subTreeRoles = new ArrayList();

            if (allStudyUserRoleBeans.containsKey(studyId)) {
                roleInStudy = (StudyUserRoleBean) allStudyUserRoleBeans.get(studyId);

                subTreeRoles.add(roleInStudy);
                parentAdded = true;
            } else { // we do this so that we can compute Role.max below
                // without
                // throwing a NullPointerException
                roleInStudy = new StudyUserRoleBean();
            }

            ArrayList children = (ArrayList) childrenByParentId.get(studyId);
            if (children == null) {
                children = new ArrayList();
            }

            for (int j = 0; j < children.size(); j++) {
                StudyBean child = (StudyBean) children.get(j);
                Integer childId = new Integer(child.getId());

                if (allStudyUserRoleBeans.containsKey(childId)) {
                    if (!parentAdded) {
                        roleInStudy.setStudyId(studyId.intValue());
                        roleInStudy.setRole(Role.INVALID);
                        roleInStudy.setStudyName(parent.getName());
                        subTreeRoles.add(roleInStudy);
                        parentAdded = true;
                    }

                    StudyUserRoleBean roleInChild = (StudyUserRoleBean) allStudyUserRoleBeans.get(childId);
                    Role max = Role.max(roleInChild.getRole(), roleInStudy.getRole());
                    roleInChild.setRole(max);
                    roleInChild.setParentStudyId(studyId.intValue());
                    subTreeRoles.add(roleInChild);
                } else {
                    StudyUserRoleBean roleInChild = new StudyUserRoleBean();
                    roleInChild.setStudyId(child.getId());
                    roleInChild.setStudyName(child.getName());
                    roleInChild.setRole(roleInStudy.getRole());
                    roleInChild.setParentStudyId(studyId.intValue());
                    subTreeRoles.add(roleInChild);
                }
            }
            if (parentAdded) {
                answer.addAll(subTreeRoles);
            }
        }

        // HashMap allStudiesById = sdao.findAllHashMap();

        // pseudocode:
        // foreach parent study P
        // add the user's StudyUserRoleBean in P to the result
        // get all of the user's StudyUserRoleBean for each study that is a
        // child of
        // P
        // foreach of the user's StudyUserRoleBean for each study that is a
        // child of
        // P
        // set the studyName in that bean correctly
        // add the bean to the result

        // //find all sites for the studies
        // Iterator ita = allStudyUserRoleBeans.keySet().iterator();
        // while (ita.hasNext()){
        // //gets parent role bean
        // Integer studyId = (Integer)ita.next();
        // StudyUserRoleBean sur =
        // (StudyUserRoleBean)allStudyUserRoleBeans.get(studyId);
        //
        // StudyBean sb = (StudyBean) allStudiesById.get(new
        // Integer(sur.getStudyId()));
        // if (sb == null) { continue; }
        //
        // int parentStudyId = sb.getParentStudyId();
        // if (parentStudyId <= 0) { // sb is a parent, not a child
        // result.add(sur);
        // }
        // else { // sb is a child
        // if (!allStudyUserRoleBeans.containsKey(new Integer(parentStudyId))) {
        // // since the user doesn't have a role in sb's parent, their role in
        // sb
        // would never be added in the for loop below
        // result.add(sur);
        // }
        // }
        //
        // ArrayList children = findAllByParent(studyId);//find all sites
        //
        // for (int i = 0; i < children.size(); i++) {
        // StudyBean child = (StudyBean) children.get(i);
        //
        // Integer childId = new Integer(child.getId());
        // StudyUserRoleBean roleInChild =
        // (StudyUserRoleBean) allStudyUserRoleBeans.get(childId);
        //
        // if (roleInChild != null) { // that is, the user has a role in the
        // child
        // study
        // Role maxRole = Role.max(roleInChild.getRole(), sur.getRole());
        // roleInChild.setRole(maxRole);
        // result.add(roleInChild);
        // }
        // else { // the user does not have a role in the child study - he
        // inherits
        // the role he has in the parent study
        // roleInChild = new StudyUserRoleBean();
        // roleInChild.setStudyId(child.getId());
        // roleInChild.setStudyName(child.getName());
        // roleInChild.setRole(sur.getRole());
        // result.add(roleInChild);
        // }
        // }//for
        // }

        return answer;
    }

    private ArrayList findAllByParent(Integer parentStudyId) {
        this.setTypesExpected();
        this.setTypeExpected(1, TypeNames.INT);
        this.setTypeExpected(2, TypeNames.STRING);
        HashMap variables = new HashMap();
        variables.put(new Integer(1), parentStudyId);
        ArrayList alist = this.select(digester.getQuery("findAllByParent"), variables);
        ArrayList al = new ArrayList();
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            StudyBean sb = new StudyBean();
            sb.setName((String) hm.get("name"));
            sb.setId(((Integer) hm.get("study_id")).intValue());
            al.add(sb);
        }
        return al;
    }

    public Collection findAllRolesByUserName(String userName) {
        this.setRoleTypesExpected();
        ArrayList answer = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), userName);
        ArrayList alist = this.select(digester.getQuery("findAllRolesByUserName"), variables);
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            StudyUserRoleBean surb = this.getRoleFromHashMap((HashMap) it.next());
            answer.add(surb);
        }

        return answer;
    }

    /**
     * Finds all user and roles in a study
     *
     * @param studyId
     */
    public ArrayList findAllByStudyId(int studyId) {

        return findAllUsersByStudyIdAndLimit(studyId, false);

    }

    /**
     * Finds all user and roles in a study
     *
     * @param studyId
     */
    public ArrayList findAllUsersByStudyIdAndLimit(int studyId, boolean isLimited) {
        this.setRoleTypesExpected();
        ArrayList answer = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));
        ArrayList alist = null;
        if (isLimited) {
            alist = this.select(digester.getQuery("findAllByStudyIdAndLimit"), variables);
        } else {
            alist = this.select(digester.getQuery("findAllByStudyId"), variables);
        }
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            StudyUserRoleBean surb = this.getRoleFromHashMap((HashMap) it.next());
            answer.add(surb);
        }

        return answer;

    }

    /**
     * Finds all user and roles in a study
     *
     * @param studyId
     */
    public ArrayList findAllUsersByStudy(int studyId) {
        // SELECT ua.user_name,ua.first_name, ua.last_name, sur.role_name,
        // sur.study_id,sur.status_id,sur.date_updated,sur.update_id, s.name
        // FROM user_account ua, study_user_role sur, study s
        // WHERE ua.user_name=sur.user_name
        // AND (sur.study_id=s.study_id)
        // AND (sur.study_id=?
        // OR sur.study_id in (select s.study_id FROM study s WHERE
        // s.parent_study_id=? ))
        // order by ua.date_created asc
        this.unsetTypeExpected();

        this.setTypeExpected(1, TypeNames.STRING);
        this.setTypeExpected(2, TypeNames.STRING);
        this.setTypeExpected(3, TypeNames.STRING);
        this.setTypeExpected(4, TypeNames.STRING);
        this.setTypeExpected(5, TypeNames.INT);
        this.setTypeExpected(6, TypeNames.INT);
        this.setTypeExpected(7, TypeNames.DATE);
        this.setTypeExpected(8, TypeNames.INT);
        this.setTypeExpected(9, TypeNames.STRING);

        ArrayList answer = new ArrayList();

        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(studyId));
        variables.put(new Integer(2), new Integer(studyId));
        ArrayList alist = this.select(digester.getQuery("findAllUsersByStudy"), variables);
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            HashMap hm = (HashMap) it.next();
            StudyUserRoleBean surb = new StudyUserRoleBean();
            surb.setUserName((String) hm.get("user_name"));
            surb.setLastName((String) hm.get("last_name"));
            surb.setFirstName((String) hm.get("first_name"));
            surb.setRoleName((String) hm.get("role_name"));
            surb.setStudyName((String) hm.get("name"));
            surb.setStudyId(((Integer) hm.get("study_id")).intValue());

            Integer statusId = (Integer) hm.get("status_id");
            Date dateUpdated = (Date) hm.get("date_updated");

            surb.setUpdatedDate(dateUpdated);
            surb.setStatus(Status.get(statusId.intValue()));
            answer.add(surb);
        }

        return answer;

    }

    public Collection findPrivilegesByRole(int roleId) {
        this.setPrivilegeTypesExpected();
        ArrayList al = new ArrayList();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), new Integer(roleId));
        ArrayList alist = this.select(digester.getQuery("findPrivilegesByRole"), variables);
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            Privilege pb = this.getPrivilegeFromHashMap((HashMap) it.next());
            al.add(pb);
        }
        return al;
    }

    public Collection findPrivilegesByRoleName(String roleName) {
        this.setPrivilegeTypesExpected();
        ArrayList al = new ArrayList();
        HashMap variables = new HashMap();
        variables.put(new Integer(1), roleName);
        ArrayList alist = this.select(digester.getQuery("findPrivilegesByRoleName"), variables);
        Iterator it = alist.iterator();
        while (it.hasNext()) {
            Privilege p = this.getPrivilegeFromHashMap((HashMap) it.next());
            al.add(p);
        }
        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
        ArrayList al = new ArrayList();

        return al;
    }

    public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
        ArrayList al = new ArrayList();

        return al;
    }

    public StudyUserRoleBean updateStudyUserRole(StudyUserRoleBean s, String userName) {
        HashMap variables = new HashMap();

        variables.put(new Integer(1), s.getRoleName());
        variables.put(new Integer(2), new Integer(s.getStatus().getId()));
        variables.put(new Integer(3), new Integer(s.getUpdaterId()));
        variables.put(new Integer(4), new Integer(s.getStudyId()));
        variables.put(new Integer(5), userName);

        String sql = digester.getQuery("updateStudyUserRole");
        this.execute(sql, variables);

        return s;
    }

    public StudyUserRoleBean findRoleByUserNameAndStudyId(String userName, int studyId) {
        Collection roles = findAllRolesByUserName(userName);
        Iterator roleIt = roles.iterator();

        while (roleIt.hasNext()) {
            StudyUserRoleBean s = (StudyUserRoleBean) roleIt.next();
            if (s.getStudyId() == studyId) {
                s.setActive(true);
                return s;
            }
        }

        StudyUserRoleBean doesntExist = new StudyUserRoleBean();
        doesntExist.setActive(false);
        return doesntExist;
    }

    public void setSysAdminRole(UserAccountBean uab, boolean creating) {
        HashMap variables = new HashMap();
        variables.put(new Integer(1), uab.getName());

        if (uab.isSysAdmin() && !uab.isTechAdmin()) {
            // we remove first so that there are no duplicate roles
            this.execute(digester.getQuery("removeSysAdminRole"), variables);

            int ownerId = creating ? uab.getOwnerId() : uab.getUpdaterId();
            variables.put(new Integer(2), new Integer(ownerId));
            variables.put(new Integer(3), new Integer(ownerId));
            this.execute(digester.getQuery("addSysAdminRole"), variables);
        } else {
            this.execute(digester.getQuery("removeSysAdminRole"), variables);
        }
    }
}
