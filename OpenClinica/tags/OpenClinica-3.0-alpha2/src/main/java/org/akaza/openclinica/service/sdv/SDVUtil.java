package org.akaza.openclinica.service.sdv;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.controller.helper.table.SDVToolbar;
import org.akaza.openclinica.controller.helper.SdvFilterDataBean;

import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.controller.helper.table.SubjectSDVContainer;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.domain.SourceDataVerification;

import org.jmesa.core.filter.MatcherKey;
import org.jmesa.facade.TableFacade;
import static org.jmesa.facade.TableFacadeFactory.createTableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.limit.Filter;
import org.jmesa.limit.FilterSet;
import org.jmesa.limit.SortSet;
import org.jmesa.util.ItemUtils;
import org.jmesa.view.html.editor.HtmlCellEditor;
import org.jmesa.view.editor.DateCellEditor;
import org.jmesa.view.html.component.HtmlTable;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlColumn;

import org.springframework.validation.BindingResult;

import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * A utility class that implements the details of the Source Data Verification (SDV) Jmesa tables.
 */
public class SDVUtil {

    private final static String VIEW_ICON_FORSUBJECT_PREFIX =
      "<a onmouseup=\"javascript:setImage('bt_View1','images/bt_View.gif');\" onmousedown=\"javascript:setImage('bt_View1','images/bt_View_d.gif');\" href=\"ViewStudySubject?id=";
    private final static String VIEW_ICON_FORSUBJECT_SUFFIX = "\"><img hspace=\"6\" border=\"0\" align=\"left\" title=\"View\" alt=\"View\" src=\"../images/bt_View.gif\" name=\"bt_View1\"/></a>";
    private final static String ICON_FORCRFSTATUS_PREFIX = "<img hspace='2' border='0'  title='Event CRF Status' alt='Event CRF Status' src='../images/icon_";

    private final static String ICON_FORCRFSTATUS_SUFFIX = ".gif'/>";
    public final static String CHECKBOX_NAME = "sdvCheck_";

    public final static Map<Integer,String> SUBJECT_EVENT_STATUS_ICONS = new HashMap<Integer,String>();
    static {
        SUBJECT_EVENT_STATUS_ICONS.put(0,"Invalid");
        SUBJECT_EVENT_STATUS_ICONS.put(1,"Scheduled");
        SUBJECT_EVENT_STATUS_ICONS.put(2,"NotStarted");
        SUBJECT_EVENT_STATUS_ICONS.put(3,"InitialDE");
        SUBJECT_EVENT_STATUS_ICONS.put(4,"DEcomplete");
        SUBJECT_EVENT_STATUS_ICONS.put(5,"Stopped");
        SUBJECT_EVENT_STATUS_ICONS.put(6,"Skipped");
        SUBJECT_EVENT_STATUS_ICONS.put(7,"Locked");
        SUBJECT_EVENT_STATUS_ICONS.put(8,"Signed");
    }

    private DataSource dataSource;

    public NoEscapeHtmlCellEditor getCellEditorNoEscapes() {
        return new NoEscapeHtmlCellEditor();
    }

    public int setDataAndLimitVariables(TableFacade tableFacade,int studyId,
                                        HttpServletRequest request) {

        Limit limit = tableFacade.getLimit();
        FilterSet filterSet = limit.getFilterSet();
        int totalRows = getTotalRowCount(filterSet,studyId);

        tableFacade.setTotalRows(totalRows);
        SortSet sortSet = limit.getSortSet();
        int rowStart = limit.getRowSelect().getRowStart();
        int rowEnd = limit.getRowSelect().getRowEnd();
        Collection<SubjectSDVContainer> items = getFilteredItems(filterSet, sortSet, rowStart, rowEnd,studyId,request);

        tableFacade.setItems(items);


        return totalRows;
    }

    private Collection<SubjectSDVContainer> getFilteredItems(FilterSet filterSet,SortSet sortSet,
                                                             int rowStart,int rowEnd,int studyId,HttpServletRequest request) {

        EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);
        List<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();

      /*  if(filterSet.getFilters().size() == 0 && sortSet.getSorts().size() == 0 &&
          (rowStart < 0 || rowEnd == 0)) {
            eventCRFBeans = eventCRFDAO.getEventCRFsByStudy(studyId,);
            return this.getSubjectRows(eventCRFBeans,request);
        }*/
        String label = "";
        String eventName = "";

