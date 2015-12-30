/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.managestudy;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.managestudy.StudyRow;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.EntityBeanTable;
import org.akaza.openclinica.core.form.FormProcessor;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author jxu
 * @version CVS: $Id: ListSiteServlet.java 12142 2008-12-04 13:17:58Z ahamid $
 */
public class ListSiteServlet extends SecureController {

    Locale locale;

    // < ResourceBundle resword,respage,resexception;
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {

        locale = request.getLocale();
        // <
        // resexception=ResourceBundle.getBundle("org.akaza.openclinica.i18n.exceptions",locale);
        // < respage =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.page_messages",locale);
        // < resword =
        // ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",locale);

        if (ub.isSysAdmin()) {
            return;
        }

        if (currentRole.getRole().equals(Role.STUDYDIRECTOR) || currentRole.getRole().equals(Role.COORDINATOR)) {
            return;
        }

        addPageMessage(respage.getString("no_have_correct_privilege_current_study") + respage.getString("change_study_contact_sysadmin"));
        throw new InsufficientPermissionException(Page.MANAGE_STUDY_SERVLET, resexception.getString("not_study_director"), "1");

    }

    /**
     * Finds all the studies, processes the request
     */
    @Override
    public void processRequest() throws Exception {
        FormProcessor fp = new FormProcessor(request);
        if (currentStudy.getParentStudyId() > 0) {
            addPageMessage(respage.getString("no_sites_available_study_is_a_site"));
            forwardPage(Page.MENU_SERVLET);
        } else {

            StudyDAO sdao = new StudyDAO(sm.getDataSource());
            ArrayList studies = (ArrayList) sdao.findAllByParent(currentStudy.getId());

            EntityBeanTable table = fp.getEntityBeanTable();
            ArrayList allStudyRows = StudyRow.generateRowsFromBeans(studies);

            String[] columns =
                { resword.getString("name"), resword.getString("unique_identifier"), resword.getString("OID"),resword.getString("principal_investigator"),
                    resword.getString("facility_name"), resword.getString("date_created"), resword.getString("status"), resword.getString("actions") };
            table.setColumns(new ArrayList(Arrays.asList(columns)));
            table.hideColumnLink(2);
            table.hideColumnLink(6);
            table.setQuery("ListSite", new HashMap());
            if(!currentStudy.getStatus().isLocked()){
                table.addLink(resword.getString("create_a_new_site"), "CreateSubStudy");    
            }

            table.setRows(allStudyRows);
            table.computeDisplay();

            request.setAttribute("table", table);
            // request.setAttribute("studies", studies);
            session.setAttribute("fromListSite", "yes");
            forwardPage(Page.SITE_LIST);
        }

    }

}