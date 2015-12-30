/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.dao.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;

import org.xml.sax.SAXException;

/**
 * Provides a singleton SQLFactory instance
 * 
 * @author thickerson
 * @author Jun Xu
 *  
 */
public class SQLFactory {

  // DAO KEYS TO USE FOR RETRIEVING DIGESTER
  public final String DAO_USERACCOUNT = "useraccount";
  public final String DAO_STUDY = "study";
  public final String DAO_STUDYEVENTDEFNITION = "studyeventdefintion";
  public final String DAO_SUBJECT = "subject";
  public final String DAO_STUDYSUBJECT = "study_subject";
  public final String DAO_STUDYGROUP = "study_group";
  public final String DAO_STUDYGROUPCLASS = "study_group_class";
  public final String DAO_SUBJECTGROUPMAP = "subject_group_map";
  public final String DAO_STUDYEVENT = "study_event";
  public final String DAO_EVENTDEFINITIONCRF = "event_definition_crf";
  public final String DAO_AUDITEVENT = "audit_event";
  public final String DAO_AUDIT = "audit";
  public final String DAO_DATAVIEW = "dataview_dao";
  public final String DAO_ITEM = "item";
  public final String DAO_ITEMDATA = "item_data";
  public final String DAO_ITEMFORMMETADATA = "item_form_metadata";
  public final String DAO_CRF = "crf";
  public final String DAO_CRFVERSION = "crfversion";
  public final String DAO_DATASET = "dataset";
  public final String DAO_SECTION = "section";
  public final String DAO_MASKING = "masking";
  public final String DAO_FILTER = "filter";

  public final String DAO_EVENTCRF = "eventcrf";
  
  public final String DAO_ARCHIVED_DATASET_FILE = "archived_dataset_file";
  
  public final String DAO_DISCREPANCY_NOTE = "discrepancy_note";
 
  public final String DAO_STUDY_PARAMETER="study_parameter";
  public final String DAO_ITEM_GROUP="item_group";
  public final String DAO_ITEM_GROUP_METADATA="item_group_metadata";
  
  public static String JUNIT_XML_DIR="C:\\work\\eclipse\\workspace\\OpenClinica" + File.separator
  + "webapp" + File.separator +  
  "properties" + File.separator;
  
  public static void setXMLDir(String path) {
    JUNIT_XML_DIR = path;
  }
  
  private static Hashtable digesters = new Hashtable();
 
      

  private String dbName = "";

  /**
   * A handle to the unique SQLFactory instance.
   */
  static private SQLFactory facInstance = null;

  /**
   * @return The unique instance of this class.
   * <b>WARNING this directory will need to be changed to run unit tests on 
   * other systems!!!</b>
   */
  static public SQLFactory getInstance() {
  	//set so that we could test an xml file in a unit test, tbh
    if (facInstance == null) {
    	
    	facInstance = new SQLFactory();
    }
    return facInstance;
  }

  // name should be one of the public static final Strings above
  public void addDigester(String name, DAODigester dig) {
    digesters.put(name, dig);
  }

  // name should be one of the public static final Strings above
  public DAODigester getDigester(String name) {
    return (DAODigester) digesters.get(name);
  }
  