        if (filterSet.getFilter("studySubjectId") != null){
            label =   filterSet.getFilter("studySubjectId").getValue();
            eventCRFBeans = eventCRFDAO.getEventCRFsByStudySubjectLabelLimit(label,
              rowEnd-rowStart,rowStart);
            
        } else if(filterSet.getFilter("eventName") != null ) {

            eventName =  filterSet.getFilter("eventName").getValue();
            eventCRFBeans = eventCRFDAO.getEventCRFsByEventNameLimit(eventName,
              rowEnd-rowStart,rowStart);

        } else {
            eventCRFBeans = eventCRFDAO.getEventCRFsByStudy(studyId, rowEnd-rowStart,rowStart);

        }



        return getSubjectRows(eventCRFBeans,request);
    }

    private int getTotalRowCount(FilterSet filterSet,int studyId) {

        /*{"studySubjectId","personId","secondaryId",
        "eventName", "eventDate","enrollmentDate","studySubjectStatus","crfNameVersion","crfStatus",
        "lastUpdatedDate","lastUpdatedBy","sdvStatusActions"}*/
        EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);

        if(filterSet.getFilters().size() == 0) {
            return eventCRFDAO.countEventCRFsByStudy(studyId);
        }

        int count = 0;
        //Filter for study subject label
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        StudySubjectBean studySubjectBean = new StudySubjectBean();
        String subjectValue="";
        String eventNameValue="";

        for(Filter filter : filterSet.getFilters()) {

            if(filter.getProperty().equalsIgnoreCase("studySubjectId")){
                subjectValue = filter.getValue();
            }
            if(filter.getProperty().equalsIgnoreCase("eventName")){
                eventNameValue = filter.getValue();
            }
        }
        //return all event CRFs if there are no filters
        if((subjectValue == null || "".equalsIgnoreCase(subjectValue))
          && (eventNameValue == null || "".equalsIgnoreCase(eventNameValue))) {
            return eventCRFDAO.countEventCRFsByStudy(studyId);
        }

        if(subjectValue.length() > 0) {
            return eventCRFDAO.countEventCRFsByStudySubjectLabel(subjectValue);
        }

        if(eventNameValue.length() > 0) {
            return eventCRFDAO.countEventCRFsByEventName(eventNameValue);
        }

        return eventCRFDAO.countEventCRFsByStudy(studyId);
    }
    public String renderAllEventCRFTable(List<EventCRFBean> eventCRFBeans,
                                         HttpServletRequest request){

        Collection<SubjectSDVContainer> items = getSubjectRows(eventCRFBeans,request);

        //The number of items represents the total number of returned rows
        int totalRowCount =0;
        if(items != null && items.size() > 0) {
            totalRowCount = items.size();
        }
        TableFacade tableFacade = createTableFacade("sdv", request);
        tableFacade.setStateAttr("restore");

        String[] allColumns = new String[]{"studySubjectId","personId","secondaryId",
          "eventName", "eventDate","enrollmentDate","studySubjectStatus","crfNameVersion","crfStatus",
          "lastUpdatedDate","lastUpdatedBy","sdvStatusActions"};

        tableFacade.setColumnProperties("studySubjectId","personId","secondaryId",
          "eventName",
          "eventDate","enrollmentDate","studySubjectStatus","crfNameVersion","crfStatus",
          "lastUpdatedDate",
          "lastUpdatedBy","sdvStatusActions");

        tableFacade.addFilterMatcher(new MatcherKey(String.class, "studySubjectStatus"), new SubjectStatusMatcher());
        tableFacade.setItems(items);

        HtmlRow row = (HtmlRow) tableFacade.getTable().getRow();
        HtmlColumn studySubjectStatus = row.getColumn("studySubjectStatus");
        studySubjectStatus.getFilterRenderer().setFilterEditor(new SubjectStatusFilter());


        //fix HTML in columns
        setHtmlCellEditors(tableFacade,allColumns,true);


        //Create the custom toolbar
        SDVToolbar  sDVToolbar = new SDVToolbar();

        // if(totalRowCount > 0){
        if(totalRowCount <= 25){
            sDVToolbar.setMaxRowsIncrements(new int[]{10,15,totalRowCount});}
        else if(totalRowCount <= 50){
            sDVToolbar.setMaxRowsIncrements(new int[]{10,25,totalRowCount});
        } else if(totalRowCount > 0) {
            sDVToolbar.setMaxRowsIncrements(new int[]{15,50,100});
        }
        tableFacade.setToolbar(sDVToolbar);

        //Fix column titles
        HtmlTable table = (HtmlTable) tableFacade.getTable();
        //i18n caption; TODO: convert to Spring messages
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
          "org.akaza.openclinica.i18n.words",request.getLocale());

        String[] allTitles = {"Study Subject Id","Person Id","Secondary Id" ,"Event Name",
          "Event Date","Enrollment Date","Subject Status","CRF Name / Version","CRF Status",
          "Last Updated Date",
          "Last Updated By","SDV Status / Actions"};

        setTitles(allTitles,table);

        //format column dates
        formatColumns(table,new String[]{"eventDate","enrollmentDate","lastUpdatedDate"},
          request);

        table.getTableRenderer().setWidth("800");
        return tableFacade.render();
    }

    public String renderEventCRFTableWithLimit(HttpServletRequest request, int studyId){

        TableFacade tableFacade = createTableFacade("sdv", request);
        tableFacade.setStateAttr("restore");

        String[] allColumns = new String[]{"studySubjectId","personId","secondaryId",
          "eventName", "eventDate","enrollmentDate","studySubjectStatus","crfNameVersion","crfStatus",
          "lastUpdatedDate","lastUpdatedBy","sdvStatusActions"};

        tableFacade.setColumnProperties("studySubjectId","personId","secondaryId",
          "eventName",
          "eventDate","enrollmentDate","studySubjectStatus","crfNameVersion","crfStatus",
          "lastUpdatedDate",
          "lastUpdatedBy","sdvStatusActions");

        tableFacade.addFilterMatcher(new MatcherKey(String.class, "studySubjectStatus"), new SubjectStatusMatcher());
        int totalRowCount = this.setDataAndLimitVariables(tableFacade,studyId,request);

        //tableFacade.setItems(items);

        HtmlRow row = (HtmlRow) tableFacade.getTable().getRow();
        HtmlColumn studySubjectStatus = row.getColumn("studySubjectStatus");
        studySubjectStatus.getFilterRenderer().setFilterEditor(new SubjectStatusFilter());


        //fix HTML in columns
        setHtmlCellEditors(tableFacade,allColumns,true);

        //temp disable some of the filters for now
        turnOffFilters(tableFacade,new String[]{"personId","secondaryId",
          "eventDate","enrollmentDate","studySubjectStatus","crfNameVersion","crfStatus",
          "lastUpdatedDate","lastUpdatedBy","sdvStatusActions"});


        //Create the custom toolbar
        SDVToolbar  sDVToolbar = new SDVToolbar();


        // if(totalRowCount > 0){
        sDVToolbar.setMaxRowsIncrements(new int[]{15,50,totalRowCount});
        tableFacade.setToolbar(sDVToolbar);

        //Fix column titles
        HtmlTable table = (HtmlTable) tableFacade.getTable();
        //i18n caption; TODO: convert to Spring messages
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
          "org.akaza.openclinica.i18n.words",request.getLocale());

        String[] allTitles = {"Study Subject Id","Person Id","Secondary Id" ,"Event Name",
          "Event Date","Enrollment Date","Subject Status","CRF Name / Version","CRF Status",
          "Last Updated Date",
          "Last Updated By","SDV Status / Actions"};

        setTitles(allTitles,table);

        //format column dates
        formatColumns(table,new String[]{"eventDate","enrollmentDate","lastUpdatedDate"},
          request);

        table.getTableRenderer().setWidth("800");
        return tableFacade.render();
    }

    private void turnOffFilters(TableFacade tableFacade, String[] colNames){

        HtmlRow row = (HtmlRow) tableFacade.getTable().getRow();
        HtmlColumn col = null;

        for(String colName : colNames) {
            col = row.getColumn(colName);
            col.setFilterable(false);
        }

    }
    public void setHtmlCellEditors(TableFacade tableFacade,
                                   String[] columnNames,boolean preventHtmlEscapes){

        HtmlRow row = ((HtmlTable)tableFacade.getTable()).getRow();
        HtmlColumn column = null;

        for(String col : columnNames) {

            column = row.getColumn(col);
            column.getCellRenderer().setCellEditor(this.getCellEditorNoEscapes());

        }

    }

    public void formatColumns(HtmlTable table,String[] columnNames,
                              HttpServletRequest request ) {

        Locale locale = ResourceBundleProvider.localeMap.get(Thread.currentThread());
        if(locale == null) {
            ResourceBundleProvider.updateLocale(request.getLocale());
        }
        ResourceBundle bundle = ResourceBundleProvider.getFormatBundle();
        String format = bundle.getString("date_time_format_string");
        HtmlRow row = table.getRow();
        HtmlColumn column = null;

        for(String colName : columnNames){
            column = row.getColumn(colName);
            if(column != null) {
                column.getCellRenderer().setCellEditor(new DateCellEditor(format));
            }
        }

    }

    /*
   Generate the rows for the study table. Each row represents an Event CRF.
    */
    public Collection<SubjectSDVContainer> getSubjectRows(
      List<EventCRFBean> eventCRFBeans,HttpServletRequest request)  {

        if(eventCRFBeans == null || eventCRFBeans.isEmpty()){
            return new ArrayList<SubjectSDVContainer>();
        }

        getEventNamesForEventCRFs(eventCRFBeans);

        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        SubjectDAO subjectDAO = new SubjectDAO(dataSource);
        StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);

        StudySubjectBean studySubjectBean = null;
        SubjectBean subjectBean = null;
        StudyEventBean studyEventBean = null;


        Collection<SubjectSDVContainer> allRows =
          new ArrayList<SubjectSDVContainer>();
        SubjectSDVContainer tempSDVBean = null;
        StringBuilder actions = new StringBuilder("");

        //Changed: The first row is the "select all" checkbox row
        //  tempSDVBean = new SubjectSDVContainer();
        // String firstRowActions = "Select All <input type=checkbox name='checkSDVAll' onclick='selectAllChecks(this.form)'/>";
        //  tempSDVBean.setSdvStatusActions(firstRowActions);
        //   allRows.add(tempSDVBean);


        for(EventCRFBean crfBean : eventCRFBeans) {

            tempSDVBean = new SubjectSDVContainer();


            studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(crfBean.getStudySubjectId());
            studyEventBean = (StudyEventBean) studyEventDAO.findByPK(crfBean.getStudyEventId());

            subjectBean = (SubjectBean) subjectDAO.findByPK(studySubjectBean.getSubjectId());

            tempSDVBean.setCrfNameVersion(getCRFName(crfBean.getCRFVersionId()) +
              "/ " + getCRFVersionName(crfBean.getCRFVersionId()));

            if(crfBean.getStatus() != null){
                tempSDVBean.setCrfStatus(getCRFStatusIconPath(studyEventBean.getSubjectEventStatus().getId()));
            }
            //TODO: I18N Date must be formatted properly
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sdformat = new SimpleDateFormat(pattern);

            if(studySubjectBean.getEnrollmentDate() != null){
                tempSDVBean.setEnrollmentDate(sdformat.format(
                  studySubjectBean.getEnrollmentDate()));
            } else {
                tempSDVBean.setEnrollmentDate("unknown");

            }
            //TODO: I18N Date must be formatted properly
            if(crfBean.getCreatedDate() != null){
                tempSDVBean.setEventDate(sdformat.format(
                  crfBean.getCreatedDate()));
            } else {
                tempSDVBean.setEventDate("unknown");

            }
            //crfBean.getEventName()
            tempSDVBean.setEventName(crfBean.getEventName());
            //The checkbox is next to the study subject id
            StringBuilder subjectIdContent = new StringBuilder("");
            //.getNexGenStatus().getCode() == 10
            //"This Event CRF has been Source Data Verified. If you uncheck this box, you are removing Source Data Verification for the Event CRF and you will have to repeat the process. Select OK to continue and Cancel to cancel this transaction."
            if(crfBean.isSdvStatus()){
                subjectIdContent.append("<a href='javascript:void(0)' onclick='prompt(document.sdvForm,");
                subjectIdContent.append(crfBean.getId());
                subjectIdContent.append(")'>");
                subjectIdContent.append( ICON_FORCRFSTATUS_PREFIX).append("DEcomplete").append(ICON_FORCRFSTATUS_SUFFIX).append("</a>");
            } else {
                subjectIdContent.append("<input style='margin-right: 5px' type='checkbox' ").append("class='sdvCheck'").append(" name='").append(CHECKBOX_NAME).append(crfBean.getId()).append("' />");

            }
            subjectIdContent.append(studySubjectBean.getLabel());
            tempSDVBean.setStudySubjectId(subjectIdContent.toString());

            if(subjectBean != null){
                tempSDVBean.setPersonId(subjectBean.getUniqueIdentifier());
            } else {
                tempSDVBean.setPersonId("");

            }
            //studySubjectBean.getSecondaryLabel()
            tempSDVBean.setSecondaryId(studySubjectBean.getSecondaryLabel());

            String statusName = studySubjectBean.getStatus().getName();
            int statusId = studySubjectBean.getStatus().getId();

            if(statusName != null) {
                tempSDVBean.setStudySubjectStatus(statusName);
            }

            //TODO: I18N Date must be formatted properly
            if(crfBean.getUpdatedDate() != null){
                tempSDVBean.setLastUpdatedDate(sdformat.format(
                  crfBean.getUpdatedDate()));
            } else {
                tempSDVBean.setLastUpdatedDate("unknown");

            }

            if(crfBean.getUpdater() != null){

                tempSDVBean.setLastUpdatedBy(crfBean.getUpdater().getFirstName()+" "+crfBean.getUpdater().getLastName());

            }

            actions = new StringBuilder("");
            //append("<input type='hidden' name='crfId' value='").append(crfBean.getId()).append("'").append("/> ")
            if(! crfBean.isSdvStatus()) {
                StringBuilder jsCodeString = new StringBuilder("this.form.method='GET'; this.form.action='").append(request.getContextPath()).append("/pages/handleSDVGet").
                  append("';").append("this.form.crfId.value='").append(crfBean.getId()).append("';").append("this.form.submit();");

                actions.append("<input type=\"submit\" class=\"button_medium\" value=\"SDV\" name=\"sdvSubmit\" ").append("onclick=\"").append(jsCodeString.toString()).append("\" />");
            }
            tempSDVBean.setSdvStatusActions(actions.toString());
            allRows.add(tempSDVBean);


        }

        return allRows;
    }

    private String getCRFStatusIconPath(int statusId) {

        StringBuilder builder = new StringBuilder( ICON_FORCRFSTATUS_PREFIX);
        String imgName = "";

        if(statusId > 0 && statusId < 8){

            builder.append(SUBJECT_EVENT_STATUS_ICONS.get(statusId));
        } else {
            builder.append(SUBJECT_EVENT_STATUS_ICONS.get(0));

        }
        builder.append(ICON_FORCRFSTATUS_SUFFIX);
        return builder.toString();
    }

    public List<Integer> getListOfSdvEventCRFIds(Collection<String> paramsContainingIds) {

        List<Integer> eventCRFWithSDV = new ArrayList<Integer>();
        if(paramsContainingIds == null || paramsContainingIds.isEmpty()){
            return eventCRFWithSDV;
        }
        int tmpInt;
        for(String param : paramsContainingIds) {
            tmpInt = stripPrefixFromParam(param);
            if(tmpInt != 0) {
                eventCRFWithSDV.add(tmpInt);
            }
        }

        return eventCRFWithSDV;
    }

    private int stripPrefixFromParam(String param) {
        if(param != null && param.contains(CHECKBOX_NAME)) {
            return Integer.parseInt(param.substring(param.indexOf("_")+1));
        } else {
            return 0;
        }
    }

    /* public static void main(String[] args) {
        SDVUtil util = new SDVUtil();
        System.out.println(util.stripPrefixFromParam(CHECKBOX_NAME+533)+"");
    }*/

    public Collection<SubjectSDVContainer> getSubjectAggregateRows(
      List<StudySubjectBean> studySubjectBeans)  {

        if(studySubjectBeans == null || studySubjectBeans.isEmpty()){
            return new ArrayList<SubjectSDVContainer>();
        }
        Collection<SubjectSDVContainer> allRows =
          new ArrayList<SubjectSDVContainer>();
        SubjectSDVContainer tempSDVBean = null;

        //The first row is the "select all" checkbox row
        tempSDVBean = new SubjectSDVContainer();
        String firstRowActions = "Select All <input type=checkbox name='checkAll' onclick='selectAllChecks(this.form)'/>";
        tempSDVBean.setSdvStatusActions(firstRowActions);
        allRows.add(tempSDVBean);
        StringBuilder actions;


        for(StudySubjectBean studySubjectBean : studySubjectBeans) {
            tempSDVBean = new SubjectSDVContainer();
            tempSDVBean.setStudySubjectId(studySubjectBean.getId()+"");
            //studySubjectBean.getStatus().getName() : TODO: fix ResourceBundle problem
            tempSDVBean.setStudySubjectStatus("subject status");
            tempSDVBean.setNumberOfCRFsSDV("0");
            tempSDVBean.setPercentageOfCRFsSDV("0");
            tempSDVBean.setGroup("group");
            actions = new StringBuilder("<input class='sdvCheckbox' type='checkbox' name=");
            actions.append("'sdvCheck").append(studySubjectBean.getId()).append("'/>&nbsp;&nbsp");
            actions.append(VIEW_ICON_FORSUBJECT_PREFIX).append(studySubjectBean.getId()).
              append(VIEW_ICON_FORSUBJECT_SUFFIX );
            tempSDVBean.setSdvStatusActions(actions.toString());

            allRows.add(tempSDVBean);


        }

        return allRows;

    }

    public void getEventNamesForEventCRFs(List<EventCRFBean> eventCRFBeans){
        if(eventCRFBeans == null || eventCRFBeans.isEmpty()) return;

        StudyEventDAO  studyEventDAO = new StudyEventDAO(dataSource);
        StudyEventDefinitionDAO  studyEventDefinitionDAO = new StudyEventDefinitionDAO(dataSource);

        StudyEventBean studyEventBean = null;
        StudyEventDefinitionBean studyEventDefBean = null;
        //Provide a value for the eventName property of the EventCRF
        for(EventCRFBean eventCRFBean : eventCRFBeans){
            if("".equalsIgnoreCase(eventCRFBean.getEventName())){
                studyEventBean = (StudyEventBean) studyEventDAO.findByPK(eventCRFBean.getStudyEventId());
                studyEventDefBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(
                  studyEventBean.getStudyEventDefinitionId());
                eventCRFBean.setEventName(studyEventDefBean.getName());
            }
        }

    }

    /* Create the titles for the HTML table's rows */
    private void setTitles(String[] allTitles,HtmlTable table ){
        HtmlRow row = table.getRow();
        HtmlColumn tempColumn = null;

        for(int i = 0; i < allTitles.length; i++) {
            tempColumn = row.getColumn(i);
            tempColumn.setTitle(allTitles[i]);
        }



    }

    public String getCRFName(int crfVersionId){
        CRFVersionDAO cRFVersionDAO = new CRFVersionDAO(dataSource);
        CRFDAO cRFDAO = new CRFDAO(dataSource);

        CRFVersionBean versionBean =
          (CRFVersionBean) cRFVersionDAO.findByPK(crfVersionId);
        if(versionBean != null){
            CRFBean crfBean = (CRFBean) cRFDAO.findByPK(versionBean.getCrfId());
            if(crfBean != null) return crfBean.getName();
        }

        return "";
    }

    public String getCRFVersionName(int crfVersionId){

        CRFVersionDAO cRFVersionDAO = new CRFVersionDAO(dataSource);
        CRFVersionBean versionBean =
          (CRFVersionBean) cRFVersionDAO.findByPK(crfVersionId);
        if(versionBean != null){
            return versionBean.getName();
        }

        return "";

    }

    public List<EventCRFBean> getAllEventCRFs(List<StudyEventBean> studyEventBeans){

        List<EventCRFBean> eventCRFBeans = new ArrayList<EventCRFBean>();
        List<EventCRFBean> studyEventCRFBeans = new ArrayList<EventCRFBean>();


        EventCRFDAO eventCRFDAO = new EventCRFDAO(dataSource);

        for(StudyEventBean studyEventBean : studyEventBeans){
            eventCRFBeans = eventCRFDAO.findAllByStudyEvent(studyEventBean);
            if(eventCRFBeans != null && ! eventCRFBeans.isEmpty()){
                studyEventCRFBeans.addAll(eventCRFBeans);
            }
        }

        return studyEventCRFBeans;
    }

    public String renderSubjectsAggregateTable( int studyId,
                                                HttpServletRequest request) {

        List<StudySubjectBean> studySubjectBeans = new ArrayList<StudySubjectBean>();
        StudySubjectDAO studySubjectDAO = new StudySubjectDAO(dataSource);
        studySubjectBeans = studySubjectDAO.findAllByStudyId(studyId);

        Collection<SubjectSDVContainer> items = getSubjectAggregateRows(studySubjectBeans);

        //The number of items represents the total number of returned rows
        int totalRowCount =0;
        if(items != null && items.size() > 0) {
            totalRowCount = items.size();
        }

        TableFacade tableFacade = createTableFacade("sdv", request);
        //The default display for the JMesa Limit select widget is 1,50,100 rows
        //We'll change this if the subject has more than one row, and have the last choice
        //set to the total row count
        if(totalRowCount > 1){
            tableFacade.setMaxRowsIncrements(15,50,totalRowCount);
        }
        tableFacade.setColumnProperties("studySubjectId","studySubjectStatus","numberOfCRFsSDV",
          "percentageOfCRFsSDV",
          "group","sdvStatusActions");

        tableFacade.setItems(items);
        //Fix column titles
        HtmlTable table = (HtmlTable) tableFacade.getTable();
        //i18n caption; TODO: convert to Spring messages
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
          "org.akaza.openclinica.i18n.words",request.getLocale());

        String[] allTitles = {"Study Subject Id","Study Subject Status","# Of CRFs SDV'd","% Of CRFs SDV'd","Group"};

        setTitles(allTitles,table);

        table.getTableRenderer().setWidth("800");
        return tableFacade.render();

    }

    public boolean setSDVerified(List<Integer> eventCRFIds, boolean setVerification){

        //If no event CRFs are offered to SDV, then the transaction has not
        //caused a problem, so return true
        if(eventCRFIds == null || eventCRFIds.isEmpty()) {
            return true;
        }

        EventCRFDAO  eventCRFDAO = new EventCRFDAO(dataSource);

        for(Integer eventCrfId : eventCRFIds){
            try{
                eventCRFDAO.setSDVStatus(setVerification,eventCrfId);
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
                return false;
            }
        }

        return true;
    }

    public void forwardRequestFromController(HttpServletRequest request, HttpServletResponse response,
                                             String path){
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareSDVSelectElements(HttpServletRequest request, StudyBean studyBean){
        //Study event statuses
        List<String> studyEventStatuses = new ArrayList<String>();
        for(org.akaza.openclinica.domain.Status stat : org.akaza.openclinica.domain.Status.values()){
            studyEventStatuses.add(stat.getDescription());
        }

        request.setAttribute("studyEventStatuses",studyEventStatuses);

        //SDV requirements
        List<String> sdvRequirements = new ArrayList<String>();
        for(SourceDataVerification sdvRequire : SourceDataVerification.values()){
            sdvRequirements.add(sdvRequire.getDescription());
        }

        request.setAttribute("sdvRequirements",SourceDataVerification.values());

        //study event definitions
        StudyEventDefinitionDAO studyEventDefinitionDAO =
          new StudyEventDefinitionDAO(dataSource);

        List<StudyEventDefinitionBean> studyEventDefinitionBeans =
          new ArrayList<StudyEventDefinitionBean>();

        studyEventDefinitionBeans = studyEventDefinitionDAO.findAllByStudy(studyBean);
        request.setAttribute("studyEventDefinitions",studyEventDefinitionBeans);

        //study event status
        request.setAttribute("studyEventStatuses",Status.toArrayList());


        //event CRF status
        request.setAttribute("eventCRFDStatuses",SubjectEventStatus.toArrayList());

        StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);

        List<StudyEventBean> studyEventBeans = studyEventDAO.findAllByStudy(studyBean);
        List<EventCRFBean> eventCRFBeans = getAllEventCRFs(studyEventBeans);
        SortedSet<String> eventCRFNames = new TreeSet<String>();

        for(EventCRFBean bean : eventCRFBeans){
            eventCRFNames.add(getCRFName(bean.getCRFVersionId()));
        }
        request.setAttribute("eventCRFNames",eventCRFNames);



    }

    public List<EventCRFBean> filterEventCRFs(List<EventCRFBean> eventCRFBeans,
                                              BindingResult bindingResult) {

        /* study_subject_id=Subject+D&eventCRF=0&studyEventDefinition=0&
        studyEventStatus=-1&eventCRFStatus=-1&eventcrfSDVStatus=None&
        sdvRequirement=0&startUpdatedDate=&endDate=&submit=Apply+Filter*/
        List<EventCRFBean> newList = new ArrayList<EventCRFBean>();

        if(eventCRFBeans == null || eventCRFBeans.isEmpty() || bindingResult == null) {
            return eventCRFBeans;
        }

        SdvFilterDataBean filterBean = (SdvFilterDataBean) bindingResult.getTarget();
        StudySubjectBean studySubjectBean = null;
        StudyEventBean studyEventBean = null;
        StudySubjectDAO  studySubjectDAO = new StudySubjectDAO(dataSource);
        StudyEventDAO studyEventDAO = new StudyEventDAO(dataSource);
        boolean studySub = true,studyEventDef = true,studyEventStatus = true,
          eventCRFStatusBool = true, eventcrfSDVStatus = true,
          eventCRFNameBool = true,upDatedDateBool = true,sdvRequirementBool = true;

        for (EventCRFBean eventCBean : eventCRFBeans){
            //filter study subject
            if( filterBean.getStudy_subject_id().length() > 0) {
                studySubjectBean = (StudySubjectBean) studySubjectDAO.findByPK(eventCBean.getStudySubjectId());

                studySub = (filterBean.getStudy_subject_id().equalsIgnoreCase(studySubjectBean.getLabel()));

            }

            //filter study event definition

            if( filterBean.getStudyEventDefinition() > 0) {
                studyEventBean =  (StudyEventBean) studyEventDAO.findByPK(eventCBean.getStudyEventId());

                studyEventDef = (filterBean.getStudyEventDefinition() == studyEventBean.getStudyEventDefinitionId());

            }

            //Event CRF status
            if( filterBean.getStudyEventStatus() > 0) {

                studyEventStatus = (filterBean.getStudyEventStatus() == eventCBean.getStatus().getId());
            }

            //Event CRF subject event status
            if( filterBean.getEventCRFStatus() > 0) {
                studyEventBean =  (StudyEventBean) studyEventDAO.findByPK(eventCBean.getStudyEventId());

                eventCRFStatusBool = (filterBean.getEventCRFStatus() == studyEventBean.getSubjectEventStatus().getId());
            }

            //Event CRF SDV status; true or false
            if(! filterBean.getEventcrfSDVStatus().equalsIgnoreCase("N/A")) {
                boolean sdvBool = filterBean.getEventcrfSDVStatus().equalsIgnoreCase("complete");
                eventcrfSDVStatus = (eventCBean.isSdvStatus() == sdvBool);
            }

            //Event CRF name match
            if(filterBean.getEventCRFName().length() > 0) {
                String tmpName = getCRFName(eventCBean.getCRFVersionId());
                eventCRFNameBool = (tmpName.equalsIgnoreCase(filterBean.getEventCRFName()));
            }

            //TODO: Event CRF SDV requirement, when the application provides a way
            //TODO: of setting this requirement in the event definition

            //event CRF updated date
            if(eventCBean.getUpdatedDate() != null && filterBean.getStartUpdatedDate() != null &&
              filterBean.getEndDate()  != null){

                GregorianCalendar calStart = new GregorianCalendar();
                calStart.setTime(filterBean.getStartUpdatedDate());

                GregorianCalendar calendarEnd = new GregorianCalendar();
                calendarEnd.setTime(filterBean.getEndDate());

                GregorianCalendar calendarNow = new GregorianCalendar();
                calendarNow.setTime(eventCBean.getUpdatedDate());

                upDatedDateBool = ((calendarNow.after(calStart) && calendarNow.before(calendarEnd)) ||
                  (calendarNow.equals(calStart) || calendarNow.equals(calendarEnd)));

            }

            if(upDatedDateBool && eventCRFNameBool && eventcrfSDVStatus &&
              eventCRFStatusBool && studyEventStatus && studyEventDef &&
              studySub)  {
                newList.add(eventCBean);
            }
        }
        return newList;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    class NoEscapeHtmlCellEditor extends HtmlCellEditor {

        public Object getValue(Object item, String property, int rowcount) {
            return ItemUtils.getItemValue(item, property);
        }
    }

}