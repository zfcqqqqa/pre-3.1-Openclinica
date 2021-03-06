/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.control.core;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.InconsistentStateException;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.BreadcrumbTrail;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.view.StudyInfoPanelLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * This class enhances the Controller in several ways.
 * 
 * <ol>
 * <li> The method mayProceed, for which the class is named, is declared
 * abstract and is called before processRequest. This method indicates whether
 * the user may proceed with the action he wishes to perform (as indicated by
 * various attributes or parameters in request or session). Note, howeveer, that
 * the method has a void return, and throws InsufficientPermissionException. The
 * intention is that if the user may not proceed with his desired action, the
 * method should throw an exception. InsufficientPermissionException will accept
 * a Page object which indicates where the user should be redirected in order to
 * be informed that he has insufficient permission, and the process method
 * enforces this redirection by catching an InsufficientPermissionException
 * object.
 * 
 * <li> Four new members, session, request, response, and the UserAccountBean
 * object ub have been declared protected, and are set in the process method.
 * This allows developers to avoid passing these objects between methods, and
 * moreover it accurately encodes the fact that these objects represent the
 * state of the servlet.
 * 
 * <br/>In particular, please note that it is no longer necessary to generate a
 * bean for the session manager, the current user or the current study.
 * 
 * <li> The method processRequest has been declared abstract. This change is
 * unlikely to affect most code, since by custom processRequest is declared in
 * each subclass anyway.
 * 
 * <li> The standard try-catch block within most processRequest methods has been
 * included in the process method, which calls the processRequest method.
 * Therefore, subclasses may throw an Exception in the processRequest method
 * without having to handle it.
 * 
 * <li> The addPageMessage method has been declared to streamline the process of
 * setting page-level messages. The accompanying showPageMessages.jsp file in
 * jsp/include/ automatically displays all of the page messages; the developer
 * need only include this file in the jsp.
 * 
 * <li> The addEntityList method makes it easy to add a Collection of
 * EntityBeans to the request. Note that this method should only be used for
 * Collections from which one EntityBean must be selected by the user. If the
 * Collection is empty, this method will throw an InconsistentStateException,
 * taking the user to an error page and settting a page message indicating that
 * the user may not proceed because no entities are present. Note that the error
 * page and the error message must be specified.
 * </ol>
 * 
 * @author ssachs, modified by ywang
 */
public abstract class SecureController extends HttpServlet implements SingleThreadModel {
    protected ServletContext context;
    protected SessionManager sm;
    // protected final Logger logger =
    // LoggerFactory.getLogger(getClass().getName());
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    protected String logDir;
    protected String logLevel;
    protected HttpSession session;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected UserAccountBean ub;
    protected StudyBean currentStudy;
    protected StudyUserRoleBean currentRole;
    protected HashMap errors = new HashMap();
    /**
     * local_df is set to the client locale in each request.
     */
    protected SimpleDateFormat local_df = new SimpleDateFormat("MM/dd/yyyy");
    public static ResourceBundle resadmin, resaudit, resexception, resformat, respage, resterm, restext, resword, resworkflow;

    protected StudyInfoPanel panel = new StudyInfoPanel();

    public static final String PAGE_MESSAGE = "pageMessages";// for showing
    // page
    // wide message

    public static final String INPUT_MESSAGES = "formMessages"; // for showing
    // input-specific
    // messages

    public static final String PRESET_VALUES = "presetValues"; // for setting
    // preset values

    public static final String ADMIN_SERVLET_CODE = "admin";

    public static final String BEAN_TABLE = "table";

    public static final String STUDY_INFO_PANEL = "panel"; // for setting the
    // side panel

    public static final String BREADCRUMB_TRAIL = "breadcrumbs";

    public static final String POP_UP_URL = "popUpURL";

    public static String DATASET_HOME_DIR = "OpenClinica";

    // Use this variable as the key for the support url
    public static final String SUPPORT_URL = "supportURL";

    public static final String MODULE = "module";// to determine which module

    // user is in

    // for setting the breadcrumb trail
    // protected HashMap errors = new HashMap();//error messages on the page

    protected void addPageMessage(String message) {
        ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

        if (pageMessages == null) {
            pageMessages = new ArrayList();
        }

        pageMessages.add(message);
        request.setAttribute(PAGE_MESSAGE, pageMessages);
    }

    protected void resetPanel() {
        panel.reset();
    }

    protected void setToPanel(String title, String info) {
        if (panel.isOrderedData()) {
            ArrayList data = panel.getUserOrderedData();
            data.add(new StudyInfoPanelLine(title, info));
            panel.setUserOrderedData(data);
        } else {
            panel.setData(title, info);
        }
        request.setAttribute(STUDY_INFO_PANEL, panel);
    }

