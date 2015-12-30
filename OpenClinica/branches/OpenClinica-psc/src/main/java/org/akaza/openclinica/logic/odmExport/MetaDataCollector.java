/*
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.logic.odmExport;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.odmbeans.FormDefBean;
import org.akaza.openclinica.bean.odmbeans.GlobalVariablesBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionBean;
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionIncludeBean;
import org.akaza.openclinica.bean.odmbeans.OdmStudyBean;
import org.akaza.openclinica.bean.odmbeans.RangeCheckBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

/**
 * Populate metadata for odm study
 * 
 * @author ywang (May, 2008)
 */

public class MetaDataCollector extends OdmDataCollector {
    private OdmStudyBean odmStudy;
    private HashMap<String, Integer> igpos;
    private HashMap<String, Integer> itpos;
    private HashMap<String, Integer> clpos;

    private static int textLength;

    // protected final Logger logger =
    // LoggerFactory.getLogger(getClass().getName());

    /**
     * This constructor also called the populateStudyAndSeds() method.
     * 
     * @param ds
     * @param study
     */
    public MetaDataCollector(DataSource ds, StudyBean study) {
        super(ds, study);
        this.odmStudy = new OdmStudyBean();
        igpos = new HashMap<String, Integer>();
        itpos = new HashMap<String, Integer>();
        clpos = new HashMap<String, Integer>();
        textLength = 4000;
    }

    /**
     * This constructor also called the populateStudyAndSeds() method.
     * 
     * @param ds
     * @param dataset
     */
    public MetaDataCollector(DataSource ds, DatasetBean dataset) {
        super(ds, dataset);
        this.odmStudy = new OdmStudyBean();
        igpos = new HashMap<String, Integer>();
        itpos = new HashMap<String, Integer>();
        clpos = new HashMap<String, Integer>();
        textLength = 4000;
    }

    @Override
    public void collectFileData() {
        this.collectOdmRoot();
        this.collectOdmStudy();
    }

    public void collectOdmStudy() {
        StudyBean study = this.getStudy();
        String studyOID = study.getOid();
        if (studyOID == null || studyOID.length() <= 0) {
            logger.info("Constructed studyOID using study_id because oc_oid is missing from the table - study.");
            studyOID = "" + study.getId();
            study.setOid(studyOID);
        }
        odmStudy.setOid(studyOID);
        collectGlobalVariables();
        collectMetaDataVersion();
    }

    public void collectGlobalVariables() {
        StudyBean study = this.getStudy();
        StudyDAO sdao = new StudyDAO(this.ds);
        int parentId = study.getParentStudyId();
        String sn = study.getName();
        String sd = study.getSummary();
        String pn = study.getIdentifier();
        if (parentId > 0) {
            StudyBean parentStudy = (StudyBean) sdao.findByPK(parentId);
            sn = parentStudy.getName() + " - " + study.getName();
            sd = parentStudy.getSummary() + " - " + study.getSummary();
            pn = parentStudy.getIdentifier() + " - " + study.getIdentifier();
        }
        GlobalVariablesBean gv = new GlobalVariablesBean();
        gv.setStudyName(sn);
        gv.setStudyDescription(sd);
        gv.setProtocolName(pn);
        this.odmStudy.setGlobalVariables(gv);
    }

    public void collectMetaDataVersion() {
        ArrayList<StudyEventDefinitionBean> sedBeansInStudy = (ArrayList<StudyEventDefinitionBean>) this.getSedBeansInStudy();
        if (sedBeansInStudy == null) {
            logger.info("null, because sedBeansInStudy is null.");
            return;
        }

        MetaDataVersionBean metadata = this.odmStudy.getMetaDataVersion();
        if (this.dataset != null) {
            metadata.setOid(this.dataset.getODMMetaDataVersionOid());
            metadata.setName(this.dataset.getODMMetaDataVersionName());
        }
        if (metadata.getOid() == null || metadata.getOid().length() <= 0) {
            metadata.setOid("v1.0.0");
        }
        if (metadata.getName() == null || metadata.getName().length() <= 0) {
            metadata.setName("MetaDataVersion_v1.0.0");
        }

        StudyParameterValueDAO spvdao = new StudyParameterValueDAO(this.ds);
        StudyParameterValueBean spv = spvdao.findByHandleAndStudy(this.getStudy().getId(), "discrepancyManagement");
        metadata.setSoftHard(spv.getValue().equalsIgnoreCase("true") ? "Soft" : "Hard");

        // populate Include
        String psOid = new String();
        String pmOid = new String();
        if (this.dataset != null) {
            psOid = this.dataset.getODMPriorStudyOid();
            pmOid = this.dataset.getODMPriorMetaDataVersionOid();
        }
        if (pmOid != null && pmOid.length() > 0) {
            MetaDataVersionIncludeBean ib = new MetaDataVersionIncludeBean();
            ib.setMetaDataVersionOID(pmOid);
            if (psOid != null && psOid.length() > 0) {
                ib.setStudyOID(psOid);
            } else {
                ib.setStudyOID(this.odmStudy.getOid());
            }
            metadata.setInclude(ib);
        }

        // populate protocol
        OdmExtractDAO oedao = new OdmExtractDAO(this.ds);
        int parentId = this.getStudy().getParentStudyId();
        int studyId = parentId > 0 ? parentId : this.getStudy().getId();
        oedao.getStudyEventAndFormMetaByStudyId(studyId, metadata);
        if (metadata.getFormDefs().size() > 0) {
            for (int i = 0; i < metadata.getFormDefs().size(); ++i) {
                FormDefBean formdef = metadata.getFormDefs().get(i);
                oedao.getItemGroupAndItemMetaByCRFVersionOID(formdef.getOid(), formdef, metadata, igpos, itpos, clpos);
            }
        }
        Collections.sort(metadata.getFormDefs());
        Collections.sort(metadata.getItemGroupDefs());
        Collections.sort(metadata.getItemDefs());
        Collections.sort(metadata.getCodeLists());
    }