  public void run(String dbName) {
    //we get the type of the database and run the factory, picking
    //up all the queries. NOTE that this should only be run
    //during the init servlets' action, and then it will
    //remain in static memory. tbh 9/8/04

    
    // ssachs 20041011
    // modified this section so that files are added using the
    // public static final strings above which are not specific to the database
    
    // key is the public static final sting used above; value is the actual filename
    HashMap fileList = new HashMap();

    if ("oracle".equals(dbName)) {
    	fileList.put(this.DAO_USERACCOUNT, "oracle_useraccount_dao.xml");
    	fileList.put(this.DAO_STUDY, "oracle_study_dao.xml");
    	fileList.put(this.DAO_STUDYEVENTDEFNITION, "oracle_studyeventdefinition_dao.xml");
    	fileList.put(this.DAO_STUDYEVENT, "oracle_study_event_dao.xml");
    	fileList.put(this.DAO_STUDYGROUP, "oracle_study_group_dao.xml");
    	fileList.put(this.DAO_STUDYSUBJECT, "oracle_study_subject_dao.xml");
    	fileList.put(this.DAO_SUBJECT, "oracle_subject_dao.xml");
    	fileList.put(this.DAO_SUBJECTGROUPMAP, "oracle_subject_group_map_dao.xml");
    	fileList.put(this.DAO_EVENTDEFINITIONCRF, "oracle_event_definition_crf_dao.xml");
    	fileList.put(this.DAO_AUDITEVENT, "oracle_audit_event_dao.xml");
    	fileList.put(this.DAO_ITEM, "oracle_item_dao.xml");
    	fileList.put(this.DAO_ITEMDATA, "oracle_itemdata_dao.xml");
    	fileList.put(this.DAO_ITEMFORMMETADATA, "oracle_item_form_metadata_dao.xml");
    	fileList.put(this.DAO_CRF, "oracle_crf_dao.xml");
    	fileList.put(this.DAO_CRFVERSION, "oracle_crfversion_dao.xml");
    	fileList.put(this.DAO_DATASET, "oracle_dataset_dao.xml");
    	fileList.put(this.DAO_SECTION, "oracle_section_dao.xml");
    	fileList.put(this.DAO_FILTER, "oracle_filter_dao.xml");
    	fileList.put(this.DAO_EVENTCRF, "oracle_eventcrf_dao.xml");
    	fileList.put(this.DAO_MASKING, "oracle_masking_dao.xml");
    } else if ("postgres".equals(dbName)) {
    	fileList.put(this.DAO_USERACCOUNT, "useraccount_dao.xml");
    	fileList.put(this.DAO_ARCHIVED_DATASET_FILE,"archived_dataset_file_dao.xml");
    	fileList.put(this.DAO_STUDY, "study_dao.xml");
    	fileList.put(this.DAO_STUDYEVENTDEFNITION, "studyeventdefinition_dao.xml");
    	fileList.put(this.DAO_STUDYEVENT, "study_event_dao.xml");
    	fileList.put(this.DAO_STUDYGROUP, "study_group_dao.xml");
    	fileList.put(this.DAO_STUDYGROUPCLASS, "study_group_class_dao.xml");
    	fileList.put(this.DAO_STUDYSUBJECT, "study_subject_dao.xml");
    	fileList.put(this.DAO_SUBJECT, "subject_dao.xml");
    	fileList.put(this.DAO_SUBJECTGROUPMAP, "subject_group_map_dao.xml");
    	fileList.put(this.DAO_EVENTDEFINITIONCRF, "event_definition_crf_dao.xml");
    	fileList.put(this.DAO_AUDITEVENT, "audit_event_dao.xml");
    	fileList.put(this.DAO_AUDIT, "audit_dao.xml");
    	fileList.put(this.DAO_ITEM, "item_dao.xml");
    	fileList.put(this.DAO_ITEMDATA, "itemdata_dao.xml");
    	fileList.put(this.DAO_CRF, "crf_dao.xml");
    	fileList.put(this.DAO_CRFVERSION, "crfversion_dao.xml");
    	fileList.put(this.DAO_DATASET, "dataset_dao.xml");
    	fileList.put(this.DAO_SECTION, "section_dao.xml");
    	
    	fileList.put(this.DAO_FILTER, "filter_dao.xml");
    	fileList.put(this.DAO_MASKING,"masking_dao.xml");
    	fileList.put(this.DAO_EVENTCRF, "eventcrf_dao.xml");
    	fileList.put(this.DAO_ITEMFORMMETADATA, "item_form_metadata_dao.xml");
    	fileList.put(this.DAO_DISCREPANCY_NOTE, "discrepancy_note_dao.xml");
        fileList.put(this.DAO_STUDY_PARAMETER, "study_parameter_value_dao.xml");
      fileList.put(this.DAO_ITEM_GROUP, "item_group_dao.xml");
      fileList.put(this.DAO_ITEM_GROUP_METADATA, "item_group_metadata_dao.xml");


      //add files here as we port over to postgres, tbh
    }//should be either oracle or postgres, but what if the file is gone?
    else {
      // throw an exception here, ssachs
    }

    Set DAONames = fileList.keySet();
    Iterator DAONamesIt = DAONames.iterator();
    
    while (DAONamesIt.hasNext()) {
      String DAOName = (String) DAONamesIt.next();
      String DAOFileName = (String) fileList.get(DAOName);
      
      DAODigester newDaoDigester = new DAODigester();
      
      try {
        if (System.getProperty("catalina.home")==null) {
          newDaoDigester.setInputStream(new FileInputStream(JUNIT_XML_DIR + DAOFileName));
     	} else {
     	  String path = SQLInitServlet.PROPERTY_DIR;
          newDaoDigester.setInputStream(new FileInputStream(path + DAOFileName));
     	}
        try {
          newDaoDigester.run();
          digesters.put(DAOName, newDaoDigester);
        } catch (SAXException saxe) {
          saxe.printStackTrace();
        }//end try block for xml
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }//end try block for files
    }//end for loop

  }

}