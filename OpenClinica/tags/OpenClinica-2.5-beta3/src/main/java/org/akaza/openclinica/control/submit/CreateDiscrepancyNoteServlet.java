/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormDiscrepancyNotes;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Create a discrepancy note for a data entity
 *
 * @author jxu
 *
 */
public class CreateDiscrepancyNoteServlet extends SecureController {

    Locale locale;
    // < ResourceBundleresexception,respage;

    public static final String DIS_TYPES = "discrepancyTypes";

    public static final String RES_STATUSES = "resolutionStatuses";

    public static final String ENTITY_ID = "id";
    
    public static final String SUBJECT_ID = "subjectId";
    
    public static final String ITEM_ID = "itemId";
    
    public static final String PARENT_ID = "parentId";// parent note id

    public static final String ENTITY_TYPE = "name";

    public static final String ENTITY_COLUMN = "column";

    public static final String ENTITY_FIELD = "field";

    public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

    public static final String DIS_NOTE = "discrepancyNote";

    public static final String WRITE_TO_DB = "writeToDB";

    public static final String PRESET_RES_STATUS = "strResStatus";
    
    public static final String CAN_MONITOR = "canMonitor";
    
    public static final String NEW_NOTE = "new";
    
    public static final String RES_STATUS_ID = "resStatusId";
    
    public static final String ERROR_FLAG ="errorFlag";// use to determine discrepany note type, whether a note's item has validation error or not
    
    
  
    /*
     * (non-Javadoc)
     *
     * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
     */
    @Override
    protected void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // <
        // resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);

        String exceptionName = resexception.getString("no_permission_to_create_discrepancy_note");
        String noAccessMessage = respage.getString("you_may_not_create_discrepancy_note") + respage.getString("change_study_contact_sysadmin");

        if (SubmitDataServlet.mayViewData(ub, currentRole)) {
            return;
        }
       
