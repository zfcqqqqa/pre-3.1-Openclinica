/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.domain.user.AuthoritiesBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Servlet for creating a user account.
 * 
 * @author ssachs
 */
public class CreateUserAccountServlet extends SecureController {

    // < ResourceBundle restext;
    Locale locale;

    public static final String INPUT_USERNAME = "userName";
    public static final String INPUT_FIRST_NAME = "firstName";
    public static final String INPUT_LAST_NAME = "lastName";
    public static final String INPUT_EMAIL = "email";
    public static final String INPUT_INSTITUTION = "institutionalAffiliation";
    public static final String INPUT_STUDY = "activeStudy";
    public static final String INPUT_ROLE = "role";
    public static final String INPUT_TYPE = "type";
    public static final String INPUT_DISPLAY_PWD = "displayPwd";

    /*
     * (non-Javadoc)
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // < restext =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.notes",locale);

        if (!ub.isSysAdmin()) {
            throw new InsufficientPermissionException(Page.MENU, resexception.getString("you_may_not_perform_administrative_functions"), "1");
        }

        return;
    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);

        StudyDAO sdao = new StudyDAO(sm.getDataSource());
        // YW 11-28-2007 << list sites under their studies
        ArrayList<StudyBean> all = (ArrayList<StudyBean>) sdao.findAll();
        ArrayList<StudyBean> finalList = new ArrayList<StudyBean>();
        for (StudyBean sb : all) {
            if (!(sb.getParentStudyId() > 0)) {
                finalList.add(sb);
                finalList.addAll(sdao.findAllByParent(sb.getId()));
            }
        }
        addEntityList("studies", finalList, respage.getString("a_user_cannot_be_created_no_study_as_active"), Page.ADMIN_SYSTEM);
        // YW >>
        Map roleMap = new LinkedHashMap();
        for (Iterator it = getRoles().iterator(); it.hasNext();) {
            Role role = (Role) it.next();
            roleMap.put(role.getId(), role.getDescription());
        }

        //addEntityList("roles", getRoles(), respage.getString("a_user_cannot_be_created_no_roles_as_role"), Page.ADMIN_SYSTEM);
        request.setAttribute("roles", roleMap);

        ArrayList types = UserType.toArrayList();
        types.remove(UserType.INVALID);
        if (!ub.isTechAdmin()) {
            types.remove(UserType.TECHADMIN);
        }
        addEntityList("types", types, respage.getString("a_user_cannot_be_created_no_user_types_for"), Page.ADMIN_SYSTEM);

        Boolean changeRoles = request.getParameter("changeRoles") == null ? false : Boolean.parseBoolean(request.getParameter("changeRoles"));
        int activeStudy = fp.getInt(INPUT_STUDY);
        if (changeRoles) {
            StudyBean study = (StudyBean) sdao.findByPK(activeStudy);
            roleMap = new LinkedHashMap();
            ResourceBundle resterm = org.akaza.openclinica.i18n.util.ResourceBundleProvider.getTermsBundle();

            if (study.getParentStudyId() > 0) {
                for (Iterator it = getRoles().iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                    //                        case 2: roleMap.put(role.getId(), resterm.getString("site_Study_Coordinator").trim());
                    //                            break;
                    //                        case 3: roleMap.put(role.getId(), resterm.getString("site_Study_Director").trim());
                    //                            break;
                    case 4:
                        roleMap.put(role.getId(), resterm.getString("site_investigator").trim());
                        break;
                    case 5:
                        roleMap.put(role.getId(), resterm.getString("site_Data_Entry_Person").trim());
                        break;
                    case 6:
                        roleMap.put(role.getId(), resterm.getString("site_monitor").trim());
                        break;
                    default:
                        logger.info("No role matched when setting role description");
                    }
                }
            } else {
                for (Iterator it = getRoles().iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                    case 2:
                        roleMap.put(role.getId(), resterm.getString("Study_Coordinator").trim());
                        break;
                    case 3:
                        roleMap.put(role.getId(), resterm.getString("Study_Director").trim());
                        break;
                    case 4:
                        roleMap.put(role.getId(), resterm.getString("Investigator").trim());
                        break;
                    case 5:
                        roleMap.put(role.getId(), resterm.getString("Data_Entry_Person").trim());
                        break;
                    case 6:
                        roleMap.put(role.getId(), resterm.getString("Monitor").trim());
                        break;
                    default:
                        logger.info("No role matched when setting role description");
                    }
                }
            }
            request.setAttribute("roles", roleMap);
        }
        request.setAttribute("activeStudy", activeStudy);
        if (!fp.isSubmitted() || changeRoles) {
            String textFields[] = { INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL, INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
            fp.setCurrentStringValuesAsPreset(textFields);

            String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE, INPUT_TYPE };
            fp.setCurrentIntValuesAsPreset(ddlbFields);

            HashMap presetValues = fp.getPresetValues();
            setPresetValues(presetValues);

            forwardPage(Page.CREATE_ACCOUNT);
        } else {
            UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
            Validator v = new Validator(request);

            // username must not be blank,
            // must be in the format specified by Validator.USERNAME,
            // and must be unique
            v.addValidation(INPUT_USERNAME, Validator.NO_BLANKS);
            v.addValidation(INPUT_USERNAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
            v.addValidation(INPUT_USERNAME, Validator.IS_A_USERNAME);

            v.addValidation(INPUT_USERNAME, Validator.USERNAME_UNIQUE, udao);

            v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
            v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
            v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
            v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);

            v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
            v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 120);
            v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);

            v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
            v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

            v.addValidation(INPUT_STUDY, Validator.ENTITY_EXISTS, sdao);
            v.addValidation(INPUT_ROLE, Validator.IS_VALID_TERM, TermType.ROLE);

            HashMap errors = v.validate();

            if (errors.isEmpty()) {
                UserAccountBean createdUserAccountBean = new UserAccountBean();
                createdUserAccountBean.setName(fp.getString(INPUT_USERNAME));
                createdUserAccountBean.setFirstName(fp.getString(INPUT_FIRST_NAME));
                createdUserAccountBean.setLastName(fp.getString(INPUT_LAST_NAME));
                createdUserAccountBean.setEmail(fp.getString(INPUT_EMAIL));
                createdUserAccountBean.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));

                SecurityManager secm = ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean("securityManager"));
                String password = secm.genPassword();
                String passwordHash = secm.encrytPassword(password, getUserDetails());

                createdUserAccountBean.setPasswd(passwordHash);

                createdUserAccountBean.setPasswdTimestamp(null);
                createdUserAccountBean.setLastVisitDate(null);

                createdUserAccountBean.setStatus(Status.AVAILABLE);
                createdUserAccountBean.setPasswdChallengeQuestion("");
                createdUserAccountBean.setPasswdChallengeAnswer("");
                createdUserAccountBean.setPhone("");
                createdUserAccountBean.setOwner(ub);

                int studyId = fp.getInt(INPUT_STUDY);
                Role r = Role.get(fp.getInt(INPUT_ROLE));
                createdUserAccountBean = addActiveStudyRole(createdUserAccountBean, studyId, r);
                UserType type = UserType.get(fp.getInt("type"));
                logger.warn("*** found type: " + fp.getInt("type"));
                logger.warn("*** setting type: " + type.getDescription());
                createdUserAccountBean.addUserType(type);
                createdUserAccountBean = (UserAccountBean) udao.create(createdUserAccountBean);
                AuthoritiesDao authoritiesDao = (AuthoritiesDao) SpringServletAccess.getApplicationContext(context).getBean("authoritiesDao");
                authoritiesDao.saveOrUpdate(new AuthoritiesBean(createdUserAccountBean.getName()));
                String displayPwd = fp.getString(INPUT_DISPLAY_PWD);

                if (createdUserAccountBean.isActive()) {
                    addPageMessage(respage.getString("the_user_account") + "\"" + createdUserAccountBean.getName() + "\""
                        + respage.getString("was_created_succesfully"));
                    if ("no".equalsIgnoreCase(displayPwd)) {
                        try {
                            sendNewAccountEmail(createdUserAccountBean, password);
                        } catch (Exception e) {
                            addPageMessage(respage.getString("there_was_an_error_sending_account_creating_mail"));
                        }
                    } else {
                        addPageMessage(respage.getString("user_password") + ":<br/>" + password + "<br/> "
                            + respage.getString("please_write_down_the_password_and_provide"));
                    }
                } else {
                    addPageMessage(respage.getString("the_user_account") + "\"" + createdUserAccountBean.getName() + "\""
                        + respage.getString("could_not_created_due_database_error"));
                }
                if (createdUserAccountBean.isActive()) {
                    request.setAttribute(ViewUserAccountServlet.ARG_USER_ID, new Integer(createdUserAccountBean.getId()).toString());
                    forwardPage(Page.VIEW_USER_ACCOUNT_SERVLET);
                } else {
                    forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET);
                }
            } else {
                String textFields[] = { INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_EMAIL, INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
                fp.setCurrentStringValuesAsPreset(textFields);

                String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE, INPUT_TYPE };
                fp.setCurrentIntValuesAsPreset(ddlbFields);

                HashMap presetValues = fp.getPresetValues();
                setPresetValues(presetValues);

                setInputMessages(errors);
                addPageMessage(respage.getString("there_were_some_errors_submission") + respage.getString("see_below_for_details"));

                forwardPage(Page.CREATE_ACCOUNT);
            }
        }
    }

    private ArrayList getRoles() {

        ArrayList roles = Role.toArrayList();
        roles.remove(Role.ADMIN);

        return roles;
    }

    private UserAccountBean addActiveStudyRole(UserAccountBean createdUserAccountBean, int studyId, Role r) {
        createdUserAccountBean.setActiveStudyId(studyId);

        StudyUserRoleBean activeStudyRole = new StudyUserRoleBean();

        activeStudyRole.setStudyId(studyId);
        activeStudyRole.setRoleName(r.getName());
        activeStudyRole.setStatus(Status.AVAILABLE);
        activeStudyRole.setOwner(ub);

        createdUserAccountBean.addRole(activeStudyRole);

        return createdUserAccountBean;
    }

    private void sendNewAccountEmail(UserAccountBean createdUserAccountBean, String password) throws Exception {
        logger.info("Sending account creation notification to " + createdUserAccountBean.getName());

        String body = resword.getString("dear") + " " + createdUserAccountBean.getFirstName() + " " + createdUserAccountBean.getLastName() + ",\n";
        body += restext.getString("a_new_user_account_has_been_created_for_you") + "\n\n";
        body += resword.getString("user_name") + ": " + createdUserAccountBean.getName() + "\n";
        body += resword.getString("password") + ": " + password + "\n\n";
        body += restext.getString("please_test_your_login_information_and_let") + "\n";
        body += SQLInitServlet.getField("sysURL");
        body += " . ";
        //body += restext.getString("openclinica_system_administrator");

        sendEmail(createdUserAccountBean.getEmail().trim(), restext.getString("your_new_openclinica_account"), body, false);
    }

    @Override
    protected String getAdminServlet() {
        return SecureController.ADMIN_SERVLET_CODE;
    }
}