    protected void setInputMessages(HashMap messages) {
        request.setAttribute(INPUT_MESSAGES, messages);
    }

    protected void setPresetValues(HashMap presetValues) {
        request.setAttribute(PRESET_VALUES, presetValues);
    }

    protected void setTable(EntityBeanTable table) {
        request.setAttribute(BEAN_TABLE, table);
    }

    @Override
    public void init() throws ServletException {
        context = getServletContext();
        DATASET_HOME_DIR = context.getInitParameter("datasetHomeDir");
    }

    /**
     * Process request
     * 
     * @throws Exception
     */
    protected abstract void processRequest() throws Exception;

    protected abstract void mayProceed() throws InsufficientPermissionException;

    public static final String USER_BEAN_NAME = "userBean";

    private void process(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException, UnsupportedEncodingException {

        request.setCharacterEncoding("UTF-8");

        session = request.getSession();
        // BWP >> 1/8/2008
        try {
            // YW 10-03-2007 <<
            session.setMaxInactiveInterval(Integer.parseInt(SQLInitServlet.getField("max_inactive_interval")));
            // YW >>
        } catch (NumberFormatException nfe) {
            // BWP>>3600 is the datainfo.properties maxInactiveInterval on
            // 1/8/2008
            session.setMaxInactiveInterval(3600);
        }

        // If the session already has a value with key SUPPORT_URL don't reset
        if (session.getAttribute(SUPPORT_URL) == null) {
            session.setAttribute(SUPPORT_URL, SQLInitServlet.getSupportURL());
        }

        ub = (UserAccountBean) session.getAttribute(USER_BEAN_NAME);
        currentStudy = (StudyBean) session.getAttribute("study");
        currentRole = (StudyUserRoleBean) session.getAttribute("userRole");

        // Set current language preferences
        Locale locale = request.getLocale();
        ResourceBundleProvider.updateLocale(locale);
        resadmin = ResourceBundleProvider.getAdminBundle(locale);
        resaudit = ResourceBundleProvider.getAuditEventsBundle(locale);
        resexception = ResourceBundleProvider.getExceptionsBundle(locale);
        resformat = ResourceBundleProvider.getFormatBundle(locale);
        restext = ResourceBundleProvider.getTextsBundle(locale);
        resterm = ResourceBundleProvider.getTermsBundle(locale);
        resword = ResourceBundleProvider.getWordsBundle(locale);
        respage = ResourceBundleProvider.getPageMessagesBundle(locale);
        resworkflow = ResourceBundleProvider.getWorkflowBundle(locale);

        local_df = new SimpleDateFormat(resformat.getString("date_format_string"));

        try {
            String userName = request.getRemoteUser();
            // BWP 1/8/08<< the sm variable may already be set with a mock
            // object,
            // from the perspective of
            // JUnit servlets tests
            /*
             * if(sm==null && (!StringUtil.isBlank(userName))) {//check if user
             * logged in, then create a new sessionmanger to get ub //create a
             * new sm in order to get a new ub object sm = new
             * SessionManager(ub, userName); }
             */
            // BWP 01/08 >>
            sm = new SessionManager(ub, userName);
            ub = sm.getUserBean();
            session.setAttribute("userBean", ub);

            StudyDAO sdao = new StudyDAO(sm.getDataSource());
            if (currentStudy == null || currentStudy.getId() <= 0) {
                if (ub.getId() > 0 && ub.getActiveStudyId() > 0) {
                    StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
                    currentStudy = (StudyBean) sdao.findByPK(ub.getActiveStudyId());

                    ArrayList studyParameters = spvdao.findParamConfigByStudy(currentStudy);

                    currentStudy.setStudyParameters(studyParameters);

                    StudyConfigService scs = new StudyConfigService(sm.getDataSource());
                    if (currentStudy.getParentStudyId() <= 0) {// top study
                        scs.setParametersForStudy(currentStudy);

                    } else {
                        // YW <<
                        currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId())).getName());
                        // YW >>
                        scs.setParametersForSite(currentStudy);
                    }

                    // set up the panel here, tbh
                    panel.reset();
                    /*
                     * panel.setData("Study", currentStudy.getName());
                     * panel.setData("Summary", currentStudy.getSummary());
                     * panel.setData("Start Date",
                     * sdf.format(currentStudy.getDatePlannedStart()));
                     * panel.setData("End Date",
                     * sdf.format(currentStudy.getDatePlannedEnd()));
                     * panel.setData("Principal Investigator",
                     * currentStudy.getPrincipalInvestigator());
                     */
                    session.setAttribute(STUDY_INFO_PANEL, panel);
                } else {
                    currentStudy = new StudyBean();
                }
                session.setAttribute("study", currentStudy);
            } else if (currentStudy.getId() > 0) {
                // YW 06-20-2007<< set site's parentstudy name when site is
                // restored
                if (currentStudy.getParentStudyId() > 0) {
                    currentStudy.setParentStudyName(((StudyBean) sdao.findByPK(currentStudy.getParentStudyId())).getName());
                }
                // YW >>
            }

