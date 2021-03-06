package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.service.job.TriggerService;
import org.akaza.openclinica.view.Page;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.JobDetailBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Create Job Import Servlet, by Tom Hickerson, 2009
 * 
 * @author thickerson Purpose: to create jobs in the 'importTrigger' group,
 *         which will be meant to run the ImportStatefulJob.
 */
public class CreateJobImportServlet extends SecureController {

    private static String SCHEDULER = "schedulerFactoryBean";
    private static String IMPORT_TRIGGER = "importTrigger";

    public static final String DATE_START_JOB = "job";
    public static final String EMAIL = "contactEmail";
    public static final String JOB_NAME = "jobName";
    public static final String JOB_DESC = "jobDesc";
    public static final String USER_ID = "user_id";
    public static final String HOURS = "hours";
    public static final String MINUTES = "minutes";
    public static final String DIRECTORY = "filePathDir";
    public static final String DIR_PATH = "scheduled_data_import";
    public static final String STUDY_ID = "studyId";

    private StdScheduler scheduler;

    // private SimpleTrigger trigger;
    // private JobDataMap jobDataMap;

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {// ?

            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO

        // allow only admin-level users

    }

    private StdScheduler getScheduler() {
        scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(context).getBean(SCHEDULER);
        return scheduler;
    }

    /*
     * Find all the form items and re-populate as necessary
     */
    private void setUpServlet() {
        String directory = SQLInitServlet.getField("filePath") + DIR_PATH + File.separator;
        System.out.println("found directory: " + directory);
        // find all the form items and re-populate them if necessary
        FormProcessor fp2 = new FormProcessor(request);

        UserAccountDAO udao = new UserAccountDAO(sm.getDataSource());
        StudyDAO sdao = new StudyDAO(sm.getDataSource());

        ArrayList studies = udao.findStudyByUser(ub.getName(), (ArrayList) sdao.findAll());
        request.setAttribute("studies", studies);
        // Date jobDate = new Date();
        // jobDate = (fp2.getDateTime(DATE_START_JOB));
        // HashMap presetValues = new HashMap();
        // // presetValues.put("filePath", directory);
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(jobDate);
        // presetValues.put(DATE_START_JOB + "Hour",
        // calendar.get(Calendar.HOUR_OF_DAY));
        // presetValues.put(DATE_START_JOB + "Minute",
        // calendar.get(Calendar.MINUTE));
        // // TODO this will have to match l10n formatting
        // String preparedDate = (calendar.get(Calendar.MONTH) + 1) + "/" +
        // calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR);
        // SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        // try {
        // Date preparedDate2 = sdf.parse(preparedDate);
        // String preparedDate3 = local_df.format(preparedDate2);
        // presetValues.put(DATE_START_JOB + "Date", preparedDate3);
        // } catch (ParseException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // presetValues.put(DATE_START_JOB + "Date",
        // (calendar.get(Calendar.MONTH) + 1) + "/" +
        // calendar.get(Calendar.DATE) + "/" +
        // calendar.get(Calendar.YEAR));
        // fp2.setPresetValues(presetValues);
        // setPresetValues(fp2.getPresetValues());
        request.setAttribute("filePath", directory);
        // try {
        // request.setAttribute(DATE_START_JOB, fp2.getDateTime(DATE_START_JOB +
        // "Date"));
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // System.out.println("reached exception " + e.getMessage());
        // request.setAttribute(DATE_START_JOB, new Date());
        // }
        request.setAttribute(JOB_NAME, fp2.getString(JOB_NAME));
        request.setAttribute(JOB_DESC, fp2.getString(JOB_DESC));
        request.setAttribute(EMAIL, fp2.getString(EMAIL));
        request.setAttribute(HOURS, new Integer(fp2.getInt(HOURS)).toString());
        request.setAttribute(MINUTES, new Integer(fp2.getInt(MINUTES)).toString());

    }

    @Override
    protected void processRequest() throws Exception {
        // TODO multi stage servlet to generate import jobs
        // validate form, create job and return to view jobs servlet
        FormProcessor fp = new FormProcessor(request);
        TriggerService triggerService = new TriggerService();
        scheduler = getScheduler();
        String action = fp.getString("action");
        if (StringUtil.isBlank(action)) {
            // set up list of data sets
            // select by ... active study
            setUpServlet();

            forwardPage(Page.CREATE_JOB_IMPORT);
        } else if ("confirmall".equalsIgnoreCase(action)) {
            // collect form information
            HashMap errors = triggerService.validateImportJobForm(fp, request, scheduler.getTriggerNames(IMPORT_TRIGGER));

            if (!errors.isEmpty()) {
                // set errors to request
                request.setAttribute("formMessages", errors);
                System.out.println("has validation errors in the first section");
                System.out.println("errors found: " + errors.toString());
                setUpServlet();

                forwardPage(Page.CREATE_JOB_IMPORT);
            } else {
                logger.info("found no validation errors, continuing");
                int studyId = fp.getInt(STUDY_ID);
                StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
                StudyBean studyBean = (StudyBean) studyDAO.findByPK(studyId);
                SimpleTrigger trigger = triggerService.generateImportTrigger(fp, sm.getUserBean(), studyBean);

                // SimpleTrigger trigger = new SimpleTrigger();
                JobDetailBean jobDetailBean = new JobDetailBean();
                jobDetailBean.setGroup(IMPORT_TRIGGER);
                jobDetailBean.setName(trigger.getName());
                jobDetailBean.setJobClass(org.akaza.openclinica.service.job.ImportStatefulJob.class);
                jobDetailBean.setJobDataMap(trigger.getJobDataMap());
                jobDetailBean.setDurability(true); // need durability?
                jobDetailBean.setVolatility(false);

                // set to the scheduler
                try {
                    Date dateStart = scheduler.scheduleJob(jobDetailBean, trigger);
                    System.out.println("== found job date: " + dateStart.toString());
                    // set a success message here
                    addPageMessage("You have successfully created a new job: " + trigger.getName() + " which is now set to run at the time you specified.");
                    forwardPage(Page.VIEW_IMPORT_JOB_SERVLET);
                } catch (SchedulerException se) {
                    se.printStackTrace();
                    // set a message here with the exception message
                    setUpServlet();
                    addPageMessage("There was an unspecified error with your creation, please contact an administrator.");
                    forwardPage(Page.CREATE_JOB_IMPORT);
                }
            }
        } else {
            forwardPage(Page.ADMIN_SYSTEM);
            // forward to form
            // should we even get to this part?
        }

    }

}
