/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package org.akaza.openclinica.control.submit;

import java.util.ArrayList;
import java.util.List;

import org.akaza.openclinica.bean.core.DataEntryStage;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.DisplayItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.core.form.DiscrepancyValidator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author ssachs
 */
public class InitialDataEntryServlet extends DataEntryServlet {
	
	Locale locale;
	//<  ResourceBundleresexception,respage;

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	protected void mayProceed() throws InsufficientPermissionException {
		
		  locale = request.getLocale();
		  //< resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);  
		  //< respage = ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
		
		getInputBeans();

		DataEntryStage stage = ecb.getStage();
		Role r = currentRole.getRole();
		
		if (stage.equals(DataEntryStage.UNCOMPLETED)) {
			if (!SubmitDataServlet.maySubmitData(ub, currentRole)) {
				String exceptionName = resexception.getString("no_permission_to_perform_data_entry");
				String noAccessMessage = respage.getString("you_may_not_perform_data_entry_on_a_CRF")+ respage.getString("change_study_contact_study_coordinator");
				
				addPageMessage(noAccessMessage);
				throw new InsufficientPermissionException(Page.MENU, exceptionName, "1");
			}
		}
		else if (stage.equals(DataEntryStage.INITIAL_DATA_ENTRY)) {
			if ((ub.getId() != ecb.getOwnerId()) 
					&& !r.equals(Role.STUDYDIRECTOR)
					&& !r.equals(Role.COORDINATOR)) {
				addPageMessage(respage.getString("you_may_not_perform_data_entry_on_event_CRF_because_not_owner"));
				throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, resexception.getString("non_owner_attempting_DE_on_event"), "1");
			}
		}
		else {
			addPageMessage(respage.getString("you_not_enter_data_initial_DE_completed"));
			throw new InsufficientPermissionException(Page.SUBMIT_DATA_SERVLET, resexception.getString("using_IDE_event_CRF_completed"), "1");
		}
		
		return ;
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateInputOnFirstRound()
	 */
	protected boolean validateInputOnFirstRound() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#validateDisplayItemBean(org.akaza.openclinica.core.form.Validator, org.akaza.openclinica.bean.submit.DisplayItemBean)
	 */
	protected DisplayItemBean validateDisplayItemBean(
			DiscrepancyValidator v,
			DisplayItemBean dib, 
			String inputName) {
		
		ItemBean ib = dib.getItem();
		org.akaza.openclinica.bean.core.ResponseType rt = dib.getMetadata().getResponseSet().getResponseType();

		
		// note that this step sets us up both for
		// displaying the data on the form again, in the event of an error
		// and sending the data to the database, in the event of no error
        if(StringUtil.isBlank(inputName)){// not an item from group, doesn't need to get data from form again
		  dib = loadFormValue(dib);
        }
		
		// types TEL and ED are not supported yet
		if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXT) ||
				rt.equals(org.akaza.openclinica.bean.core.ResponseType.TEXTAREA)) {
			dib = validateDisplayItemBeanText(v, dib, inputName);
		}
		else if (rt.equals(org.akaza.openclinica.bean.core.ResponseType.RADIO) ||
				rt.equals(org.akaza.openclinica.bean.core.ResponseType.SELECT)) {
			dib = validateDisplayItemBeanSingleCV(v, dib, inputName);
		}
		
		return dib;
	}

    
    protected List<DisplayItemGroupBean> validateDisplayItemGroupBean(
    		DiscrepancyValidator v,
    		DisplayItemGroupBean digb,
    		List<DisplayItemGroupBean>digbs,
    		List<DisplayItemGroupBean>formGroups){
      
      formGroups = loadFormValueForItemGroup(digb, digbs, formGroups, edcb.getId());
      String inputName="";
      for (int i=0; i<formGroups.size(); i++) {   
        DisplayItemGroupBean displayGroup= formGroups.get(i);
       
        List<DisplayItemBean> items = displayGroup.getItems();
        int order =displayGroup.getOrdinal();
        if (displayGroup.isAuto() && displayGroup.getFormInputOrdinal()>0){
          order = displayGroup.getFormInputOrdinal();
        }
        for(DisplayItemBean displayItem : items){
          if (displayGroup.isAuto()){
          inputName =getGroupItemInputName(displayGroup,
              order, displayItem);
          } else {
            inputName =getGroupItemManualInputName(displayGroup,
                order, displayItem);
          }
          validateDisplayItemBean(v, displayItem, inputName);                   
        }       
      }     
      return formGroups;
      
    }
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getBlankItemStatus()
	 */
	protected Status getBlankItemStatus() {
		return Status.AVAILABLE;
	}

	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getNonBlankItemStatus()
	 */
	protected Status getNonBlankItemStatus() {
		return edcb.isDoubleEntry() ? Status.PENDING : Status.UNAVAILABLE;
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getEventCRFAnnotations()
	 */
	protected String getEventCRFAnnotations() {
		return ecb.getAnnotations();
	}
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#setEventCRFAnnotations(java.lang.String)
	 */
	protected void setEventCRFAnnotations(String annotations) {
		ecb.setAnnotations(annotations);
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getJSPPage()
	 */
	protected Page getJSPPage() {
   // request.setAttribute("newtable","y");
    return Page.INITIAL_DATA_ENTRY_NW;
    
	}
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#getServletPage()
	 */
	protected Page getServletPage() {
        String tabId= fp.getString("tab",true);
        String sectionId= fp.getString(DataEntryServlet.INPUT_SECTION_ID,true);
		String eventCRFId = fp.getString(INPUT_EVENT_CRF_ID,true);
        if (StringUtil.isBlank(sectionId) || StringUtil.isBlank(tabId)) {
        return Page.INITIAL_DATA_ENTRY_SERVLET;
        } else {
          Page target = Page.INITIAL_DATA_ENTRY_SERVLET ;
          target.setFileName(target.getFileName()+"?eventCRFId=" +eventCRFId + "&sectionId="+ sectionId + "&tab=" + tabId);
          return target;
        }
	}
	
	/* (non-Javadoc)
	 * @see org.akaza.openclinica.control.submit.DataEntryServlet#loadDBValues()
	 */
	protected boolean shouldLoadDBValues(DisplayItemBean dib) {
		return true;
	}
    
    
}