            if (currentRole == null || currentRole.getId() <= 0) {
                // if (ub.getId() > 0 && currentStudy.getId() > 0) {
                // if current study has been "removed", current role will be
                // kept as "invalid" -- YW 06-21-2007
                if (ub.getId() > 0 && currentStudy.getId() > 0 && !currentStudy.getStatus().getName().equals("removed")) {
                    currentRole = ub.getRoleByStudy(currentStudy.getId());
                    if (currentStudy.getParentStudyId() > 0) {
                        // Checking if currentStudy has been removed or not will
                        // ge good enough -- YW 10-17-2007
                        StudyUserRoleBean roleInParent = ub.getRoleByStudy(currentStudy.getParentStudyId());
                        // inherited role from parent study, pick the higher
                        // role
                        currentRole.setRole(Role.max(currentRole.getRole(), roleInParent.getRole()));
                    }
                    // logger.info("currentRole:" + currentRole.getRoleName());
                } else {
                    currentRole = new StudyUserRoleBean();
                }
                session.setAttribute("userRole", currentRole);
            }
            // YW << For the case that current role is not "invalid" but current
            // active study has been removed.
            else if (currentRole.getId() > 0 && (currentStudy.getStatus().equals(Status.DELETED) || currentStudy.getStatus().equals(Status.AUTO_DELETED))) {
                currentRole.setRole(Role.INVALID);
                currentRole.setStatus(Status.DELETED);
                session.setAttribute("userRole", currentRole);
            }
            // YW 06-19-2007 >>

            request.setAttribute("isAdminServlet", getAdminServlet());

            this.request = request;
            this.response = response;

            // java.util.Enumeration en_session = session.getAttributeNames();
            // java.util.Enumeration en_request = request.getAttributeNames();
            //
            // // logging added to find problems with adding subjects, tbh
            // 102007
            // String ss_names = "session names: ";
            // while (en_session.hasMoreElements()) {
            // ss_names += " - " + en_session.nextElement();
            // }
            // logger.info(ss_names);
            //			
            // // also added tbh, 102007
            // String rq_names = "request names: ";
            // while (en_request.hasMoreElements()) {
            // rq_names += " - " + en_request.nextElement();
            // }
            // logger.info(rq_names);

