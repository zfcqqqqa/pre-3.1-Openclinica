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
import org.akaza.openclinica.bean.odmbeans.MetaDataVersionProtocolBean;
import org.akaza.openclinica.bean.odmbeans.OdmStudyBean;

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

/**
 * Populate metadata for a ODM XML file. It supports:
 * <ul>
 * <li>ODM XML file contains only one ODM Study element.<br>
 * In this case, Include element references only to external MetadataVersion.
 * </li>
 * <li>ODM XML file contains multiple Study elements - one parent study and its
 * site(s). <br>
 * In this case, Include element can reference to both internal and external
 * MetadataVersion. ODM fields in dataset table are for the study. For site(s),
 * siteOID will be appended automatically. </li>
 * </ul>
 * 
 * @author ywang (May, 2009)
 */

public class MetaDataCollector extends OdmDataCollector {
    private LinkedHashMap<String, OdmStudyBean> odmStudyMap;
    private static int textLength = 4000;

    // protected final Logger logger =
    // LoggerFactory.getLogger(getClass().getName());

    public MetaDataCollector(DataSource ds, StudyBean study) {
        super(ds, study);
        odmStudyMap = new LinkedHashMap<String, OdmStudyBean>();
    }

    public MetaDataCollector(DataSource ds, DatasetBean dataset, StudyBean currentStudy) {
        super(ds, dataset, currentStudy);
        odmStudyMap = new LinkedHashMap<String, OdmStudyBean>();
    }

    public MetaDataCollector() {
    }

    @Override
    public void collectFileData() {
        this.collectOdmRoot();
        this.collectMetadataUnitMap();
    }

    public void collectMetadataUnitMap() {
        Iterator<OdmStudyBase> it = this.getStudyBaseMap().values().iterator();
        MetaDataVersionProtocolBean protocol = new MetaDataVersionProtocolBean();
        while (it.hasNext()) {
            OdmStudyBase u = it.next();
            StudyBean study = u.getStudy();
            MetadataUnit meta = new MetadataUnit(this.ds, this.dataset, this.getOdmbean(), study, this.getCategory());
            meta.collectOdmStudy();
            if (this.getCategory() == 1) {
                if (study.isSite(study.getParentStudyId())) {
                    MetaDataVersionProtocolBean p = meta.getOdmStudy().getMetaDataVersion().getProtocol();
                    if (p != null && p.getStudyEventRefs().size() > 0) {
                    } else {
                        logger.error("site " + study.getName() + " will be assigned protocol with StudyEventRefs size=" + protocol.getStudyEventRefs().size());
                        meta.getOdmStudy().getMetaDataVersion().setProtocol(protocol);
                    }
                } else {
                    protocol = meta.getOdmStudy().getMetaDataVersion().getProtocol();

                }
            }
            odmStudyMap.put(u.getStudy().getOid(), meta.getOdmStudy());
        }
    }

    public LinkedHashMap<String, OdmStudyBean> getOdmStudyMap() {
        return odmStudyMap;
    }

    public void setOdmStudyMap(LinkedHashMap<String, OdmStudyBean> odmStudyMap) {
        this.odmStudyMap = odmStudyMap;
    }

    public static void setTextLength(int len) {
        textLength = len;
    }

    public static int getTextLength() {
        return textLength;
    }
}
