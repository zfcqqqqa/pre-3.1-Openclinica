/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.exception.InsufficientPermissionException;
import org.akaza.openclinica.view.Page;

/**
 * Prepares to creat a new CRF Version
 *
 * @author jxu
 */
public class InitCreateCRFVersionServlet extends SecureController {
    /**
     *
     */
    @Override
    public void mayProceed() throws InsufficientPermissionException {
        if (ub.isSysAdmin()) {
            return;
        }
    }

    @Override
    public void processRequest() throws Exception {

        resetPanel();
        panel.setStudyInfoShown(false);
        panel.setOrderedData(true);

        setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"));

        setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"));
        setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"));
        setToPanel(resword.getString("CRF_spreadsheet_template"), respage.getString("br_download_blank_CRF_spreadsheet_from"));
        setToPanel(resword.getString("example_CRF_br_spreadsheets"), respage.getString("br_download_example_CRF_instructions_from"));

        String idString = request.getParameter("crfId");
        String name = request.getParameter("name");
        // logger.info("crf id:" + idString);

        // checks which module the requests are from
        String module = request.getParameter(MODULE);
        request.setAttribute(MODULE, module);

        if (StringUtil.isBlank(idString) || StringUtil.isBlank(name)) {
            addPageMessage(respage.getString("please_choose_a_CRF_to_add_new_version_for"));
            forwardPage(Page.CRF_LIST);
        } else {
            // crf id
            int crfId = Integer.valueOf(idString.trim()).intValue();
            CRFVersionBean version = new CRFVersionBean();
            version.setCrfId(crfId);
            session.setAttribute("version", version);
            request.setAttribute("crfName", name);
            forwardPage(Page.CREATE_CRF_VERSION);
        }
    }

    @Override
    protected String getAdminServlet() {
        if (ub.isSysAdmin()) {
            return SecureController.ADMIN_SERVLET_CODE;
        } else {
            return "";
        }
    }
}