        addPageMessage(noAccessMessage);
        throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
    }

    @Override
    protected void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());
        ArrayList types = DiscrepancyNoteType.toArrayList();
        
        
        request.setAttribute(DIS_TYPES, types);
        request.setAttribute(RES_STATUSES, ResolutionStatus.toArrayList());
        //types.remove(DiscrepancyNoteType.ANNOTATION);//this for legancy data only, not for new notes
        
        /*ArrayList ress = ResolutionStatus.toArrayList();
        for ( int i=0; i<ress.size(); i++) {
            ResolutionStatus s= (ResolutionStatus)ress.get(i); 
            System.out.println("status:" + s.getName()) ;
            
        }*/

        boolean writeToDB = fp.getBoolean(WRITE_TO_DB, true);
        int entityId = fp.getInt(ENTITY_ID);
        int subjectId = fp.getInt(SUBJECT_ID);
        int itemId = fp.getInt(ITEM_ID);
        String entityType = fp.getString(ENTITY_TYPE);
        String field = fp.getString(ENTITY_FIELD);
        String column = fp.getString(ENTITY_COLUMN);
        int parentId = fp.getInt(PARENT_ID);
        
        boolean isInError = fp.getBoolean(ERROR_FLAG);        
        
        
        boolean isNew = fp.getBoolean(NEW_NOTE);
        request.setAttribute(NEW_NOTE, isNew? "1": "0");
        
        String strResStatus = fp.getString(PRESET_RES_STATUS);
        if (!strResStatus.equals("")) {
            request.setAttribute(PRESET_RES_STATUS, strResStatus);
        }

        String monitor = fp.getString("monitor");       
        String enterData = fp.getString("enterData");
        request.setAttribute("enterData", enterData);
        
        
        boolean enteringData = false;
        if (enterData != null && "1".equalsIgnoreCase(enterData)){
            //variables are not set in JSP, so not from viewing data and from entering data
            request.setAttribute(CAN_MONITOR, "1");  
            request.setAttribute("monitor", monitor);            
            
            enteringData = true;
            
        //} else if ("1".equalsIgnoreCase(monitor) && "1".equalsIgnoreCase(blank)){
        } else if ("1".equalsIgnoreCase(monitor)){// change to allow user to enter note for all items, not just blank items
            
            request.setAttribute(CAN_MONITOR, "1");  
            request.setAttribute("monitor", monitor);                
            
        } else {
            request.setAttribute(CAN_MONITOR, "0");            
            
        }
        
        if( "itemData".equalsIgnoreCase(entityType) && enteringData) {
            request.setAttribute("enterItemData", "yes");
        }
        
        //finds all the related notes
        ArrayList notes = (ArrayList) dndao.findAllByEntityAndColumn(entityType, entityId, column);
        
        DiscrepancyNoteBean parent = new DiscrepancyNoteBean();
        if (parentId > 0) {            
            dndao.setFetchMapping(true);
            parent = (DiscrepancyNoteBean) dndao.findByPK(parentId);
            if (parent.isActive()) {
                request.setAttribute("parent", parent);
            }
            dndao.setFetchMapping(false);
        } else {
            if (!isNew) {//not a new note, so try to find the parent note
                for (int i = 0; i < notes.size(); i++) {
                    DiscrepancyNoteBean note1 = (DiscrepancyNoteBean) notes.get(i);
                    if (note1.getParentDnId() == 0) {
                        parent = note1;
                        parent.setEntityId(note1.getEntityId());
                        parent.setColumn(note1.getColumn());
                        parent.setId(note1.getId());
                        request.setAttribute("parent", note1);
                        break;
                    }
                }
            }
        }
        FormDiscrepancyNotes newNotes = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);

        if (newNotes == null) {
            newNotes = new FormDiscrepancyNotes();
        }

        if (!notes.isEmpty() || !newNotes.getNotes(field).isEmpty()) {
            request.setAttribute("hasNotes", "yes");
            //request.setAttribute("parent", parent);
        } else {
            request.setAttribute("hasNotes", "no");
            logger.info("has notes:" + "no");
        }
        
        if (!fp.isSubmitted()) {
            DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();

            if (subjectId>0) {
                StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
                StudySubjectBean ssub = (StudySubjectBean)ssdao.findByPK(subjectId);
                dnb.setSubjectName(ssub.getName());
                dnb.setSubjectId(ssub.getId());
            }  
            if (itemId>0){
              ItemDAO idao = new ItemDAO(sm.getDataSource());            
              ItemBean item = (ItemBean) idao.findByPK(itemId);
              dnb.setEntityName(item.getName()); 
              request.setAttribute("item", item);
            }
            dnb.setEntityType(entityType);
            dnb.setColumn(column);
            dnb.setEntityId(entityId);
            dnb.setField(field);
            dnb.setParentDnId(parent.getId());
            dnb.setCreatedDate(new Date());
            
            //When a user is performing Data Entry, Initial Data Entry or Double Data Entry and 
            //have not received any validation warnings or messages for a particular item, 
            //they will see Annotation as the default type in the Add Discrepancy note window.

            //When a user is performing Data Entry, Initial Data Entry or Double Data Entry and they 
            //have received a validation warning or message for a particular item, 
            //they can click on the flag and the default type should be Failed Validation Check

            //When a user is viewing a CRF and they click on the flag icon, the default type should be query.
            
            if (parent.getId()==0 || isNew) {//no parent, new note thread
              if (enteringData) {
                  if (isInError){
                     dnb.setDiscrepancyNoteTypeId((DiscrepancyNoteType.FAILEDVAL).getId());
                    
                  } else {
                     dnb.setDiscrepancyNoteTypeId((DiscrepancyNoteType.ANNOTATION).getId());  
                     dnb.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
                    
                  }
              } else {
                  dnb.setDiscrepancyNoteTypeId((DiscrepancyNoteType.QUERY).getId());  
                 
              }
              
                
                
            }
            
            else if (parent.getDiscrepancyNoteTypeId()>0) {
              dnb.setDiscrepancyNoteTypeId(parent.getDiscrepancyNoteTypeId());
              dnb.setResolutionStatusId(ResolutionStatus.UPDATED.getId());
            }       

            getNoteInfo(dnb);// populate note infos
            request.setAttribute(DIS_NOTE, dnb);
            request.setAttribute(WRITE_TO_DB, writeToDB ? "1" : "0");            
           
           
            forwardPage(Page.ADD_DISCREPANCY_NOTE);

        } else {
            logger.info("submitted ************");

            FormDiscrepancyNotes noteTree = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);

            if (noteTree == null) {
                noteTree = new FormDiscrepancyNotes();
                logger.info("No note tree initailized in session");
            }

            Validator v = new Validator(request);
            String description = fp.getString("description");
            int typeId = fp.getInt("typeId");
            int resStatusId = fp.getInt(RES_STATUS_ID);
            String detailedDes = fp.getString("detailedDes");
            DiscrepancyNoteBean note = new DiscrepancyNoteBean();
            v.addValidation("description", Validator.NO_BLANKS);           
            v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);
            v.addValidation("detailedDes", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 1000);

            v.addValidation("typeId", Validator.NO_BLANKS);
            
            HashMap errors = v.validate();
            note.setDescription(description);
            note.setDetailedNotes(detailedDes);
            note.setOwner(ub);
            note.setCreatedDate(new Date());
            note.setResolutionStatusId(resStatusId);
            note.setDiscrepancyNoteTypeId(typeId);
            note.setParentDnId(parent.getId());
            note.setField(field);
            if(DiscrepancyNoteType.ANNOTATION.getId()== note.getDiscrepancyNoteTypeId()){
                updateStudyEvent(entityType, entityId);
            }
            if ((DiscrepancyNoteType.ANNOTATION.getId()== note.getDiscrepancyNoteTypeId()) ||
               ( DiscrepancyNoteType.REASON_FOR_CHANGE.getId() ==  note.getDiscrepancyNoteTypeId())) {
                 note.setResStatus(ResolutionStatus.NOT_APPLICABLE); 
                 note.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
            }
            if (DiscrepancyNoteType.FAILEDVAL.getId() == note.getDiscrepancyNoteTypeId() ||
                    DiscrepancyNoteType.QUERY.getId() == note.getDiscrepancyNoteTypeId()) {
               if (ResolutionStatus.NOT_APPLICABLE.getId()== note.getResolutionStatusId()) {
                  Validator.addError(errors, RES_STATUS_ID, restext.getString("not_valid_res_status"));
               } 
            }

            if (!parent.isActive()) {
                //System.out.println("note entity id:" + entityId);
                note.setEntityId(entityId);
                note.setEntityType(entityType);
                note.setColumn(column);
            } else {
                //System.out.println("parent entity id:" + parent.getEntityId());
                note.setEntityId(parent.getEntityId());
                note.setEntityType(parent.getEntityType());
                if (!StringUtil.isBlank(parent.getColumn())){
                    note.setColumn(parent.getColumn());
                } else {
                    note.setColumn(column); 
                }
                note.setParentDnId(parent.getId());
            }

            note.setStudyId(currentStudy.getId());

            note = getNoteInfo(note);// populate note infos

            request.setAttribute(DIS_NOTE, note);
            request.setAttribute(WRITE_TO_DB, writeToDB ? "1" : "0");           

            if (errors.isEmpty()) {

                if (!writeToDB) {
                    logger.info("save note into session");                   
                    noteTree.addNote(field, note);
                    noteTree.addIdNote(note.getEntityId(),field);
                    session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, noteTree);
//                  
                    forwardPage(Page.ADD_DISCREPANCY_NOTE_DONE);
                } else {
                    //if not creating a new thread(note), update exsiting notes if necessary           
                    if ("itemData".equalsIgnoreCase(entityType) && !isNew) {                
                      System.out.println("Create:find parent note for item data:" + note.getEntityId());
                     
                      DiscrepancyNoteBean pNote = (DiscrepancyNoteBean)dndao.findByPK(note.getParentDnId());
                      
                      if (note.getDiscrepancyNoteTypeId() == pNote. getDiscrepancyNoteTypeId()) {
                          
                          if (note.getResolutionStatusId()>pNote.getResolutionStatusId()) {                          
                            pNote.setResolutionStatusId(note.getResolutionStatusId());
                            dndao.update(pNote);
                          }
                      }  
                      
                    } 
                    if (note.getEntityId() ==0) {
                        //addPageMessage("Your discrepancy note for the item cannot be saved because there is no item data for this CRF in the database yet.");
                        addPageMessage(respage.getString("note_cannot_be_saved"));
                        forwardPage(Page.ADD_DISCREPANCY_NOTE_SAVE_DONE);
                        return;
                    }
                    note = (DiscrepancyNoteBean) dndao.create(note);  
                   
                    dndao.createMapping(note);
                    
                    request.setAttribute(DIS_NOTE, note);
                    
                    if (note.getParentDnId() ==0) {
                     //see issue 2659 this is a new thread, we will create two notes in this case, 
                     //This way one can be the parent that updates as the status changes, but one also stays as New. 
                        note.setParentDnId(note.getId());
                        note = (DiscrepancyNoteBean) dndao.create(note);                         
                        dndao.createMapping(note);  
                    }
                    
                   
                    
//                  addPageMessage("Your discrepancy note has been saved into database.");
                    addPageMessage(respage.getString("note_saved_into_db"));
                    forwardPage(Page.ADD_DISCREPANCY_NOTE_SAVE_DONE);
                }

                
            } else {
                setInputMessages(errors);
                forwardPage(Page.ADD_DISCREPANCY_NOTE);
            }

        }

    }

    /**
     * Constructs a url for creating new note on 'view note list' page
     *
     * @param note
     * @param preset
     * @return
     */
    public static String getAddChildURL(DiscrepancyNoteBean note, ResolutionStatus preset, boolean toView) {
        ArrayList arguments = new ArrayList();

        arguments.add(ENTITY_TYPE + "=" + note.getEntityType());
        arguments.add(ENTITY_ID + "=" + note.getEntityId());                
        arguments.add(WRITE_TO_DB + "=" + "1");
        arguments.add("monitor" + "=" + 1);// of course, when resolving a note, we have monitor privilege

        if (preset.isActive()) {
            arguments.add(PRESET_RES_STATUS + "=" + String.valueOf(preset.getId()));
        }       
        
        if (toView) {
            arguments.add(ENTITY_COLUMN + "=" + "value");
            arguments.add(SUBJECT_ID + "=" + note.getSubjectId());
            arguments.add(ITEM_ID + "=" + note.getItemId());
            String queryString = StringUtil.join("&", arguments);
         return "ViewDiscrepancyNote?" + queryString;  
        } else {
            arguments.add(PARENT_ID + "=" + note.getId());
            String queryString = StringUtil.join("&", arguments);
        return "CreateDiscrepancyNote?" + queryString;
        }
    }

    /**
     * Pulls the note related information from database according to note type
     *
     * @param note
     */
    private DiscrepancyNoteBean getNoteInfo(DiscrepancyNoteBean note) {
        StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
        if ("itemData".equalsIgnoreCase(note.getEntityType())) {
            int itemDataId = note.getEntityId();
            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            ItemDataBean itemData = (ItemDataBean) iddao.findByPK(itemDataId);
            ItemDAO idao = new ItemDAO(sm.getDataSource());
            if (StringUtil.isBlank(note.getEntityName())) {
              ItemBean item = (ItemBean) idao.findByPK(itemData.getItemId());
              note.setEntityName(item.getName());
              request.setAttribute("item", item);
            }
            EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
            StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());

            EventCRFBean ec = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());
            StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());

            StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event.getStudyEventDefinitionId());
            note.setEventName(sed.getName());
            note.setEventStart(event.getDateStarted());

            CRFDAO cdao = new CRFDAO(sm.getDataSource());
            CRFBean crf = cdao.findByVersionId(ec.getCRFVersionId());
            note.setCrfName(crf.getName());
           
            if (StringUtil.isBlank(note.getSubjectName())){
                StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());
                note.setSubjectName(ss.getName());
            }
            
            if (note.getDiscrepancyNoteTypeId()==0){
                note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.FAILEDVAL.getId());//default value
             }

        } else if ("eventCrf".equalsIgnoreCase(note.getEntityType())) {
            int eventCRFId = note.getEntityId();
            EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
            StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());

            EventCRFBean ec = (EventCRFBean) ecdao.findByPK(eventCRFId);
            StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());

            StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event.getStudyEventDefinitionId());
            note.setEventName(sed.getName());
            note.setEventStart(event.getDateStarted());

            CRFDAO cdao = new CRFDAO(sm.getDataSource());
            CRFBean crf = cdao.findByVersionId(ec.getCRFVersionId());
            note.setCrfName(crf.getName());
            StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(ec.getStudySubjectId());
            note.setSubjectName(ss.getName());

        } else if ("studyEvent".equalsIgnoreCase(note.getEntityType())) {
            int eventId = note.getEntityId();
            StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());
            StudyEventBean event = (StudyEventBean) svdao.findByPK(eventId);

            StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
            StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(event.getStudyEventDefinitionId());
            note.setEventName(sed.getName());
            note.setEventStart(event.getDateStarted());

            StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(event.getStudySubjectId());
            note.setSubjectName(ss.getName());

        } else if ("studySub".equalsIgnoreCase(note.getEntityType())) {
            int studySubjectId = note.getEntityId();
            StudySubjectBean ss = (StudySubjectBean) ssdao.findByPK(studySubjectId);
            note.setSubjectName(ss.getName());

        } else if ("Subject".equalsIgnoreCase(note.getEntityType())) {
            int subjectId = note.getEntityId();
            StudySubjectBean ss = ssdao.findBySubjectIdAndStudy(subjectId, currentStudy);
            note.setSubjectName(ss.getName());
        }
        
        return note;
    }

    private void updateStudyEvent(String entityType, int entityId){
        if ("itemData".equalsIgnoreCase(entityType)) {
            int itemDataId = entityId;
            ItemDataDAO iddao = new ItemDataDAO(sm.getDataSource());
            ItemDataBean itemData = (ItemDataBean) iddao.findByPK(itemDataId);
            EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
            StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());
            EventCRFBean ec = (EventCRFBean) ecdao.findByPK(itemData.getEventCRFId());
            StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());
                if(event.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)){
                    event.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
                    event.setUpdater(ub);
                    event.setUpdatedDate(new Date());
                    svdao.update(event);
                }
        } else if ("eventCrf".equalsIgnoreCase(entityType)) {
            int eventCRFId = entityId;
            EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
            StudyEventDAO svdao = new StudyEventDAO(sm.getDataSource());

            EventCRFBean ec = (EventCRFBean) ecdao.findByPK(eventCRFId);
            StudyEventBean event = (StudyEventBean) svdao.findByPK(ec.getStudyEventId());
            if(event.getSubjectEventStatus().equals(SubjectEventStatus.SIGNED)){
                event.setSubjectEventStatus(SubjectEventStatus.COMPLETED);
                event.setUpdater(ub);
                event.setUpdatedDate(new Date());
                svdao.update(event);
            }
        }
    }
}
