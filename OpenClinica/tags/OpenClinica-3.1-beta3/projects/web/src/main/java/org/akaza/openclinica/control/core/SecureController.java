/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package org.akaza.openclinica.control.core;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.BreadcrumbTrail;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.view.StudyInfoPanelLine;
import org.akaza.openclinica.web.InconsistentStateException;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.bean.EntityBeanTable;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
 * <li>The method mayProceed, for which the class is named, is declared abstract and is called before processRequest. This
 * method indicates whether the user may proceed with the action he wishes to perform (as indicated by various attributes or
 * parameters in request or session). Note, howeveer, that the method has a void return, and throws
 * InsufficientPermissionException. The intention is that if the user may not proceed with his desired action, the method
 * should throw an exception. InsufficientPermissionException will accept a Page object which indicates where the user should
 * be redirected in order to be informed that he has insufficient permission, and the process method enforces this redirection
 * by catching an InsufficientPermissionException object.
 * 
 * <li>Four new members, session, request, response, and the UserAccountBean object ub have been declared protected, and are
 * set in the process method. This allows developers to avoid passing these objects between methods, and moreover it
 * accurately encodes the fact that these objects represent the state of the servlet.
 * 
 * <br/>
 * In particular, please note that it is no longer necessary to generate a bean for the session manager, the current user or
 * the current study.
 * 
 * <li>The method processRequest has been declared abstract. This change is unlikely to affect most code, since by custom
 * processRequest is declared in each subclass anyway.
 * 
 * <li>The standard try-catch block within most processRequest methods has been included in the process method, which calls
 * the processRequest method. Therefore, subclasses may throw an Exception in the processRequest method without having to
 * handle it.
 * 
 * <li>The addPageMessage method has been declared to streamline the process of setting page-level messages. The accompanying
 * showPageMessages.jsp file in jsp/include/ automatically displays all of the page messages; the developer need only include
 * this file in the jsp.
 * 
 * <li>The addEntityList method makes it easy to add a Collection of EntityBeans to the request. Note that this method should
 * only be used for Collections from which one EntityBean must be selected by the user. If the Collection is empty, this
 * method will throw an InconsistentStateException, taking the user to an error page and settting a page message indicating
 * that the user may not proceed because no entities are present. Note that the error page and the error message must be
 * specified.
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

    private static String SCHEDULER = "schedulerFactoryBean";

    private StdScheduler scheduler;
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

    // public static String DATASET_HOME_DIR = "OpenClinica";

    // Use this variable as the key for the support url
    public static final String SUPPORT_URL = "supportURL";

    public static final String MODULE = "module";// to determine which module

    private static HashMap unavailableCRFList = new HashMap();

    // user is in

    // for setting the breadcrumb trail
    // protected HashMap errors = new HashMap();//error messages on the page

    protected void addPageMessage(String message) {
        ArrayList pageMessages = (ArrayList) request.getAttribute(PAGE_MESSAGE);

        if (pageMessages == null) {
            pageMessages = new ArrayList();
        }

        pageMessages.add(message);
        logger.debug(message);
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
        // DATASET_HOME_DIR = context.getInitParameter("datasetHomeDir");
    }

    /**
     * Process request
     * 
     * @throws Exception
     */
    protected abstract void processRequest() throws Exception;

    protected abstract void mayProceed() throws InsufficientPermissionException;

    public static final String USER_BEAN_NAME = "userBean";

    public void passwdTimeOut() {
        Date lastChangeDate = ub.getPasswdTimestamp();
        if (lastChangeDate == null) {
            addPageMessage(respage.getString("welcome") + " " + ub.getFirstName() + " " + ub.getLastName() + ". " + respage.getString("password_set"));
            // + "<a href=\"UpdateProfile\">" + respage.getString("user_profile") + " </a>");
            int pwdChangeRequired = new Integer(SQLInitServlet.getField("change_passwd_required")).intValue();
            if (pwdChangeRequired == 1) {
                request.setAttribute("mustChangePass", "yes");
                forwardPage(Page.RESET_PASSWORD);
            }
        }
    }

    private void pingJobServer(HttpServletRequest request) {
        String jobName = (String) request.getSession().getAttribute("jobName");
        String groupName = (String) request.getSession().getAttribute("groupName");
        Integer datasetId = (Integer) request.getSession().getAttribute("datasetId");
        try {
            if (jobName != null && groupName != null) {
                System.out.println("trying to retrieve status on " + jobName + " " + groupName);
                int state = getScheduler(request).getTriggerState(jobName, groupName);
                System.out.println("found state: " + state);
                org.quartz.JobDetail details = getScheduler(request).getJobDetail(jobName, groupName);
                List contexts = getScheduler(request).getCurrentlyExecutingJobs();
                // will we get the above, even if its completed running?
                // ProcessingResultType message = null;
                // for (int i = 0; i < contexts.size(); i++) {
                // org.quartz.JobExecutionContext context = (org.quartz.JobExecutionContext) contexts.get(i);
                // if (context.getJobDetail().getName().equals(jobName) &&
                // context.getJobDetail().getGroup().equals(groupName)) {
                // message = (ProcessingResultType) context.getResult();
                // System.out.println("found message " + message.getDescription());
                // }
                // }
                // ProcessingResultType message = (ProcessingResultType) details.getResult();
                org.quartz.JobDataMap dataMap = details.getJobDataMap();
                String failMessage = dataMap.getString("failMessage");
                if (state == Trigger.STATE_NONE) {
                    // add the message here that your export is done
                    System.out.println("adding a message!");
                    // TODO make absolute paths in the message, for example a link from /pages/* would break
                    // TODO i18n
                    if (failMessage != null) {
                        // The extract data job failed with the message:
                        // ERROR: relation "demographics" already exists
                        // More information may be available in the log files.
                        addPageMessage("The extract data job failed with the message: <br/><br/>" + failMessage
                            + "<br/><br/>More information may be available in the log files.");
                    } else {
                        String successMsg = dataMap.getString("SUCCESS_MESSAGE");
                        if (successMsg != null) {
                            if (successMsg.contains("$linkURL")) {
                                successMsg = decodeLINKURL(successMsg, datasetId);
                            }

                            addPageMessage("Your Extract is now completed. Please go to review them at <a href='ViewDatasets'>View Datasets</a> or <a href='ExportDataset?datasetId="
                                + datasetId + "'>View Specific Dataset</a>." + successMsg);
                        } else {
                            addPageMessage("Your Extract is now completed. Please go to review them at <a href='ViewDatasets'>View Datasets</a> or <a href='ExportDataset?datasetId="
                                + datasetId + "'>View Specific Dataset</a>.");
                        }
                    }
                    request.getSession().removeAttribute("jobName");
                    request.getSession().removeAttribute("groupName");
                    request.getSession().removeAttribute("datasetId");
                } else {

                }
            }
        } catch (SchedulerException se) {
            se.printStackTrace();
        }

    }

    private String decodeLINKURL(String successMsg, Integer datasetId) {

        ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(sm.getDataSource());

        ArrayList<ArchivedDatasetFileBean> fileBeans = asdfDAO.findByDatasetId(datasetId);

        successMsg =
            successMsg.replace("$linkURL", "<a href=\"" + CoreResources.getField("sysURL.base") + "AccessFile?fileId=" + fileBeans.get(0).getId()
                + "\">here </a>");

        return successMsg;
    }

    private StdScheduler getScheduler(HttpServletRequest request) {
        scheduler =
            this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(request.getSession().getServletContext()).getBean(
                    SCHEDULER);
        return scheduler;
    }

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
             * if(sm==null && (!StringUtil.isBlank(userName))) {//check if user logged in, then create a new sessionmanger to
             * get ub //create a new sm in order to get a new ub object sm = new SessionManager(ub, userName); }
             */
            // BWP 01/08 >>
            // sm = new SessionManager(ub, userName);
            sm = new SessionManager(ub, userName, SpringServletAccess.getApplicationContext(context));
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
                     * panel.setData("Study", currentStudy.getName()); panel.setData("Summary", currentStudy.getSummary());
                     * panel.setData("Start Date", sdf.format(currentStudy.getDatePlannedStart())); panel.setData("End Date",
                     * sdf.format(currentStudy.getDatePlannedEnd())); panel.setData("Principal Investigator",
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

            if (currentStudy.getParentStudyId() > 0) {
                /*
                 * The Role decription will be set depending on whether the user logged in at study lever or site level.
                 * issue-2422
                 */
                List roles = Role.toArrayList();
                for (Iterator it = roles.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                    case 2:
                        role.setDescription("site_Study_Coordinator");
                        break;
                    case 3:
                        role.setDescription("site_Study_Director");
                        break;
                    case 4:
                        role.setDescription("site_investigator");
                        break;
                    case 5:
                        role.setDescription("site_Data_Entry_Person");
                        break;
                    case 6:
                        role.setDescription("site_monitor");
                        break;
                    default:
                        logger.info("No role matched when setting role description");
                    }
                }
            } else {
                /*
                 * If the current study is a site, we will change the role description. issue-2422
                 */
                List roles = Role.toArrayList();
                for (Iterator it = roles.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    switch (role.getId()) {
                    case 2:
                        role.setDescription("Study_Coordinator");
                        break;
                    case 3:
                        role.setDescription("Study_Director");
                        break;
                    case 4:
                        role.setDescription("Investigator");
                        break;
                    case 5:
                        role.setDescription("Data_Entry_Person");
                        break;
                    case 6:
                        role.setDescription("Monitor");
                        break;
                    default:
                        logger.info("No role matched when setting role description");
                    }
                }
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
            if (!request.getRequestURI().endsWith("ResetPassword")) {
                passwdTimeOut();
            }
            mayProceed();
            pingJobServer(request);
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
        } catch (OutOfMemoryError ome) {
            ome.printStackTrace();
            long heapSize = Runtime.getRuntime().totalMemory();
            session.setAttribute("ome", "yes");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(SecureController.getStackTrace(e));

            forwardPage(Page.ERROR);
        }
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
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
     * @param request servlet request
     * @param response servlet response
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
     * Forwards to a jsp page. Additions to the forwardPage() method involve checking the session for the bread crumb trail
     * and setting it, if necessary. Setting it here allows the developer to only have to update the
     * <code>BreadcrumbTrail</code> class.
     * 
     * @param jspPage The page to go to.
     * @param checkTrail The command to check for, and set a trail in the session.
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

                    session.setAttribute(STUDY_INFO_PANEL, panel);
                }
                // we are also using checkTrail to update the panel, tbh
                // 01/31/2005
            }
            // above added 01/19/2005, tbh
            context.getRequestDispatcher(jspPage.getFileName()).forward(request, response);
        } catch (Exception se) {
            if ("View Notes".equals(jspPage.getTitle())) {
                String viewNotesURL = jspPage.getFileName();
                if (viewNotesURL != null && viewNotesURL.contains("listNotes_p_=")) {
                    String[] ps = viewNotesURL.split("listNotes_p_=");
                    String t = ps[1].split("&")[0];
                    int p = t.length() > 0 ? Integer.valueOf(t).intValue() : -1;
                    if (p > 1) {
                        viewNotesURL = viewNotesURL.replace("listNotes_p_=" + p, "listNotes_p_=" + (p - 1));
                        forwardPage(Page.setNewPage(viewNotesURL, "View Notes"));
                    } else if (p <= 0) {
                        forwardPage(Page.VIEW_DISCREPANCY_NOTES_IN_STUDY);
                    }
                }
            }
            se.printStackTrace();
        }

    }

    protected void forwardPage(Page jspPage) {
        this.forwardPage(jspPage, true);
    }

    /**
     * This method supports functionality of the type
     * "if a list of entities is empty, then jump to some page and display an error message." This prevents users from seeing
     * empty drop-down lists and being given error messages when they can't choose an entity from the drop-down list. Use,
     * e.g.:
     * <code>addEntityList("groups", allGroups, "There are no groups to display, so you cannot add a subject to this Study.",
     * Page.SUBMIT_DATA)</code>
     * 
     * @param beanName The name of the entity list as it should be stored in the request object.
     * @param list The Collection of entities.
     * @param messageIfEmpty The message to display if the collection is empty.
     * @param destinationIfEmpty The Page to go to if the collection is empty.
     * @throws InconsistentStateException
     */
    protected void addEntityList(String beanName, Collection list, String messageIfEmpty, Page destinationIfEmpty) throws InconsistentStateException {
        if (list.isEmpty()) {
            throw new InconsistentStateException(destinationIfEmpty, messageIfEmpty);
        }

        request.setAttribute(beanName, list);
    }

    /**
     * @return A blank String if this servlet is not an Administer System servlet. SecureController.ADMIN_SERVLET_CODE
     *         otherwise.
     */
    protected String getAdminServlet() {
        return "";
    }

    protected void setPopUpURL(String url) {
        if (url != null && request != null) {
            request.setAttribute(POP_UP_URL, url);
            logger.info("just set pop up url: " + url);
            System.out.println("just set pop up url: " + url);
        }
    }

    /**
     * <p>Check if an entity with passed entity id is included in studies of current user.</p>
     * 
     * <p>Note: This method called AuditableEntityDAO.findByPKAndStudy which required 
     * "The subclass must define findByPKAndStudyName before calling this
     * method. Otherwise an inactive AuditableEntityBean will be returned."</p>
     * @author ywang 10-18-2007
     * @param entityId int
     * @param userName String
     * @param adao AuditableEntityDAO
     * @param ds javax.sql.DataSource
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

    public String getRequestURLMinusServletPath() {
        String requestURLMinusServletPath = request.getRequestURL().toString().replaceAll(request.getServletPath(), "");
        return requestURLMinusServletPath;
    }

    public String getHostPath() {
        String requestURLMinusServletPath = getRequestURLMinusServletPath();
        return requestURLMinusServletPath.substring(0, requestURLMinusServletPath.lastIndexOf("/"));
    }

    public String getContextPath() {
        String contextPath = request.getContextPath().replaceAll("/", "");
        return contextPath;
    }

    /*
     * To check if the current study is LOCKED
     */
    public void checkStudyLocked(Page page, String message) {
        if (currentStudy.getStatus().equals(Status.LOCKED)) {
            addPageMessage(message);
            forwardPage(page);
        }
    }

    public void checkStudyLocked(String url, String message) {
        try {
            if (currentStudy.getStatus().equals(Status.LOCKED)) {
                addPageMessage(message);
                response.sendRedirect(url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * To check if the current study is FROZEN
     */

    public void checkStudyFrozen(Page page, String message) {
        if (currentStudy.getStatus().equals(Status.FROZEN)) {
            addPageMessage(message);
            forwardPage(page);
        }
    }

    public void checkStudyFrozen(String url, String message) {
        try {
            if (currentStudy.getStatus().equals(Status.FROZEN)) {
                addPageMessage(message);
                response.sendRedirect(url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public ArrayList getEventDefinitionsByCurrentStudy() {
        StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
        StudyEventDefinitionDAO studyEventDefinitionDAO = new StudyEventDefinitionDAO(sm.getDataSource());
        int parentStudyId = currentStudy.getParentStudyId();
        ArrayList allDefs = new ArrayList();
        if (parentStudyId > 0) {
            StudyBean parentStudy = (StudyBean) studyDAO.findByPK(parentStudyId);
            allDefs = studyEventDefinitionDAO.findAllActiveByStudy(parentStudy);
        } else {
            parentStudyId = currentStudy.getId();
            allDefs = studyEventDefinitionDAO.findAllActiveByStudy(currentStudy);
        }
        return allDefs;
    }

    public ArrayList getStudyGroupClassesByCurrentStudy() {
        StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
        StudyGroupClassDAO studyGroupClassDAO = new StudyGroupClassDAO(sm.getDataSource());
        StudyGroupDAO studyGroupDAO = new StudyGroupDAO(sm.getDataSource());
        int parentStudyId = currentStudy.getParentStudyId();
        ArrayList studyGroupClasses = new ArrayList();
        if (parentStudyId > 0) {
            StudyBean parentStudy = (StudyBean) studyDAO.findByPK(parentStudyId);
            studyGroupClasses = studyGroupClassDAO.findAllActiveByStudy(parentStudy);
        } else {
            parentStudyId = currentStudy.getId();
            studyGroupClasses = studyGroupClassDAO.findAllActiveByStudy(currentStudy);
        }

        for (int i = 0; i < studyGroupClasses.size(); i++) {
            StudyGroupClassBean sgc = (StudyGroupClassBean) studyGroupClasses.get(i);
            ArrayList groups = studyGroupDAO.findAllByGroupClass(sgc);
            sgc.setStudyGroups(groups);
        }

        return studyGroupClasses;

    }

    protected UserDetails getUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        } else {
            return null;
        }
    }

    public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail, Boolean sendMessage) throws Exception {
        return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail, respage.getString("your_message_sent_succesfully"),
                respage.getString("mail_cannot_be_sent_to_admin"), sendMessage);
    }

    public Boolean sendEmail(String to, String subject, String body, Boolean htmlEmail) throws Exception {
        return sendEmail(to, EmailEngine.getAdminEmail(), subject, body, htmlEmail, respage.getString("your_message_sent_succesfully"),
                respage.getString("mail_cannot_be_sent_to_admin"), true);
    }

    public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail) throws Exception {
        return sendEmail(to, from, subject, body, htmlEmail, respage.getString("your_message_sent_succesfully"),
                respage.getString("mail_cannot_be_sent_to_admin"), true);
    }

    public Boolean sendEmail(String to, String from, String subject, String body, Boolean htmlEmail, String successMessage, String failMessage,
            Boolean sendMessage) throws Exception {
        Boolean messageSent = true;
        try {
            JavaMailSenderImpl mailSender = (JavaMailSenderImpl) SpringServletAccess.getApplicationContext(context).getBean("mailSender");
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, htmlEmail);
            helper.setFrom(from);
            helper.setTo(processMultipleImailAddresses(to.trim()));
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
            if (successMessage != null && sendMessage) {
                addPageMessage(successMessage);
            }
            logger.debug("Email sent successfully on {}", new Date());
        } catch (MailException me) {
            me.printStackTrace();
            if (failMessage != null && sendMessage) {
                addPageMessage(failMessage);
            }
            logger.debug("Email could not be sent on {} due to: {}", new Date(), me.toString());
            messageSent = false;
        }
        return messageSent;
    }

    private InternetAddress[] processMultipleImailAddresses(String to) throws MessagingException {
        ArrayList<String> recipientsArray = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(to, ",");
        while (st.hasMoreTokens()) {
            recipientsArray.add(st.nextToken());
        }

        int sizeTo = recipientsArray.size();
        InternetAddress[] addressTo = new InternetAddress[sizeTo];
        for (int i = 0; i < sizeTo; i++) {
            addressTo[i] = new InternetAddress(recipientsArray.get(i).toString());
        }
        return addressTo;

    }

    public synchronized static void removeLockedCRF(int userId) {
        for (Iterator iter = getUnavailableCRFList().entrySet().iterator(); iter.hasNext();) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
            int id = (Integer) entry.getValue();
            if (id == userId)
                getUnavailableCRFList().remove(entry.getKey());
        }
    }

    public synchronized void lockThisEventCRF(int ecb, int ub) {
        getUnavailableCRFList().put(ecb, ub);
    }

    public synchronized static HashMap getUnavailableCRFList() {
        return unavailableCRFList;
    }

    public void dowloadFile(File f, String contentType) throws Exception {

        response.setHeader("Content-disposition", "attachment; filename=\"" + f.getName() + "\";");
        response.setContentType("text/xml");
        response.setHeader("Pragma", "public");

        ServletOutputStream op = response.getOutputStream();

        DataInputStream in = null;
        try {
            response.setContentType("text/xml");
            response.setHeader("Pragma", "public");
            response.setContentLength((int) f.length());

            byte[] bbuf = new byte[(int) f.length()];
            in = new DataInputStream(new FileInputStream(f));
            int length;
            while (in != null && (length = in.read(bbuf)) != -1) {
                op.write(bbuf, 0, length);
            }

            in.close();
            op.flush();
            op.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (op != null) {
                op.close();
            }
        }
    }

    public String getPageServletFileName() {
        String fileName = request.getServletPath();
        String temp = request.getPathInfo();
        if (temp != null) {
            fileName += temp;
        }
        temp = request.getQueryString();
        if (temp != null && temp.length() > 0) {
            fileName += "?" + temp;
        }
        return fileName;
    }

    public String getPageURL() {
        String url = request.getRequestURL().toString();
        String query = request.getQueryString();
        if (url != null && url.length() > 0 && query != null) {
            url += "?" + query;
        }
        return url;
    }

    /**
     * A inner class designed to allow the implementation of a JUnit test case for abstract SecureController. The inner class
     * allows the test case to call the outer class' private process() method.
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