    public static String getOdmItemDataType(int OCDataTypeId) {
        switch (OCDataTypeId) {
        // BL //BN //ED //TEL //ST
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
            // SET
        case 8:
            return "text";
            // INT
        case 6:
            return "integer";
            // REAL
        case 7:
            return "float";
            // DATE
        case 9:
            return "date";
        default:
            return "text";
        }
    }

    /**
     * 
     * @param datatype
     * @return
     */
    public static String getDataTypeLength(String datatype, List<String> values, boolean hasCode) {
        if (datatype.equalsIgnoreCase("integer")) {
            return hasCode ? getDataTypeLength(values) : 10 + "";
        } else if (datatype.equalsIgnoreCase("float")) {
            return hasCode ? getDataTypeLength(values) : "" + 32;
        } else if (datatype.equalsIgnoreCase("text")) {
            return textLength + "";
        }
        return new String();
    }

    public static String getSignificantDigits(String datatype, List<String> values, boolean hasCode) {
        if (datatype.equalsIgnoreCase("float")) {
            return hasCode ? getSignificantDigits(values) : 6 + "";
        }
        return new String();
    }

    public static String getDataTypeLength(List<String> values) {
        int len = 0;
        for (String value : values) {
            len = Math.max(len, value.length());
        }
        return len + "";
    }

    public static String getSignificantDigits(List<String> values) {
        int d = 0;
        for (String v : values) {
            if (v != null && v.length() > 0) {
                d = Math.max(d, BigDecimal.valueOf(Double.parseDouble(v)).scale());
            }
        }
        return d + "";
    }

    public static String getItemQuestionText(String header, String left, String right) {
        String t = header != null && header.length() > 0 ? header : "";
        if (left != null && left.length() > 0) {
            t += t.length() > 0 ? "  - " + left : left;
        }
        if (right != null && right.length() > 0) {
            t += t.length() > 0 ? "  - " + right : right;
        }
        return t;
    }

    public static List<RangeCheckBean> getItemRangeCheck(String func, String constraint, String errorMessage) {
        List<RangeCheckBean> rcs = new ArrayList<RangeCheckBean>();
        final String[] odmComparator = { "LT", "LE", "GT", "GE", "EQ", "NE", "IN", "NOTIN" };
        String[] s = func.split("\\(");
        RangeCheckBean rc = new RangeCheckBean();
        if (s[0].equalsIgnoreCase("range")) {
            String[] values = s[1].split("\\,");
            String smaller = values[0];
            String larger = values[1].trim();
            larger = larger.substring(0, larger.length() - 1);
            rc.setComparator("GE");
            rc.setSoftHard(constraint);
            rc.getErrorMessage().setText(errorMessage);
            rc.setCheckValue(smaller);
            rcs.add(rc);
            rc = new RangeCheckBean();
            rc.setComparator("LE");
            rc.setSoftHard(constraint);
            rc.getErrorMessage().setText(errorMessage);
            rc.setCheckValue(larger);
            rcs.add(rc);
        } else {
            rc = new RangeCheckBean();
            String value = s[1].trim();
            value = value.substring(0, value.length() - 1);
            if (s[0].equalsIgnoreCase("gt")) {
                rc.setComparator("GT");
            } else if (s[0].equalsIgnoreCase("lt")) {
                rc.setComparator("LT");
            } else if (s[0].equalsIgnoreCase("gte")) {
                rc.setComparator("GE");
            } else if (s[0].equalsIgnoreCase("lte")) {
                rc.setComparator("LE");
            } else if (s[0].equalsIgnoreCase("ne")) {
                rc.setComparator("NE");
            } else if (s[0].equalsIgnoreCase("eq")) {
                rc.setComparator("EQ");
            }
            rc.setSoftHard(constraint);
            rc.getErrorMessage().setText(errorMessage);
            rc.setCheckValue(value);
            rcs.add(rc);
        }
        return rcs;
    }

    public static boolean needCodeList(int id, int datatypeid) {
        if ((id == 5 || id == 6 || id == 7) && (datatypeid == 5 || datatypeid == 6 || datatypeid == 7)) {
            return true;
        }
        return false;
    }

    public static HashMap<String, String> parseCode(String text, String value) {
        HashMap<String, String> code = new HashMap<String, String>();
        String[] keys = text.replaceAll("\\\\,", "##").split(",");
        String[] values = value.replaceAll("\\\\,", "##").split(",");
        if (values == null) {
            return code;
        }
        if (keys == null) {
            keys = new String[0];
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null || values[i].length() <= 0) {
                continue;
            }
            String v = values[i].trim().replaceAll("##", ",");
            if (keys.length <= i || keys[i] == null || keys[i].length() <= 0) {
                code.put(v, v);
            } else {
                code.put(keys[i].trim().replaceAll("##", ","), v);
            }
        }
        return code;
    }

    public void setOdmStudy(OdmStudyBean odmstudy) {
        this.odmStudy = odmstudy;
    }

    public OdmStudyBean getOdmStudy() {
        return this.odmStudy;
    }

    public static void setTextLength(int len) {
        textLength = len;
    }

    public static int getTextLength() {
        return textLength;
    }
}
