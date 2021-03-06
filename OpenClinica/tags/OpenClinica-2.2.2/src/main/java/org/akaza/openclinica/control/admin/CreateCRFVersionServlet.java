/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.text.SimpleDateFormat;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.core.form.Validator;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.core.SQLInitServlet;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.oreilly.servlet.MultipartRequest;

/**
 * Create a new CRF verison by uploading excel file
 *
 * @author jxu, ywang
 */
public class CreateCRFVersionServlet extends SecureController {
	
	Locale locale ;
	//<  ResourceBundleresword,resexception,respage;

  /**
   *
   */
  public void mayProceed() throws InsufficientPermissionException {
	  
		locale = request.getLocale();
		//< resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);
		//< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
		//< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
	  
    if (ub.isSysAdmin()) {
      return;
    }
  }

  public void processRequest() throws Exception {
    resetPanel();
    panel.setStudyInfoShown(false);
    panel.setOrderedData(true);

    setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"));

    setToPanel(resword.getString("create_CRF_version"),	respage.getString("br_create_new_CRF_uploading"));
    setToPanel(resword.getString("revise_CRF_version") ,respage.getString("br_if_you_owner_CRF_version"));
    setToPanel(resword.getString("CRF_spreadsheet_template"),respage.getString("br_download_blank_CRF_spreadsheet_from"));
    setToPanel(resword.getString("example_CRF_br_spreadsheets"),respage.getString("br_download_example_CRF_instructions_from"));

    CRFDAO cdao = new CRFDAO(sm.getDataSource());
    CRFVersionDAO vdao = new CRFVersionDAO(sm.getDataSource());
    EventDefinitionCRFDAO edao = new EventDefinitionCRFDAO(sm.getDataSource());

    FormProcessor fp = new FormProcessor(request);
    //checks which module the requests are from
    String module = fp.getString(MODULE);
    //keep the module in the session
    session.setAttribute(MODULE,module);

    String action = request.getParameter("action");
    CRFVersionBean version = (CRFVersionBean) session.getAttribute("version");

    if (StringUtil.isBlank(action)) {
      logger.info("action is blank");
      request.setAttribute("version", version);
      forwardPage(Page.CREATE_CRF_VERSION);
    } else if ("confirm".equalsIgnoreCase(action)) {
    	String dir = SQLInitServlet.getField("filePath");
    	if (!(new File(dir)).exists()) {
    	  logger.info("The filePath in datainfo.properties is invalid " + dir);
    	  addPageMessage("The filePath you defined in datainfo.properties does not seem to be a valid path, please check it.");
    	  forwardPage(Page.CREATE_CRF_VERSION);
    	} 
    	//All the uploaded files will be saved in filePath/crf/original/
    	String theDir = dir + "crf" + File.separator + "original" + File.separator;
        if (!(new File(theDir)).isDirectory()) {
        	(new File(theDir)).mkdirs();
          logger.info("Made the directory " + theDir);
        }
	    MultipartRequest multi = new MultipartRequest(request, theDir, 50 * 1024 * 1024);
		String tempFile = "";
        try {
          tempFile = uploadFile(multi, theDir, version);
        } catch (Exception e) {
          // 
          logger.warning("*** Found exception during file upload***");
          e.printStackTrace();
        }
        session.setAttribute("tempFileName", tempFile);
        //YW, at this point, if there are errors, they point to no file provided and/or not xls format
        if(errors.isEmpty()) {
	        String s = ((NewCRFBean)session.getAttribute("nib")).getVersionName();
			if(s.length()>255) {
				Validator.addError(errors, "excel_file", "The version in the CRF worksheet Version column is more than 255 character long.");
			}else if(s.length()<=0) {
				Validator.addError(errors, "excel_file", "The VERSION column was blank in the CRF worksheet of your Excel spreadsheet.");
			}
			version.setName(s);
			if (version.getCrfId() == 0) {
			    version.setCrfId(fp.getInt("crfId"));
			}
			session.setAttribute("version", version);
        }		
        if (!errors.isEmpty()) {
        	logger.info("has validation errors ");
        	request.setAttribute("formMessages", errors);
        	forwardPage(Page.CREATE_CRF_VERSION);
        } else {
        	CRFBean crf = (CRFBean) cdao.findByPK(version.getCrfId());
        	ArrayList versions = (ArrayList) vdao.findAllByCRF(crf.getId());
        	for (int i = 0; i < versions.size(); i++) {
        		CRFVersionBean version1 = (CRFVersionBean) versions.get(i);
        		if (version.getName().equals(version1.getName())) {
        			// version already exists
        			logger.info("Version already exists; owner or not:" + ub.getId() + "," + version1.getOwnerId());
        			if (ub.getId() != version1.getOwnerId()) {// not owner
        				addPageMessage("The CRF version you try to upload already exists in the DB and is created by "
        						+ version1.getOwner().getName() 
        						+ ", please contact the owner to delete the previous version. "
        						+ "Or you can change the version name and upload a different version for the CRF.");
        				forwardPage(Page.CREATE_CRF_VERSION);
        				return;
        			} else {// owner,
        				ArrayList definitions = (ArrayList) edao.findByDefaultVersion(version1.getId());
        				if (!definitions.isEmpty()) {// used in definition
        					request.setAttribute("definitions", definitions);
        					forwardPage(Page.REMOVE_CRF_VERSION_DEF);
        					return;
        				} else {// not used in definition
        					int previousVersionId = version1.getId();
        					version.setId(previousVersionId);
        					session.setAttribute("version", version);
        					session.setAttribute("previousVersionId", new Integer(previousVersionId));
        					forwardPage(Page.REMOVE_CRF_VERSION_CONFIRM);
        					return;
        				}
        			}
        		}
        	}
        	// didn't find same version in the DB,let user upload the excel file
        	logger.info("didn't find same version in the DB,let user upload the excel file.");
        
        	//List excelErr = ((ArrayList)request.getAttribute("excelErrors"));
        	List excelErr = ((ArrayList)session.getAttribute("excelErrors"));
        	logger.info("excelErr.isEmpty()=" + excelErr.isEmpty());
        	if(excelErr != null && excelErr.isEmpty()) {
        		addPageMessage("<b>Congratulations!</b>  Your spreadsheet generated no errors.<br/>" +
                		"  <b>Please preview it.<br> You can save it to database by clicking \"Continue\".</b>"); 
        		forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PREVIEW);
        	} else {
        		logger.info("OpenClinicaException thrown, forwarding to CREATE_CRF_VERSION_CONFIRM.");
                forwardPage(Page.CREATE_CRF_VERSION_CONFIRM);
        	}
        	
        	return;
      	}
    } else if ("confirmsql".equalsIgnoreCase(action)) {
      NewCRFBean nib = (NewCRFBean) session.getAttribute("nib");
      if (nib != null && nib.getItemQueries() != null) {
        request.setAttribute("openQueries", nib.getItemQueries());
      } else {
        request.setAttribute("openQueries", new HashMap());
      }
      // check whether need to delete previous version
      Boolean deletePreviousVersion =
        (Boolean) session.getAttribute("deletePreviousVersion");
      Integer previousVersionId = (Integer) session.getAttribute("previousVersionId");
      if ((deletePreviousVersion != null && deletePreviousVersion.equals(Boolean.TRUE))
        && (previousVersionId != null && previousVersionId.intValue() > 0)) {
        logger.info("Need to delete previous version");
        // whether we can delete
        boolean canDelete = canDeleteVersion(previousVersionId.intValue());
        if (!canDelete) {
          logger.info("but cannot delete previous version");
          if (session.getAttribute("itemsHaveData") == null
            && session.getAttribute("eventsForVersion") == null) {
            addPageMessage(respage.getString("you_are_not_owner_some_items_cannot_delete"));
          }
          if (session.getAttribute("itemsHaveData") == null) {
            session.setAttribute("itemsHaveData", new ArrayList());
          }
          if (session.getAttribute("eventsForVersion") == null) {
            session.setAttribute("eventsForVersion", new ArrayList());
          }

          forwardPage(Page.CREATE_CRF_VERSION_NODELETE);
          return;
        }
        try {
          CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
          ArrayList nonSharedItems = (ArrayList) cvdao
            .findNotSharedItemsByVersion(previousVersionId.intValue());

          // now delete the version and related info

          nib.deleteFromDB();
          //The purpose of this new instance?
          NewCRFBean nib1 = (NewCRFBean) session.getAttribute("nib");
          // not all the items already in the item table,still need to
          // insert new ones
          if (!nonSharedItems.isEmpty()) {
            logger.info("items are not shared, need to insert");
            request.setAttribute("openQueries", nib1.getBackupItemQueries());
            nib1.setItemQueries(nib1.getBackupItemQueries());
          } else {
            // no need to insert new items, all items are already in the
            // item table(shared by other versions)
            nib1.setItemQueries(new HashMap());

          }
          session.setAttribute("nib", nib1);

        } catch (OpenClinicaException pe) {
          session.setAttribute("excelErrors", nib.getDeleteErrors());
          //request.setAttribute("excelErrors", nib.getDeleteErrors());
          logger.info("OpenClinicaException thrown, forwarding to CREATE_CRF_VERSION_ERROR.");
          forwardPage(Page.CREATE_CRF_VERSION_ERROR);
          return;
        }
      }

/*                } else if ("commitsql".equalsIgnoreCase(action)) {
*/

      // submit
      logger.info("commit sql");
      NewCRFBean nib1 = (NewCRFBean) session.getAttribute("nib");
      if (nib1 != null) {
        try {
          nib1.insertToDB();
          request.setAttribute("queries", nib1.getQueries());
          // YW << for add a link to "View CRF Version Data Entry". For this purpose, CRFVersion id is needed.
          // So the latest CRFVersion Id of A CRF Id is it.
          CRFVersionDAO cvdao = new CRFVersionDAO(sm.getDataSource());
          ArrayList crfvbeans = new ArrayList();

          crfvbeans = cvdao.findAllByCRFId(version.getCrfId());
          CRFVersionBean cvbean = (CRFVersionBean) crfvbeans.get(crfvbeans.size()-1);
          int crfVersionId = cvbean.getId();
          for (Iterator iter = crfvbeans.iterator();iter.hasNext(); ) {
            cvbean = (CRFVersionBean) iter.next();
            if(crfVersionId < cvbean.getId()) {
              crfVersionId = cvbean.getId();
            }
          }
          //Not needed; crfVersionId will be autoboxed in Java 5
          //this was added for the old CVS java compiler
          Integer cfvID = new Integer(crfVersionId); 
          request.setAttribute("crfVersionId", cfvID);
          // YW >>
          // return those properties to initial values
          session.removeAttribute("version");
          session.removeAttribute("eventsForVersion");
          session.removeAttribute("itemsHaveData");
          session.removeAttribute("nib");
          session.removeAttribute("deletePreviousVersion");
          session.removeAttribute("previousVersionId");

          // save new version spreadsheet
          String tempFile = (String) session.getAttribute("tempFileName");
          if (tempFile != null) {
            logger.info("*** ^^^ *** saving new version spreadsheet" + tempFile);
            try {
              String dir = SQLInitServlet.getField("filePath");
              File f = new File(dir + "crf" + File.separator + "original" + File.separator
                + tempFile);
              // check to see whether crf/new/ folder exists inside, if not,
              // creates
              // the crf/new/ folder
              String finalDir = dir + "crf" + File.separator + "new" + File.separator;

              if (!new File(finalDir).isDirectory()) {
                logger.info("need to create folder for excel files" + finalDir);
                new File(finalDir).mkdirs();
              }

              String newFile = version.getCrfId() + version.getName() + ".xls";
              logger.info("*** ^^^ *** new file: " + newFile);
              File nf = new File(finalDir + newFile);
              logger.info("copying old file " + f.getName() + " to new file " + nf.getName());
              copy(f, nf);
              // ?
            } catch (IOException ie) {
              addPageMessage(respage.getString("CRF_version_spreadsheet_could_not_saved_contact"));
            }

          }
          session.removeAttribute("tempFileName");
          session.removeAttribute(MODULE);
          session.removeAttribute("excelErrors");
          session.removeAttribute("htmlTab");
          forwardPage(Page.CREATE_CRF_VERSION_DONE);
        } catch (OpenClinicaException pe) {
          session.setAttribute("excelErrors", nib1.getErrors());
          //request.setAttribute("excelErrors", nib1.getErrors());
          forwardPage(Page.CREATE_CRF_VERSION_ERROR);
        }
      } else {
        forwardPage(Page.CREATE_CRF_VERSION);
      }
    } else if ("delete".equalsIgnoreCase(action)) {
      logger.info("user wants to delete previous version");
      session.setAttribute("deletePreviousVersion", Boolean.TRUE);
      List excelErr = ((ArrayList)session.getAttribute("excelErrors"));
  	  logger.info("for overwrite CRF version, excelErr.isEmpty()=" + excelErr.isEmpty());
  	  if(excelErr != null && excelErr.isEmpty()) {
  		addPageMessage("<b>Congratulations!</b>  Your spreadsheet generated no errors.<br/>" +
  				"  <b>Please preview it.<br> You can save it to database by clicking \"Continue\".</b>"); 
  		forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PREVIEW);
  	  } else {
  		logger.info("OpenClinicaException thrown, forwarding to CREATE_CRF_VERSION_CONFIRM.");
  		forwardPage(Page.CREATE_CRF_VERSION_CONFIRM);
  	  }
      
    }
  }

  /**
   * Uploads the excel version file
   *
   * @param version
   * @throws Exception
   */
  public String uploadFile(MultipartRequest multi, String theDir, CRFVersionBean version) throws Exception {
    Enumeration files = multi.getFileNames();
    errors.remove("excel_file");
    String tempFile = null;
    while (files.hasMoreElements()) {
      String name = (String) files.nextElement();
      File f = multi.getFile(name);
      if (f == null || (f.getName() == null)) {
        logger.info("file is empty.");
        Validator.addError(errors, "excel_file", "You have to provide an Excel spreadsheet!");
        session.setAttribute("version", version);
        return tempFile;
      }
      else if(((f.getName().indexOf(".xls") < 0) && (f.getName().indexOf(".XLS") < 0))) {
        logger.info("file name:" + f.getName());
        
	addPageMessage(respage.getString("file_you_uploaded_not_seem_excel_spreadsheet"));
	//Validator.addError(errors, "excel_file", "The file you uploaded does not seem to be an Excel spreadsheet. Please upload again.");
        session.setAttribute("version", version);
        return tempFile;

      } else {
        logger.info("file name:" + f.getName());
        tempFile = f.getName();
        //create the inputstream here, so that it can be enclosed in a
        //try/finally block and closed :: BWP, 06/08/2007
        FileInputStream inStream = null;
        FileInputStream inStreamClassic = null;
        SpreadSheetTableRepeating htab = null;
        SpreadSheetTableClassic sstc = null;
        // create newCRFBean here
        NewCRFBean nib = null;
        try{
          inStream =  new
            FileInputStream(theDir + tempFile);

          //*** now change the code here to generate sstable, tbh 06/07
          htab = new SpreadSheetTableRepeating(inStream, ub,
            //SpreadSheetTable htab = new SpreadSheetTable(new FileInputStream(theDir + tempFile), ub,
            version.getName());

          if (!htab.isRepeating()) {
            inStreamClassic =  new
              FileInputStream(theDir + tempFile);
            sstc = new SpreadSheetTableClassic(
              inStreamClassic,
              ub,
              version.getName());
          }
          //logger.info("finishing with feedin file-input-stream, did we error out here?");

          if (htab.isRepeating()) {
            htab.setCrfId(version.getCrfId());
            //not the best place for this but for now...
            session.setAttribute("new_table","y");
          } else {
            sstc.setCrfId(version.getCrfId());
          }


          if (htab.isRepeating()) {
            nib = htab.toNewCRF(sm.getDataSource());
          } else {
            nib = sstc.toNewCRF(sm.getDataSource());
          }

          //bwp; 2/28/07; updated 6/11/07;
          // This object is created to pull preview information out of the
          //spreadsheet
          HSSFWorkbook workbook = null;
          FileInputStream inputStream = null;
          try{
            inputStream = new FileInputStream(theDir +
              tempFile);
            workbook =   new HSSFWorkbook(inputStream);
            //Store the Sections, Items, Groups, and CRF name and version information
            //so they can be displayed in a preview. The Map consists of the
            //names "sections," "items," "groups," and "crf_info" as keys, each of which point
            //to a Map containing data on those CRF sections.

            //Check if it's the old template
            Preview preview;
            if(htab.isRepeating()) {

               //the preview uses date formatting with default values in date fields: yyyy-MM-dd
              preview = new SpreadsheetPreviewNw();

            }  else {
              preview = new SpreadsheetPreview();

            }
            session.setAttribute("preview_crf",
              preview.createCrfMetaObject(workbook));
          }   catch (Exception exc)  {   //opening the stream could throw FileNotFoundException
            exc.printStackTrace();
            String message =
              "The application encountered a problem uploading the Excel template and creating a preview CRF";
            logger.info(
              message+ ": "+
                exc.getMessage());
            this.addPageMessage(message);
          }  finally {
            if(inputStream != null) {
              try{inputStream.close();
              } catch(IOException io){
                //ignore this close()-related exception
              }
            }
          }
          ArrayList ibs = isItemSame(nib.getItems(), version);

          if (!ibs.isEmpty()) {
            ArrayList warnings = new ArrayList();
            warnings
              .add(resexception.getString("you_may_not_modify_items"));
            for (int i = 0; i < ibs.size(); i++) {
              ItemBean ib = (ItemBean) ibs.get(i);
              if (ib.getOwner().getId() == ub.getId()) {
                warnings.add(resword.getString("the_item")+" '"+ ib.getName()
                      + "'"+ resexception.getString("in_your_spreadsheet_already_exists") + ib.getDescription()
                      + "), DATA_TYPE(" + ib.getDataType().getName()+ "), UNITS("
                      + ib.getUnits()+ "), " + resword.getString("and_or")+ "PHI_STATUS(" + ib.isPhiStatus()
                      + "). UNITS" + resword.getString("and") + "DATA_TYPE" + resexception.getString("will_not_be_changed_if")+ "PHI, DESCRIPTION, "
                      + resexception.getString("will_be_changed_if_you_continue"));
            } else {
              warnings.add(resword.getString("the_item")+" '"+ ib.getName() + "' in your spreadsheet already exists for "
                  + "'"+ resexception.getString("in_your_spreadsheet_already_exists") + ib.getDescription()
                  + "), " + "DATA_TYPE(" + ib.getDataType().getName() + ") ,"
                  + "UNITS(" + ib.getUnits() + ")," + resword.getString("and_or")+ "PHI_STATUS(" + ib.isPhiStatus()
                  + ")." + resexception.getString("these_field_cannot_be_modified_because_not_owner"));
            }

              request.setAttribute("warnings", warnings);
            }
          }
          ItemBean ib = isResponseValid(nib.getItems(), version);
          if (ib != null) {

		nib.getErrors().add(resword.getString("the_item")+": "+ ib.getName()+ resexception.getString("in_your_spreadsheet_already_exits_in_DB"));          }
        } catch (IOException io)  {
          logger.warning(
            "Opening up the Excel file caused an error. the error message is: "+
              io.getMessage());

        }  finally {
          if(inStream != null)  {
            try{
              inStream.close();
            } catch (IOException ioe) {}
          }
          if(inStreamClassic != null)  {
            try{
              inStreamClassic.close();
            } catch (IOException ioe) {}
          }
        }
        //request.setAttribute("excelErrors", nib.getErrors());
        session.setAttribute("excelErrors", nib.getErrors());
        session.setAttribute("htmlTable", nib.getHtmlTable());
        session.setAttribute("nib", nib);
      }
    }
    return tempFile;
  }

  /**
   * Checks whether the version can be deleted
   *
   * @param previousVersionId
   * @return
   */
  private boolean canDeleteVersion(int previousVersionId) {
    CRFVersionDAO cdao = new CRFVersionDAO(sm.getDataSource());
    ArrayList items = null;
    ArrayList itemsHaveData = new ArrayList();
    // boolean isItemUsedByOtherVersion =
    // cdao.isItemUsedByOtherVersion(previousVersionId);
    // if (isItemUsedByOtherVersion) {
    // ArrayList itemsUsedByOtherVersion = (ArrayList)
    // cdao.findItemUsedByOtherVersion(previousVersionId);
    // session.setAttribute("itemsUsedByOtherVersion",itemsUsedByOtherVersion);
    // return false;
    EventCRFDAO ecdao = new EventCRFDAO(sm.getDataSource());
    ArrayList events = ecdao.findAllByCRFVersion(previousVersionId);
    if (!events.isEmpty()) {
      session.setAttribute("eventsForVersion", events);
      return false;
    }
    items = (ArrayList) cdao.findNotSharedItemsByVersion(previousVersionId);
    for (int i = 0; i < items.size(); i++) {
      ItemBean item = (ItemBean) items.get(i);
      if (ub.getId() != item.getOwner().getId()) {
        logger.info("not owner" + item.getOwner().getId() + "<>" + ub.getId());
        return false;
      }
      if (cdao.hasItemData(item.getId())) {
        itemsHaveData.add(item);
        logger.info("item has data");
        session.setAttribute("itemsHaveData", itemsHaveData);
        return false;
      }
    }

    // user is the owner and item not have data,
    // delete previous version with non-shared items
    NewCRFBean nib = (NewCRFBean) session.getAttribute("nib");
    nib.setDeleteQueries(cdao.generateDeleteQueries(previousVersionId, items));
    session.setAttribute("nib", nib);
    return true;

  }

  /**
   * Checks whether the item with same name has the same other fields: units,
   * phi_status if no, they are two different items, cannot have the same same
   *
   * @param items
   *          items from excel
   * @return the items found
   */
  private ArrayList isItemSame(HashMap items, CRFVersionBean version) {
    ItemDAO idao = new ItemDAO(sm.getDataSource());
    ArrayList diffItems = new ArrayList();
    Set names = items.keySet();
    Iterator it = names.iterator();
    while (it.hasNext()) {
      String name = (String) it.next();
      ItemBean newItem = (ItemBean) idao.findByNameAndCRFId(name, version.getCrfId());
      ItemBean item = (ItemBean) items.get(name);
      if (newItem.getId() > 0) {
        if (!item.getUnits().equalsIgnoreCase(newItem.getUnits())
          || item.isPhiStatus() != newItem.isPhiStatus()
          || item.getDataType().getId() != newItem.getDataType().getId()
          || !item.getDescription().equalsIgnoreCase(newItem.getDescription())) {

          logger
            .info("found two items with same name but different units/phi/datatype/description");
          // logger.info("item" + newItem.getId() );
          // logger.info("unit" + newItem.getUnits() + " |" + item.getUnits() );
          // logger.info("phi" + newItem.isPhiStatus() + "| " +
          // item.isPhiStatus() );
          // logger.info("DataType" + newItem.getDataType().getId() + " | " +
          // item.getDataType().getId() );
          // logger.info("Description" + newItem.getDescription() + " |" +
          // item.getDescription() );
          diffItems.add(newItem);
        }
      }
    }

    return diffItems;
  }

  private ItemBean isResponseValid(HashMap items, CRFVersionBean version) {
    ItemDAO idao = new ItemDAO(sm.getDataSource());
    ItemFormMetadataDAO metadao = new ItemFormMetadataDAO(sm.getDataSource());
    Set names = items.keySet();
    Iterator it = names.iterator();
    while (it.hasNext()) {
      String name = (String) it.next();
      ItemBean oldItem = (ItemBean) idao.findByNameAndCRFId(name, version.getCrfId());
      ItemBean item = (ItemBean) items.get(name);
      if (oldItem.getId() > 0) {// found same item in DB
        ArrayList metas = metadao.findAllByItemId(oldItem.getId());
        for (int i = 0; i < metas.size(); i++) {
          ItemFormMetadataBean ifmb = (ItemFormMetadataBean) metas.get(i);
          ResponseSetBean rsb = ifmb.getResponseSet();
          if (hasDifferentOption(rsb, item.getItemMeta().getResponseSet()) != null) {
            return item;
          }
        }

      }
    }
    return null;

  }

  protected String getAdminServlet() {
    if (ub.isSysAdmin()) {
      return SecureController.ADMIN_SERVLET_CODE;
    } else {
      return "";
    }
  }

  /**
   * When the version is added, for each non-new item OpenClinica should check
   * the RESPONSE_OPTIONS_TEXT, and RESPONSE_VALUES used for the item in other
   * versions of the CRF.
   *
   * For a given RESPONSE_VALUES code, the associated RESPONSE_OPTIONS_TEXT
   * string is different than in a previous version
   *
   * For a given RESPONSE_OPTIONS_TEXT string, the associated RESPONSE_VALUES
   * code is different than in a previous version
   *
   * @param oldRes
   * @param newRes
   * @return The original option
   */
  public ResponseOptionBean hasDifferentOption(ResponseSetBean oldRes, ResponseSetBean newRes) {
    ArrayList oldOptions = oldRes.getOptions();
    ArrayList newOptions = newRes.getOptions();
    if (oldOptions.size() != newOptions.size()) {
      // if the sizes are different, means the options don't match
      return null;

    } else {
      for (int i = 0; i < oldOptions.size(); i++) {// from database
        ResponseOptionBean rob = (ResponseOptionBean) oldOptions.get(i);
        String text = rob.getText();
        String value = rob.getValue();
        for (int j = i; j < newOptions.size(); j++) {// from spreadsheet
          ResponseOptionBean rob1 = (ResponseOptionBean) newOptions.get(j);
          // changed by jxu on 08-29-06, to fix the problem of cannot recognize
          // the same responses
          String text1 = restoreQuotes(rob1.getText());

          String value1 = restoreQuotes(rob1.getValue());

          if (StringUtil.isBlank(text1) && StringUtil.isBlank(value1)) {
            // this response label appears in the spreadsheet multiple times, so
            // ignore the checking for the repeated ones
            break;
          }
          if (text1.equalsIgnoreCase(text) && !value1.equals(value)) {
            System.out.println("different response value:" + value1 + "|" + value);
            return rob;
          } else if (!text1.equalsIgnoreCase(text) && value1.equals(value)) {
            System.out.println("different response text:" + text1 + "|" + text);
            return rob;
          }
          break;
        }
      }

    }
    return null;
  }

  /**
   * Copy one file to another
   *
   * @param src
   * @param dst
   * @throws IOException
   */
  public void copy(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);

    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }

  /**
   * restoreQuotes, utility function meant to replace double quotes in strings
   * with single quote. Don''t -> Don't, for example. If the option text has
   * single quote, it is changed to double quotes for SQL compatability, so we
   * will change it back before the comparison
   *
   * @param subj
   *          the subject line
   * @return A string with all the quotes escaped.
   */
  public String restoreQuotes(String subj) {
    if (subj == null) {
      return null;
    }
    String returnme = "";
    String[] subjarray = subj.split("''");
    if (subjarray.length == 1) {
      returnme = subjarray[0];
    } else {
      for (int i = 0; i < (subjarray.length - 1); i++) {
        returnme += subjarray[i];
        returnme += "'";
      }
      returnme += subjarray[subjarray.length - 1];
    }
    return returnme;
  }
}
