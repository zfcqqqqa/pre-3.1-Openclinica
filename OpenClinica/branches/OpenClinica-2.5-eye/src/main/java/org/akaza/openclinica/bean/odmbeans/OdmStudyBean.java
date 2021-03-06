/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */
public class OdmStudyBean extends ElementOIDBean {
    private GlobalVariablesBean globalVariables;
    private MetaDataVersionBean metaDataVersion;
    
    public OdmStudyBean() {
        globalVariables = new GlobalVariablesBean();
        metaDataVersion = new MetaDataVersionBean();
    }

    public void setGlobalVariables(GlobalVariablesBean gv) {
        this.globalVariables = gv;
    }

    public GlobalVariablesBean getGlobalVariables() {
        return this.globalVariables;
    }

    public void setMetaDataVersion(MetaDataVersionBean metadataversion) {
        this.metaDataVersion = metadataversion;
    }

    public MetaDataVersionBean getMetaDataVersion() {
        return this.metaDataVersion;
    }
}