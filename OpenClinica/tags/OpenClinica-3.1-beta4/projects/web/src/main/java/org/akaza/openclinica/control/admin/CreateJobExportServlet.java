package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.ExtractPropertyBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.service.extract.XsltTriggerService;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.job.ExampleSpringJob;
import org.akaza.openclinica.web.job.TriggerService;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdScheduler;
import org.springframework.scheduling.quartz.JobDetailBean;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author thickerson
 * 
 */
public class CreateJobExportServlet extends SecureController {
    public static final String PERIOD = "periodToRun";
    public static final String FORMAT_ID = "formatId";
    public static final String DATASET_ID = "dsId";
    public static final String DATE_START_JOB = "job";
    public static final String EMAIL = "contactEmail";
    public static final String JOB_NAME = "jobName";
    public static final String JOB_DESC = "jobDesc";
    public static final String USER_ID = "user_id";
    public static final String STUDY_NAME = "study_name";

    private static String SCHEDULER = "schedulerFactoryBean";
    // faking out DRY - should we create a super class, Job Servlet, which
    // captures the scheduler?
    private StdScheduler scheduler;

    // private SimpleTrigger trigger;
    // private JobDataMap jobDataMap;

    // private FormProcessor fp;

    @Override
    protected void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {// ?
            // ?
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MENU, resexception.getString("not_allowed_access_extract_data_servlet"), "1");// TODO
        // above copied from create dataset servlet, needs to be changed to
        // allow only admin-level users

    }

    private StdScheduler getScheduler() {
        scheduler = this.scheduler != null ? scheduler : (StdScheduler) SpringServletAccess.getApplicationContext(context).getBean(SCHEDULER);
        return scheduler;
    }

    private void setUpServlet() {

        // TODO find all the form items and re-populate them if necessary
        FormProcessor fp2 = new FormProcessor(request);
        // Enumeration enumeration = request.getAttributeNames();
        // while (enumeration.hasMoreElements()) {
        // String attrName = (String)enumeration.nextElement();
        // if (fp.getString(attrName) != null) {
        // request.setAttribute(attrName, fp.getString(attrName));
        // }
        // // possible error with dates? yep
        // }
        DatasetDAO dsdao = new DatasetDAO(sm.getDataSource());
        Collection dsList = dsdao.findAllOrderByStudyIdAndName();
        // TODO will have to dress this up to allow for sites then datasets
        request.setAttribute("datasets", dsList);
        request.setAttribute(JOB_NAME, fp2.getString(JOB_NAME));
        request.setAttribute(JOB_DESC, fp2.getString(JOB_DESC));
        request.setAttribute("extractProperties", CoreResources.getExtractProperties());
        request.setAttribute(EMAIL, fp2.getString(EMAIL));
        request.setAttribute(FORMAT_ID, fp2.getInt(FORMAT_ID));
        request.setAttribute(PERIOD, fp2.getString(PERIOD));
        request.setAttribute(DATASET_ID, fp2.getInt(DATASET_ID));
        Date jobDate = (fp2.getDateTime(DATE_START_JOB));
        HashMap presetValues = new HashMap();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(jobDate);
        presetValues.put(DATE_START_JOB + "Hour", calendar.get(Calendar.HOUR_OF_DAY));
        presetValues.put(DATE_START_JOB + "Minute", calendar.get(Calendar.MINUTE));
        presetValues.put(DATE_START_JOB + "Date", local_df.format(jobDate));
        // (calendar.get(Calendar.MONTH) + 1) + "/" +
        // calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR));
        fp2.setPresetValues(presetValues);
        setPresetValues(fp2.getPresetValues());
        request.setAttribute(DATE_START_JOB, fp2.getDateTime(DATE_START_JOB + "Date"));
        // EMAIL, TAB, CDISC, SPSS, PERIOD, DATE_START_JOB
        // TODO pick out the datasets and the date
    }

    @Override
    protected void processRequest() throws Exception {
        // TODO multi stage servlet which will create export jobs
        // will accept, create, and return the ViewJob servlet
        FormProcessor fp = new FormProcessor(request);
        TriggerService triggerService = new TriggerService();
        scheduler = getScheduler();
        String action = fp.getString("action");
        if (StringUtil.isBlank(action)) {
            // set up list of data sets
            // select by ... active study
            setUpServlet();

            forwardPage(Page.CREATE_JOB_EXPORT);
        } else if ("confirmall".equalsIgnoreCase(action)) {
            // collect form information
            HashMap errors = validateForm(fp, request, scheduler.getTriggerNames("DEFAULT"), "");

            if (!errors.isEmpty()) {
                // set errors to request
                request.setAttribute("formMessages", errors);
                logger.info("has validation errors in the first section");
                logger.info("errors found: " + errors.toString());
                setUpServlet();

                forwardPage(Page.CREATE_JOB_EXPORT);
            } else {
                logger.info("found no validation errors, continuing");

                StudyDAO studyDAO = new StudyDAO(sm.getDataSource());
                DatasetDAO datasetDao = new DatasetDAO(sm.getDataSource());

                UserAccountBean userBean = (UserAccountBean) request.getSession().getAttribute("userBean");
                CoreResources cr =  new CoreResources();
                int datasetId = fp.getInt(DATASET_ID);
                String period = fp.getString(PERIOD);
                String email = fp.getString(EMAIL);
                String jobName = fp.getString(JOB_NAME);
                String jobDesc = fp.getString(JOB_DESC);
                Date startDateTime = fp.getDateTime(DATE_START_JOB);

                Integer exportFormatId = fp.getInt(FORMAT_ID);

                ExtractPropertyBean epBean = cr.findExtractPropertyBeanById(exportFormatId, "" + datasetId);
                DatasetBean dsBean = (DatasetBean)datasetDao.findByPK(new Integer(datasetId).intValue());

                // set the job in motion
                String[] files = epBean.getFileName();
                String exportFileName;
                int fileSize = files.length;
                int  cnt = 0;
                dsBean.setName(dsBean.getName().replaceAll(" ", "_"));
                String[] exportFiles= epBean.getExportFileName();
                 String pattern = "yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HHmmssSSS" + File.separator;
                 SimpleDateFormat sdfDir = new SimpleDateFormat(pattern);
                int i =0;
                String[] temp = new String[exportFiles.length];
                //JN: The following logic is for comma separated variables, to avoid the second file be treated as a old file and deleted.
                String datasetFilePath = SQLInitServlet.getField("filePath")+"datasets";

                while(i<exportFiles.length)
                {
                    temp[i] = XsltTriggerService.resolveVars(exportFiles[i],dsBean,sdfDir, datasetFilePath);
                    i++;
                }
                epBean.setDoNotDelFiles(temp);
                epBean.setExportFileName(temp);

                XsltTriggerService xsltService = new XsltTriggerService();
                String generalFileDir = SQLInitServlet.getField("filePath");

                generalFileDir = generalFileDir + "datasets" + File.separator + dsBean.getId() + File.separator + sdfDir.format(new java.util.Date());

                exportFileName = epBean.getExportFileName()[cnt];


                // need to set the dataset path here, tbh
                // next, can already run jobs, translations, and then add a message to be notified later
                //JN all the properties need to have the variables...
                String xsltPath = SQLInitServlet.getField("filePath") + "xslt" + File.separator +files[cnt];
                String endFilePath = epBean.getFileLocation();
                endFilePath  =  XsltTriggerService.getEndFilePath(endFilePath, dsBean, sdfDir, datasetFilePath);
              //  exportFileName = resolveVars(exportFileName,dsBean,sdfDir);
                if(epBean.getPostProcExportName()!=null)
                {
                    //String preProcExportPathName = getEndFilePath(epBean.getPostProcExportName(),dsBean,sdfDir);
                    String preProcExportPathName = XsltTriggerService.resolveVars(epBean.getPostProcExportName(),dsBean,sdfDir, datasetFilePath);
                    epBean.setPostProcExportName(preProcExportPathName);
                }
                if(epBean.getPostProcLocation()!=null)
                {
                    String prePocLoc = XsltTriggerService.getEndFilePath(epBean.getPostProcLocation(), dsBean, sdfDir, datasetFilePath);
                    epBean.setPostProcLocation(prePocLoc);
                }
                XsltTriggerService.setAllProps(epBean, dsBean, sdfDir, datasetFilePath);
                SimpleTrigger trigger = null;

                trigger = xsltService.generateXsltTrigger(xsltPath,
                        generalFileDir, // xml_file_path
                        endFilePath + File.separator,
                        exportFileName,
                        dsBean.getId(),
                        epBean, userBean, request.getLocale().getLanguage(),cnt,  SQLInitServlet.getField("filePath") + "xslt", XsltTriggerService.TRIGGER_GROUP_NAME);

                //Updating the original trigger with user given inputs
                trigger.setRepeatCount(64000);
                trigger.setRepeatInterval(XsltTriggerService.getIntervalTime(period));
                trigger.setDescription(jobDesc);
                // set just the start date
                trigger.setStartTime(startDateTime);
                trigger.setName(jobName);// + datasetId);
                trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
                trigger.getJobDataMap().put(XsltTriggerService.EMAIL, email);
                trigger.getJobDataMap().put(XsltTriggerService.PERIOD, period);
                trigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT, epBean.getFiledescription());
                trigger.getJobDataMap().put(XsltTriggerService.EXPORT_FORMAT_ID, exportFormatId);
                trigger.getJobDataMap().put(XsltTriggerService.JOB_NAME, jobName);

                JobDetailBean jobDetailBean = new JobDetailBean();
                jobDetailBean.setGroup(xsltService.TRIGGER_GROUP_NAME);
                jobDetailBean.setName(trigger.getName());
                jobDetailBean.setJobClass(org.akaza.openclinica.job.XsltStatefulJob.class);
                jobDetailBean.setJobDataMap(trigger.getJobDataMap());
                jobDetailBean.setDurability(true); // need durability?
                jobDetailBean.setVolatility(false);

                // set to the scheduler
                try {
                    Date dateStart = scheduler.scheduleJob(jobDetailBean, trigger);
                    logger.info("== found job date: " + dateStart.toString());
                    // set a success message here
                } catch (SchedulerException se) {
                    se.printStackTrace();
                    setUpServlet();
                    addPageMessage("Error creating Job.");
                    forwardPage(Page.VIEW_JOB_SERVLET);
                    return;
                }
                setUpServlet();
                addPageMessage("You have successfully created a new job: " + jobName + " which is now set to run at the time you specified.");
                forwardPage(Page.VIEW_JOB_SERVLET);
            }
        } else {
            forwardPage(Page.ADMIN_SYSTEM);
            // forward to form
            // should we even get to this part?
        }
    }

    public HashMap validateForm(FormProcessor fp, HttpServletRequest request, String[] triggerNames, String properName) {
        Validator v = new Validator(request);
        v.addValidation(JOB_NAME, Validator.NO_BLANKS);
        // need to be unique too
        v.addValidation(JOB_DESC, Validator.NO_BLANKS);
        v.addValidation(EMAIL, Validator.IS_A_EMAIL);
        v.addValidation(PERIOD, Validator.NO_BLANKS);
        v.addValidation(DATE_START_JOB + "Date", Validator.IS_A_DATE);
        // v.addValidation(DATE_START_JOB + "Date", new Date(), Validator.DATE_IS_AFTER_OR_EQUAL);
        // TODO job names will have to be unique, tbh

        int formatId = fp.getInt(FORMAT_ID);
        Date jobDate = fp.getDateTime(DATE_START_JOB);
        HashMap errors = v.validate();
        if (formatId == 0) {
            // throw an error here, at least one should work
            // errors.put(TAB, "Error Message - Pick one of the below");
            v.addError(errors, FORMAT_ID, "Please pick at least one.");
        }
        for (String triggerName : triggerNames) {
            if (triggerName.equals(fp.getString(JOB_NAME)) && (!triggerName.equals(properName))) {
                v.addError(errors, JOB_NAME, "A job with that name already exists.  Please pick another name.");
            }
        }
        if (jobDate.before(new Date())) {
            v.addError(errors, DATE_START_JOB + "Date", "This date needs to be later than the present time.");
        }
        return errors;
    }
}