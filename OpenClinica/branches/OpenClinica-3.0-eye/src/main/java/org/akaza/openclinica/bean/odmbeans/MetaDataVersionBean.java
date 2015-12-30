/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.bean.odmbeans;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */
public class MetaDataVersionBean extends ElementOIDBean {
    private String name;
    private String Description;
    private MetaDataVersionIncludeBean include;
    private MetaDataVersionProtocolBean protocol;
    private List<StudyEventDefBean> studyEventDefs;
    private List<FormDefBean> formDefs;
    private List<ItemGroupDefBean> itemGroupDefs;
    private List<ItemDefBean> itemDefs;
    private List<CodeListBean> codeLists;
    private List<StudyGroupClassListBean> studyGroupClassLists;
    // OpenClinica system has set softhard constraint on study level
    private String softhard;
    private List<MultiSelectListBean> multiSelectLists;

    public MetaDataVersionBean() {
        include = new MetaDataVersionIncludeBean();
        protocol = new MetaDataVersionProtocolBean();
        studyEventDefs = new ArrayList<StudyEventDefBean>();
        formDefs = new ArrayList<FormDefBean>();
        itemGroupDefs = new ArrayList<ItemGroupDefBean>();
        itemDefs = new ArrayList<ItemDefBean>();
        codeLists = new ArrayList<CodeListBean>();
        studyGroupClassLists = new ArrayList<StudyGroupClassListBean>();
        multiSelectLists = new ArrayList<MultiSelectListBean>();
    }

    public void setName(String metadataVersionName) {
        this.name = metadataVersionName;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getDescription() {
        return this.Description;
    }

    public void setInclude(MetaDataVersionIncludeBean include) {
        this.include = include;
    }

    public MetaDataVersionIncludeBean getInclude() {
        return this.include;
    }

    public void setProtocol(MetaDataVersionProtocolBean protocol) {
        this.protocol = protocol;
    }

    public MetaDataVersionProtocolBean getProtocol() {
        return this.protocol;
    }

    public void setStudyEventDefs(List<StudyEventDefBean> seds) {
        this.studyEventDefs = seds;
    }

    public List<StudyEventDefBean> getStudyEventDefs() {
        return this.studyEventDefs;
    }

    public void setFormDefs(List<FormDefBean> formDefs) {
        this.formDefs = formDefs;
    }

    public List<FormDefBean> getFormDefs() {
        return this.formDefs;
    }

    public void setItemGroupDefs(List<ItemGroupDefBean> igDefs) {
        this.itemGroupDefs = igDefs;
    }

    public List<ItemGroupDefBean> getItemGroupDefs() {
        return this.itemGroupDefs;
    }

    public void setItemDefs(List<ItemDefBean> itDefs) {
        this.itemDefs = itDefs;
    }

    public List<ItemDefBean> getItemDefs() {
        return this.itemDefs;
    }

    public void setCodeLists(List<CodeListBean> codeLists) {
        this.codeLists = codeLists;
    }

    public List<CodeListBean> getCodeLists() {
        return this.codeLists;
    }

    public void setStudyGroupClassLists(List<StudyGroupClassListBean> studyGroupClassLists) {
        this.studyGroupClassLists = studyGroupClassLists;
    }

    public List<StudyGroupClassListBean> getStudyGroupClassLists() {
        return this.studyGroupClassLists;
    }

    public void setSoftHard(String constraint) {
        this.softhard = constraint;
    }

    public String getSoftHard() {
        return this.softhard;
    }

    public List<MultiSelectListBean> getMultiSelectLists() {
        return multiSelectLists;
    }

    public void setMultiSelectLists(List<MultiSelectListBean> multiSelectLists) {
        this.multiSelectLists = multiSelectLists;
    }
}