            mayProceed();
            processRequest();
        } catch (InconsistentStateException ise) {
            ise.printStackTrace();
            logger.warn("InconsistentStateException: org.akaza.openclinica.control.SecureController: " + ise.getMessage());

            addPageMessage(ise.getOpenClinicaMessage());
            forwardPage(ise.getGoTo());
        } catch (InsufficientPermissionException ipe) {
            ipe.printStackTrace();
            logger.warn("InsufficientPermissionException: org.akaza.openclinica.control.SecureController: " + ipe.getMessage());

            // addPageMessage(ipe.getOpenClinicaMessage());
            forwardPage(ipe.getGoTo());
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("OpenClinicaException:: org.akaza.openclinica.control.SecureController:: " + e.getMessage());

            forwardPage(Page.ERROR);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        try {
            logger.debug("Request");
            process(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        try {
            logger.debug("Post");
            process(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <P>
     * Forwards to a jsp page. Additions to the forwardPage() method involve
     * checking the session for the bread crumb trail and setting it, if
     * necessary. Setting it here allows the developer to only have to update
     * the <code>BreadcrumbTrail</code> class.
     * 
     * @param jspPage
     *            The page to go to.
     * @param checkTrail
     *            The command to check for, and set a trail in the session.
     */
    protected void forwardPage(Page jspPage, boolean checkTrail) {
        // YW 10-03-2007 <<
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        response.setHeader("Cache-Control", "no-store");
        // YW >>

        if (request.getAttribute(POP_UP_URL) == null) {
            request.setAttribute(POP_UP_URL, "");
        }

        try {
            // Added 01/19/2005 for breadcrumbs, tbh
            if (checkTrail) {
                BreadcrumbTrail bt = new BreadcrumbTrail();
                if (session != null) {// added bu jxu, fixed bug for log out
                    ArrayList trail = (ArrayList) session.getAttribute("trail");
                    if (trail == null) {
                        trail = bt.generateTrail(jspPage, request);
                    } else {
                        bt.setTrail(trail);
                        trail = bt.generateTrail(jspPage, request);
                    }
                    session.setAttribute("trail", trail);
                    panel = (StudyInfoPanel) session.getAttribute(STUDY_INFO_PANEL);
                    if (panel == null) {
                        panel = new StudyInfoPanel();
                        panel.setData(jspPage, session, request);
                    } else {
                        panel.setData(jspPage, session, request);
                    }

                    /* 88888888888888888888888888888888888888888888888888888 */
                    StudySubjectDAO ssDAO = new StudySubjectDAO(sm.getDataSource());
                    // TODO find the right place for this, this is not the right
                    // place
                    Collection subjects = ssDAO.findAllByStudyId(currentStudy.getId());
                    int numOfSubjects = subjects.size();
                    // logger.warn("found subjects "+numOfSubjects);
                    if (panel.isStudyInfoShown()) {
                        ResourceBundle resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words", ResourceBundleProvider.getLocale());
                        setToPanel(resword.getString("number_of_subjects"), new Integer(numOfSubjects).toString());
                    }
                    /* 888888888888888888888888888888888888888888888888888 */
                    session.setAttribute(STUDY_INFO_PANEL, panel);
                }
                // we are also using checkTrail to update the panel, tbh
                // 01/31/2005
            }
            // above added 01/19/2005, tbh
            context.getRequestDispatcher(jspPage.getFileName()).forward(request, response);
        } catch (Exception se) {
            se.printStackTrace();
        }

    }

    protected void forwardPage(Page jspPage) {
        this.forwardPage(jspPage, true);
    }

    /**
     * This method supports functionality of the type "if a list of entities is
     * empty, then jump to some page and display an error message." This
     * prevents users from seeing empty drop-down lists and being given error
     * messages when they can't choose an entity from the drop-down list. Use,
     * e.g.:
     * <code>addEntityList("groups", allGroups, "There are no groups to display, so you cannot add a subject to this Study.",
     * Page.SUBMIT_DATA)</code>
     * 
     * @param beanName
     *            The name of the entity list as it should be stored in the
     *            request object.
     * @param list
     *            The Collection of entities.
     * @param messageIfEmpty
     *            The message to display if the collection is empty.
     * @param destinationIfEmpty
     *            The Page to go to if the collection is empty.
     * @throws InconsistentStateException
     */
    protected void addEntityList(String beanName, Collection list, String messageIfEmpty, Page destinationIfEmpty) throws InconsistentStateException {
        if (list.isEmpty()) {
            throw new InconsistentStateException(destinationIfEmpty, messageIfEmpty);
        }

        request.setAttribute(beanName, list);
    }

    /**
     * @return A blank String if this servlet is not an Administer System
     *         servlet. SecureController.ADMIN_SERVLET_CODE otherwise.
     */
    protected String getAdminServlet() {
        return "";
    }

    protected void setPopUpURL(String url) {
        if (url != null && request != null) {
            request.setAttribute(POP_UP_URL, url);
            // logger.info("just set pop up url: "+url);
        }
    }

    /**
     * Check if an entity with passed entity id is included in studies of
     * current user.
     * 
     * @author ywang 10-18-2007
     * @param entityId
     *            int
     * @param userName
     *            String
     * @param adao
     *            AuditableEntityDAO
     * @param ds
     *            javax.sql.DataSource
     */
    protected boolean entityIncluded(int entityId, String userName, AuditableEntityDAO adao, DataSource ds) {
        StudyDAO sdao = new StudyDAO(ds);
        ArrayList<StudyBean> studies = (ArrayList<StudyBean>) sdao.findAllByUserNotRemoved(userName);
        for (int i = 0; i < studies.size(); ++i) {
            if (adao.findByPKAndStudy(entityId, studies.get(i)).getId() > 0) {
                return true;
            }
            // Here follow the current logic - study subjects at sites level are
            // visible to parent studies.
            if (studies.get(i).getParentStudyId() <= 0) {
                ArrayList<StudyBean> sites = (ArrayList<StudyBean>) sdao.findAllByParent(studies.get(i).getId());
                if (sites.size() > 0) {
                    for (int j = 0; j < sites.size(); ++j) {
                        if (adao.findByPKAndStudy(entityId, sites.get(j)).getId() > 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * A inner class designed to allow the implementation of a JUnit test case
     * for abstract SecureController. The inner class allows the test case to
     * call the outer class' private process() method.
     * 
     * @author Bruce W. Perry 01/2008
     * @see org.akaza.openclinica.servlettests.SecureControllerServletTest
     * @see org.akaza.openclinica.servlettests.SecureControllerWrapper
     */
    public class SecureControllerTestDelegate {

        public SecureControllerTestDelegate() {
            super();
        }

        public void process(HttpServletRequest request, HttpServletResponse response) throws OpenClinicaException, UnsupportedEncodingException {
            SecureController.this.process(request, response);
        }
    }
}
