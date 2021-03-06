/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.bean.submit;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.CrfVersionOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;

import java.util.Date;

/**
 * The object to carry CRF versions in the application.
 *
 * @author thickerson
 *
 */
public class CRFVersionBean extends AuditableEntityBean {

    private String description = "";
    private int crfId = 0;
    private int statusId = 1;
    private String revisionNotes = "";
    private Date date_created;

    private boolean downloadable = false;// not in DB, tells whether the
                                            // spreadsheet is downloadable

    private String oid;
    private OidGenerator oidGenerator;

    public CRFVersionBean() {
        this.oidGenerator = new CrfVersionOidGenerator();
        date_created = new Date();
    }

    /**
     * @return date_created Date
     */
    public Date getDateCreated() {
        return date_created;
    }

    /**
     * @return Returns the cRFId.
     */
    public int getCrfId() {
        return crfId;
    }

    /**
     * @param id
     *            The cRFId to set.
     */
    public void setCrfId(int id) {
        crfId = id;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the revisionNotes.
     */
    public String getRevisionNotes() {
        return revisionNotes;
    }

    /**
     * @param revisionNotes
     *            The revisionNotes to set.
     */

    public void setRevisionNotes(String revisionNotes) {
        this.revisionNotes = revisionNotes;

    }

    /**
     * @return Returns the statusId.
     */
    public int getStatusId() {
        return statusId;
    }

    /**
     * @param statusId
     *            The statusId to set.
     */
    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    /**
     * @return Returns the downloadable.
     */
    public boolean isDownloadable() {
        return downloadable;
    }

    /**
     * @param downloadable
     *            The downloadable to set.
     */
    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public OidGenerator getOidGenerator() {
        return oidGenerator;
    }

    public void setOidGenerator(OidGenerator oidGenerator) {
        this.oidGenerator = oidGenerator;
    }
}
