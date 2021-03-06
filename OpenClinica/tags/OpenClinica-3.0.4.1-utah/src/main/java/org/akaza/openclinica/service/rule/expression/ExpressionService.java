/*
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research
 */
package org.akaza.openclinica.service.rule.expression;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.Utils;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemGroupDAO;
import org.akaza.openclinica.dao.submit.ItemGroupMetadataDAO;
import org.akaza.openclinica.domain.rule.expression.ExpressionObjectWrapper;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.expressionTree.ExpressionTreeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

public class ExpressionService {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final String SEPERATOR = ".";
    private final String ESCAPED_SEPERATOR = "\\.";
    private final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN = "[A-Z_0-9]+|[A-Z_0-9]+\\[(ALL|[1-9]\\d*)\\]$";
    private final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_NO_ALL = "[A-Z_0-9]+|[A-Z_0-9]+\\[[1-9]\\d*\\]$";
    private final String STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_ORDINAL = "[A-Z_0-9]+\\[(ALL|[1-9]\\d*)\\]$";
    private final String PRE = "[A-Z_0-9]+\\[";
    private final String POST = "\\]";
    private final String CRF_OID_OR_ITEM_DATA_PATTERN = "[A-Z_0-9]+";
    private final String BRACKETS_AND_CONTENTS = "\\[(ALL|[1-9]\\d*)\\]";
    private final String ALL_IN_BRACKETS = "ALL";
    private final String OPENNIG_BRACKET = "[";
    private final String CLOSING_BRACKET = "]";

    DataSource ds;
    Pattern[] pattern;
    Pattern[] rulePattern;
    ExpressionObjectWrapper expressionWrapper;

    private ItemDAO itemDao;
    private StudyEventDefinitionDAO studyEventDefinitionDao;
    private EventDefinitionCRFDAO eventDefinitionCRFDao;
    private ItemGroupMetadataDAO itemGroupMetadataDao;
    private EventCRFDAO eventCRFDao;
    private ItemGroupDAO itemGroupDao;
    private CRFDAO crfDao;
    private CRFVersionDAO crfVersionDao;
    private ItemDataDAO itemDataDao;
    private StudyEventDAO studyEventDao;

    /*
     * The variables below are used as a small Cache so that we don't go to the
     * database every time we want to get an Object by it's OID. This is a very
     * stripped down cache which will help performance in a single
     * request/response cycle.
     */
    private HashMap<String, StudyEventDefinitionBean> studyEventDefinitions;
    private HashMap<String, ItemGroupBean> itemGroups;
    private HashMap<String, ItemBean> items;

    public ExpressionService(DataSource ds) {
        init(ds, null);
    }

    public ExpressionService(ExpressionObjectWrapper expressionWrapper) {
        init(expressionWrapper.getDs(), expressionWrapper);
    }

    private void init(DataSource ds, ExpressionObjectWrapper expressionWrapper) {
        pattern = new Pattern[4];
        pattern[3] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN); // STUDY_EVENT_DEFINITION_OID
        // +
        // ordinal
        pattern[2] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // CRF_OID
        // or
        // CRF_VERSION_OID
        pattern[1] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN); // ITEM_GROUP_DATA_OID
        // +
        // ordinal
        pattern[0] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // ITEM_DATA_OID

        // [ALL] ordinals are not accepted in Rule Expressions
        rulePattern = new Pattern[4];
        rulePattern[3] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_NO_ALL); // STUDY_EVENT_DEFINITION_OID
        // +
        // ordinal
        rulePattern[2] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // CRF_OID
        // or
        // CRF_VERSION_OID
        rulePattern[1] = Pattern.compile(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_NO_ALL); // ITEM_GROUP_DATA_OID
        // +
        // ordinal
        rulePattern[0] = Pattern.compile(CRF_OID_OR_ITEM_DATA_PATTERN); // ITEM_DATA_OID

        this.studyEventDefinitions = new HashMap<String, StudyEventDefinitionBean>();
        this.itemGroups = new HashMap<String, ItemGroupBean>();
        this.items = new HashMap<String, ItemBean>();

        this.ds = ds;
        this.expressionWrapper = expressionWrapper;
    }

    public boolean ruleSetExpressionChecker(String expression) {
        int k = expression.split(ESCAPED_SEPERATOR).length;
        if (checkSyntax(expression) && expression.split(ESCAPED_SEPERATOR).length == 4) {
            isExpressionValid(expression);
        } else {
            throw new OpenClinicaSystemException("OCRERR_0032");
        }
        return true;
    }

    public String ifValueIsDate(ItemBean itemBean, String value) {
        String theFinalValue = value;

        if (value != null && itemBean.getDataType() == ItemDataType.DATE) {
            value = Utils.convertedItemDateValue(value, ResourceBundleProvider.getFormatBundle().getString("date_format_string"), "MM/dd/yyyy");
            theFinalValue = ExpressionTreeHelper.isValidDateMMddyyyy(value);
        }
        return theFinalValue;
    }

    public String getValueFromDb(String expression,List<ItemDataBean> itemData) throws OpenClinicaSystemException {
        if (isExpressionPartial(expression)) {
            throw new OpenClinicaSystemException("getValueFromDb:We cannot get the Value of a PARTIAL expression : " + expression);
        }
        try {
            Integer index =
                getItemGroupOidOrdinalFromExpression(expression).equals("") ? 0 : Integer.valueOf(getItemGroupOidOrdinalFromExpression(expression)) - 1;
            ItemDataBean itemDataBean = itemData.get(index);
            ItemBean itemBean = (ItemBean) getItemDao().findByPK(itemDataBean.getItemId());

            String value = itemData.get(index).getValue();
            value = ifValueIsDate(itemBean, value);

            return value;
        } catch (NullPointerException npe) {
            logger.error("NullPointerException was thrown ");
            return null;
        } catch (IndexOutOfBoundsException ioobe) {
            logger.error("IndexOutOfBoundsException was thrown ");
            return null;
        }

    }

    public String getValueFromDbb(String expression) throws OpenClinicaSystemException {
        if (isExpressionPartial(expression)) {
            throw new OpenClinicaSystemException("getValueFromDb:We cannot get the Value of a PARTIAL expression : " + expression);
        }
        try {
            // Get the studyEventId from RuleSet Target so we can know which
            // StudySubject we are dealing with.
        	String ruleSetExpression = expressionWrapper.getRuleSet().getTarget().getValue();
            String ruleSetExpressionStudyEventId = getStudyEventDefinitionOidOrdinalFromExpression(ruleSetExpression);
            StudyEventBean studyEvent = (StudyEventBean) getStudyEventDao().findByPK(Integer.valueOf(ruleSetExpressionStudyEventId));

            // Prepare Method arguments
            String studyEventDefinitionOid = getStudyEventDefinitionOidFromExpression(expression);
            String crfOrCrfVersionOid = getCrfOidFromExpression(expression);
            String studyEventDefinitionOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
            studyEventDefinitionOrdinal = studyEventDefinitionOrdinal.equals("") ? "1" : studyEventDefinitionOrdinal;
            String studySubjectId = String.valueOf(studyEvent.getStudySubjectId());

            logger.debug("ruleSet studyEventId  {} , studyEventDefinitionOid {} , crfOrCrfVersionOid {} , studyEventDefinitionOrdinal {} ,studySubjectId {}",
                    new Object[] { studyEvent.getId(), studyEventDefinitionOid, crfOrCrfVersionOid, studyEventDefinitionOrdinal, studySubjectId });

            StudyEventBean studyEventofThisExpression =
                getStudyEventDao().findAllByStudyEventDefinitionAndCrfOidsAndOrdinal(studyEventDefinitionOid, crfOrCrfVersionOid, studyEventDefinitionOrdinal,
                        studySubjectId);

            logger.debug("studyEvent : {} , itemOid {} , itemGroupOid {}", new Object[] { studyEventofThisExpression.getId(),
                getItemOidFromExpression(expression), getItemGroupOidFromExpression(expression) });

            List<ItemDataBean> itemData =
                getItemDataDao().findByStudyEventAndOids(Integer.valueOf(studyEventofThisExpression.getId()), getItemOidFromExpression(expression),
                        getItemGroupOidFromExpression(expression));
            
            expression = fixGroupOrdinal(expression,ruleSetExpression,itemData,expressionWrapper.getEventCrf());
            
            Integer index =
                getItemGroupOidOrdinalFromExpression(expression).equals("") ? 0 : Integer.valueOf(getItemGroupOidOrdinalFromExpression(expression)) - 1;
            

            ItemDataBean itemDataBean = itemData.get(index);
            ItemBean itemBean = (ItemBean) getItemDao().findByPK(itemDataBean.getItemId());
            String value = itemData.get(index).getValue();
            value = ifValueIsDate(itemBean, value);

            return value;
        } catch (Exception e) {
            return null;
        }
    }

    public ItemDataBean getItemDataBeanFromDb(String expression) throws OpenClinicaSystemException {
        if (isExpressionPartial(expression)) {
            throw new OpenClinicaSystemException("getItemDataBeanFromDb:We cannot get the ItemData of a PARTIAL expression : " + expression);
        }
        String studyEventId = getStudyEventDefinitionOidOrdinalFromExpression(expression);
        Integer index = getItemGroupOidOrdinalFromExpression(expression).equals("") ? 0 : Integer.valueOf(getItemGroupOidOrdinalFromExpression(expression)) - 1;
        List<ItemDataBean> itemData =
            getItemDataDao().findByStudyEventAndOids(Integer.valueOf(studyEventId), getItemOidFromExpression(expression),
                    getItemGroupOidFromExpression(expression));
        return itemData.get(index);
    }

    public String getValueFromForm(String expression) {
        String result = null;
        HashMap<String, String> formValues = expressionWrapper.getItemsAndTheirValues();
        if (formValues != null && !formValues.isEmpty()) {
            String withGroup = getItemGroupPLusItem(expression);
            String withoutGroup = getItemOidFromExpression(expression);
            result = formValues.containsKey(withGroup) ? formValues.get(withGroup) : formValues.containsKey(withoutGroup) ? formValues.get(withoutGroup) : null;
        } else {
            logger.warn("The HashMap that stores form values was null, Better this be a Bulk operation");
        }
        return result;
    }

    public String evaluateExpression(String expression) throws OpenClinicaSystemException {
        String value = null;
        if (expressionWrapper.getRuleSet() != null) {
            if (isExpressionPartial(expression)) {
                String fullExpression = constructFullExpressionIfPartialProvided(expression, expressionWrapper.getRuleSet().getTarget().getValue());
                List<ItemDataBean> itemDatas = getItemDatas(fullExpression);
                fullExpression = fixGroupOrdinal(fullExpression,expressionWrapper.getRuleSet().getTarget().getValue(),itemDatas,expressionWrapper.getEventCrf());
                if (checkSyntax(fullExpression)) {
                	
                    String valueFromForm = getValueFromForm(fullExpression);
                    String valueFromDb = getValueFromDb(fullExpression,itemDatas);
                    logger.debug("valueFromForm : {} , valueFromDb : {}", valueFromForm, valueFromDb);
                    if (valueFromForm == null && valueFromDb == null) {
                        throw new OpenClinicaSystemException("OCRERR_0017", new Object[] { fullExpression,
                            expressionWrapper.getRuleSet().getTarget().getValue() });
                    }
                    /*
                     * if (valueFromForm != null) { // TODO: Do this if type a
                     * date String dateFormat =
                     * ResourceBundleProvider.getFormatBundle().getString("date_format_string");
                     * String dateRegexp =
                     * ResourceBundleProvider.getFormatBundle().getString("date_regexp");
                     * valueFromForm =
                     * ExpressionTreeHelper.isValidDate(valueFromForm,
                     * dateFormat, dateRegexp); }
                     */
                    value = valueFromForm == null ? valueFromDb : valueFromForm;
                }
            } else {
                // So Expression is not Partial
                if (checkSyntax(expression)) {
                    String valueFromDb = getValueFromDbb(expression);
                    if (valueFromDb == null) {
                        throw new OpenClinicaSystemException("OCRERR_0018", new Object[] { expression });
                    }
                    logger.debug("valueFromDb : {}", valueFromDb);
                    value = valueFromDb;
                }
            }
        }
        return value;
    }
    
    private List<ItemDataBean> getItemDatas(String expression){
    	
    	String studyEventId = getStudyEventDefinitionOidOrdinalFromExpression(expression);
        List<ItemDataBean> itemData =
            getItemDataDao().findByStudyEventAndOids(Integer.valueOf(studyEventId), getItemOidFromExpression(expression),
                    getItemGroupOidFromExpression(expression));
        return itemData;
    }
    
    private String fixGroupOrdinal(String ruleExpression,String targetExpression,List<ItemDataBean> itemData,EventCRFBean eventCrf){
    	
    	String returnedRuleExpression = ruleExpression;
    	
    	if (getItemGroupOid(ruleExpression).equals(getItemGroupOid(targetExpression))) {
    		if (getGroupOrdninalCurated(ruleExpression).equals("") && !getGroupOrdninalCurated(targetExpression).equals("")) {
    			returnedRuleExpression =  replaceGroupOidOrdinalInExpression(ruleExpression, Integer.valueOf(getGroupOrdninalCurated(targetExpression)));
			}
		}else{
			 EventCRFBean theEventCrfBean = null;
			 if (eventCrf != null) {
				 theEventCrfBean = eventCrf;
			 }else if (!itemData.isEmpty()) {
				 theEventCrfBean = (EventCRFBean)getEventCRFDao().findByPK(itemData.get(0).getEventCRFId());
			 }else{
				 return returnedRuleExpression;
			 }
			 
			 Integer itemId = itemData.isEmpty() ? getItemDao().findByOid(getItemOid(ruleExpression)).get(0).getId() : itemData.get(0).getItemId();			 
			 
	         ItemGroupMetadataBean itemGroupMetadataBean =
	             (ItemGroupMetadataBean) getItemGroupMetadataDao().findByItemAndCrfVersion(itemId, theEventCrfBean.getCRFVersionId());
	         if (isGroupRepeating(itemGroupMetadataBean) && getGroupOrdninalCurated(ruleExpression).equals("")){
	        	 returnedRuleExpression =  replaceGroupOidOrdinalInExpression(ruleExpression, Integer.valueOf(getGroupOrdninalCurated(targetExpression)));
	         }
			
		}
    	return returnedRuleExpression;
    }
    
    private Boolean isGroupRepeating(ItemGroupMetadataBean itemGroupMetadataBean) {
        return itemGroupMetadataBean.getRepeatNum() > 1 || itemGroupMetadataBean.getRepeatMax() > 1;
    }

    public boolean ruleExpressionChecker(String expression) {
        boolean result = false;
        boolean isRuleExpressionValid = false;
        if (expressionWrapper.getRuleSet() != null) {
            String fullExpression = constructFullExpressionIfPartialProvided(expression, expressionWrapper.getRuleSet().getTarget().getValue());

            if (isExpressionPartial(expression)) {
                isRuleExpressionValid = checkSyntax(fullExpression);
            } else {
                isRuleExpressionValid = checkRuleExpressionSyntax(fullExpression);
            }

            if (isRuleExpressionValid) {
                isExpressionValid(fullExpression);
                result = true;
            }
            
            String targetGroupOid = getItemGroupOid(expressionWrapper.getRuleSet().getTarget().getValue());
            String ruleGroupOid = getItemGroupOid(fullExpression);
            CRFVersionBean targetCrfVersion = getCRFVersionFromExpression(expressionWrapper.getRuleSet().getTarget().getValue());
            CRFVersionBean ruleCrfVersion= getCRFVersionFromExpression(fullExpression);
            Boolean isTargetGroupRepeating = targetCrfVersion == null ? getItemGroupDao().isItemGroupRepeatingBasedOnAllCrfVersions(targetGroupOid) : 
            	getItemGroupDao().isItemGroupRepeatingBasedOnCrfVersion(targetGroupOid, targetCrfVersion.getId());
            Boolean isRuleGroupRepeating = ruleCrfVersion == null ? getItemGroupDao().isItemGroupRepeatingBasedOnAllCrfVersions(ruleGroupOid) : 
            	getItemGroupDao().isItemGroupRepeatingBasedOnCrfVersion(ruleGroupOid, ruleCrfVersion.getId());
            if(!isTargetGroupRepeating && isRuleGroupRepeating){
            	String ordinal = getItemGroupOidOrdinalFromExpression(fullExpression);
            	if (ordinal.equals("") || ordinal.equals("ALL")) {
					result = false;
				}
            }
            

        } else {
            if (checkSyntax(expression) && getItemBeanFromExpression(expression) != null) {
                result = true;
            }
        }
        return result;
    }

    private Boolean isExpressionPartial(String expression) {
        String[] splitExpression = expression.split(ESCAPED_SEPERATOR);
        if (splitExpression.length == 4)
            return false;
        else
            return true;
    }

    private String constructFullExpressionIfPartialProvided(String expression, String ruleSetTargetExpression) {
        String[] splitExpression = expression.split(ESCAPED_SEPERATOR);
        switch (splitExpression.length) {
        case 1:
            return deContextualizeExpression(3, expression, ruleSetTargetExpression);
        case 2:
            return deContextualizeExpression(2, expression, ruleSetTargetExpression);
        case 3:
            return deContextualizeExpression(1, expression, ruleSetTargetExpression);
        case 4:
            return expression;
        default:
            throw new OpenClinicaSystemException("Full Expression cannot be constructed from provided expression : " + expression);
        }
    }

    private String deContextualizeExpression(int j, String ruleExpression, String ruleSetTargetExpression) {
        String ruleSetExpression = ruleSetTargetExpression;
        String[] splitRuleSetExpression = ruleSetExpression.split(ESCAPED_SEPERATOR);
        String buildExpression = "";

        for (int i = 0; i < j; i++) {
            buildExpression = buildExpression + splitRuleSetExpression[i] + SEPERATOR;
        }
        return buildExpression + ruleExpression;
    }

    private String getItemOidFromExpression(String expression) throws OpenClinicaSystemException {
        return getOidFromExpression(expression, 0, 0);
    }

    private String getItemGroupOidFromExpression(String expression) throws OpenClinicaSystemException {
        return getOidFromExpression(expression, 1, 1).replaceAll(BRACKETS_AND_CONTENTS, "");
    }

    private String getItemGroupOidWithOrdinalFromExpression(String expression) throws OpenClinicaSystemException {
        return getOidFromExpression(expression, 1, 1);
    }

    private String getItemGroupOidOrdinalFromExpression(String expression) throws OpenClinicaSystemException {
        String itemGroupOid = getOidFromExpression(expression, 1, 1);
        String itemGroupOidOrdinal = "";
        if (itemGroupOid.matches(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_ORDINAL)) {
            itemGroupOidOrdinal = itemGroupOid.trim().replaceAll(PRE, "").trim().replaceAll(POST, "");
        }
        return itemGroupOidOrdinal;
    }

    private String getItemGroupPLusItem(String expression) throws OpenClinicaSystemException {
        return getItemGroupOidWithOrdinalFromExpression(expression) + SEPERATOR + getItemOidFromExpression(expression);
    }

    private String getCrfOidFromExpression(String expression) throws OpenClinicaSystemException {
        return getOidFromExpression(expression, 2, 2);
    }

    private String getStudyEventDefinitionOidFromExpression(String expression) throws OpenClinicaSystemException {
        return getOidFromExpression(expression, 3, 3).replaceAll(BRACKETS_AND_CONTENTS, "");
    }

    private String getStudyEventDefinitionOidWithOrdinalFromExpression(String expression) throws OpenClinicaSystemException {
        return getOidFromExpression(expression, 3, 3);
    }

    public String getItemGroupNameAndOrdinal(String expression) {
        return getItemGroupExpression(expression).getName() + " " + OPENNIG_BRACKET + getItemGroupOidOrdinalFromExpression(expression) + CLOSING_BRACKET;
    }

    public String getStudyEventDefinitionOidOrdinalFromExpression(String expression) throws OpenClinicaSystemException {
        String studyEventDefinitionOid = getOidFromExpression(expression, 3, 3);
        String studyEventDefinitionOidOrdinal = "";
        if (studyEventDefinitionOid.matches(STUDY_EVENT_DEFINITION_OR_ITEM_GROUP_PATTERN_WITH_ORDINAL)) {
            studyEventDefinitionOidOrdinal = studyEventDefinitionOid.trim().replaceAll(PRE, "").trim().replaceAll(POST, "");
        }
        return studyEventDefinitionOidOrdinal;
    }

    /**
     * Use this method to create 1ItemOID or ItemOID Used in Data Entry Rule
     * Execution
     * 
     * @param expression
     * @return GroupOrdinal + ItemOID
     */
    public String getGroupOrdninalConcatWithItemOid(String expression) {
        String ordinal = getGroupOrdninalCurated(expression);
        logger.debug(" orginigal expression {} , post getGroupOrdninalConcatWithItemOid : {} ", expression, ordinal + getItemOidFromExpression(expression));
        return ordinal + getItemOidFromExpression(expression);
    }

    public String getGroupOidWithItemOid(String expression) {
        return getItemGroupOidWithOrdinalFromExpression(expression) + SEPERATOR + getItemOidFromExpression(expression);
    }

    public String getItemOid(String expression) {
        return getItemOidFromExpression(expression);
    }

    public String getItemGroupOid(String expression) {
        return getItemGroupOidFromExpression(expression);
    }

    public String getCrfOid(String expression) {
        return getCrfOidFromExpression(expression);
    }

    public String getStudyEventDefenitionOid(String expression) {
        return getStudyEventDefinitionOidFromExpression(expression);
    }

    public String getGroupOrdninalCurated(String expression) {
        String originalOrdinal = getItemGroupOidOrdinalFromExpression(expression);
        String ordinal = originalOrdinal.equals(ALL_IN_BRACKETS) ? "" : originalOrdinal;
        return ordinal;
    }

    public String getStudyEventDefinitionOrdninalCurated(String expression) {
        String originalOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
        String ordinal = originalOrdinal.equals(ALL_IN_BRACKETS) ? "" : originalOrdinal;
        return ordinal;
    }

    public String getStudyEventDefenitionOrdninalCurated(String expression) {
        String originalOrdinal = getStudyEventDefinitionOidOrdinalFromExpression(expression);
        String ordinal = null;
        if (originalOrdinal.equals(ALL_IN_BRACKETS)) {
            throw new OpenClinicaSystemException("ALL not supported in the following instance");
        } else if (originalOrdinal.equals("")) {
            ordinal = "1";
        } else {
            ordinal = originalOrdinal;
        }
        return ordinal;
    }

    public String getGroupOidConcatWithItemOid(String expression) {
        String result = getItemGroupOidFromExpression(expression) + SEPERATOR + getItemOidFromExpression(expression);
        logger.debug("getGroupOidConcatWithItemOid returns : {} ", result);
        return result;
    }

    public String replaceGroupOidOrdinalInExpression(String expression, Integer ordinal) {
        String replacement = getStudyEventDefinitionOidWithOrdinalFromExpression(expression) + SEPERATOR + getCrfOidFromExpression(expression) + SEPERATOR;
        if (ordinal == null) {
            replacement += getItemGroupOidFromExpression(expression) + SEPERATOR + getItemOidFromExpression(expression);
        } else {
            replacement +=
                getItemGroupOidFromExpression(expression) + OPENNIG_BRACKET + ordinal + CLOSING_BRACKET + SEPERATOR + getItemOidFromExpression(expression);
        }
        logger.debug("Original Expression : {} , Rewritten as {} .", expression, replacement);
        return replacement;
    }

    public String replaceCRFOidInExpression(String expression, String replacementCrfOid) {
        return getStudyEventDefinitionOidWithOrdinalFromExpression(expression) + SEPERATOR + replacementCrfOid + SEPERATOR
            + getItemGroupOidWithOrdinalFromExpression(expression) + SEPERATOR + getItemOidFromExpression(expression);
    }

    public String getCustomExpressionUsedToCreateView(String expression, int sampleOrdinal) {
        return getStudyEventDefenitionOid(expression) + OPENNIG_BRACKET + sampleOrdinal + CLOSING_BRACKET + SEPERATOR + "XXX" + SEPERATOR
            + getGroupOidWithItemOid(expression);
    }

    public String replaceStudyEventDefinitionOIDWith(String expression, String replacement) {
        replacement = getStudyEventDefinitionOidFromExpression(expression) + OPENNIG_BRACKET + replacement + CLOSING_BRACKET;
        String studyEventDefinitionOID = getStudyEventDefinitionOidWithOrdinalFromExpression(expression);
        return expression.replace(studyEventDefinitionOID, replacement);
    }

    /*
     * public String replaceFirstIntegerBy(String expression, String
     * replacement) { return expression.trim().replaceFirst("\\[\\d+\\]", "[" +
     * replacement + "]"); }
     */

    private String getOidFromExpression(String expression, int patternIndex, int expressionIndex) throws OpenClinicaSystemException {
        String[] splitExpression = expression.split(ESCAPED_SEPERATOR);
        // int patternIndex = ?;
        if (!match(splitExpression[splitExpression.length - 1 - expressionIndex], pattern[patternIndex])) {
            throw new OpenClinicaSystemException("OCRERR_0019", new String[] { expression });
        }
        return splitExpression[splitExpression.length - 1 - expressionIndex];
    }

    public ItemBean getItemBeanFromExpression(String expression) {
        List<ItemBean> items = getItemDao().findByOid(getItemOidFromExpression(expression));
        return items.size() > 0 ? items.get(0) : null;
    }

    public StudyEventDefinitionBean getStudyEventDefinitionFromExpression(String expression) {
        return getStudyEventDefinitionFromExpression(expression, expressionWrapper.getStudyBean());
    }

    public StudyEventDefinitionBean getStudyEventDefinitionFromExpression(String expression, StudyBean study) {
        String studyEventDefinitionKey = getStudyEventDefinitionOidFromExpression(expression);
        logger.debug("Expression : {} , Study Event Definition OID {} , Study Bean {} ", new Object[] { expression, studyEventDefinitionKey, study.getId() });
        if (studyEventDefinitions.get(studyEventDefinitionKey) != null) {
            return studyEventDefinitions.get(studyEventDefinitionKey);
        } else {
            // temp fix
            int studyId = study.getParentStudyId() != 0 ? study.getParentStudyId() : study.getId();
            StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionDao().findByOidAndStudy(studyEventDefinitionKey, studyId, studyId);
            // another way to get at the problem which I fix in the
            // findByOidAndStudy method, tbh
            if (studyEventDefinition != null) {
                studyEventDefinitions.put(studyEventDefinitionKey, studyEventDefinition);
                return studyEventDefinition;
            } else {
                return null;
            }
        }
    }

    public ItemGroupBean getItemGroupExpression(String expression) {
        String itemGroupKey = getItemGroupOidFromExpression(expression);
        logger.debug("Expression : {} , ItemGroup OID : {} " + expression, itemGroupKey);
        if (itemGroups.get(itemGroupKey) != null) {
            return itemGroups.get(itemGroupKey);
        } else {
            ItemGroupBean itemGroup = getItemGroupDao().findByOid(itemGroupKey);
            if (itemGroup != null) {
                itemGroups.put(itemGroupKey, itemGroup);
                return itemGroup;
            } else {
                return null;
            }
        }
    }

    public ItemGroupBean getItemGroupExpression(String expression, CRFBean crf) {
        logger.debug("Expression : " + expression);
        logger.debug("Expression : " + getItemGroupOidFromExpression(expression));
        ItemGroupBean itemGroup = getItemGroupDao().findByOidAndCrf(getItemGroupOidFromExpression(expression), crf.getId());
        return itemGroup;
    }

    public ItemBean getItemExpression(String expression, ItemGroupBean itemGroup) {
        String itemKey = getItemOidFromExpression(expression);
        logger.debug("Expression : {} , Item OID : {}", expression, itemKey);
        if (items.containsKey(itemKey)) {
            return items.get(itemKey);
        } else {
            ItemBean item = getItemDao().findItemByGroupIdandItemOid(itemGroup.getId(), itemKey);
            if (item != null) {
                items.put(itemKey, item);
                return item;
            } else {
                return null;
            }
        }
    }

    public CRFBean getCRFFromExpression(String expression) {
        CRFBean crf;
        logger.info("Expression : " + expression);
        logger.info("Expression : " + getCrfOidFromExpression(expression));
        CRFVersionBean crfVersion = getCrfVersionDao().findByOid(getCrfOidFromExpression(expression));
        if (crfVersion != null) {
            int crfId = getCrfVersionDao().getCRFIdFromCRFVersionId(crfVersion.getId());
            crf = (CRFBean) getCrfDao().findByPK(crfId);
        } else {
            crf = getCrfDao().findByOid(getCrfOidFromExpression(expression));
        }
        return crf;
        // return crfVersions.size() > 0 ? crfVersions.get(0) : null;
    }

    public CRFVersionBean getCRFVersionFromExpression(String expression) {
        logger.info("Expression : " + expression);
        logger.info("Expression : " + getCrfOidFromExpression(expression));
        return getCrfVersionDao().findByOid(getCrfOidFromExpression(expression));
    }

    /**
     * Given a Complete Expression check business logic validity of each
     * component. Will throw OpenClinicaSystemException with correct
     * explanation. This might allow immediate communication of message to user .
     * 
     * @param expression
     */
    public void isExpressionValid(String expression) {
        StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionFromExpression(expression);
        CRFBean crf = getCRFFromExpression(expression);
        if (studyEventDefinition == null || crf == null)
            throw new OpenClinicaSystemException("OCRERR_0020");

        EventDefinitionCRFBean eventDefinitionCrf =
            getEventDefinitionCRFDao().findByStudyEventDefinitionIdAndCRFId(this.expressionWrapper.getStudyBean(), studyEventDefinition.getId(), crf.getId());
        if (eventDefinitionCrf == null || eventDefinitionCrf.getId() == 0 || eventDefinitionCrf.getStatus() != Status.AVAILABLE)
            throw new OpenClinicaSystemException("OCRERR_0021");

        ItemGroupBean itemGroup = getItemGroupExpression(expression, crf);
        if (itemGroup == null)
            throw new OpenClinicaSystemException("OCRERR_0022");

        ItemBean item = getItemExpression(expression, itemGroup);
        if (item == null)
            throw new OpenClinicaSystemException("OCRERR_0023");

        logger.debug("Study Event Definition ID : " + studyEventDefinition.getId());
        logger.debug("Crf ID : " + crf.getId());
        logger.debug("Event Definition CRF ID : " + eventDefinitionCrf.getId());
        logger.debug("Item ID : " + item.getId());
    }

    public EventDefinitionCRFBean getEventDefinitionCRF(String expression) {
        StudyEventDefinitionBean studyEventDefinition = getStudyEventDefinitionFromExpression(expression);
        CRFBean crf = getCRFFromExpression(expression);

        if (studyEventDefinition == null || crf == null)
            throw new OpenClinicaSystemException("OCRERR_0020");

        return getEventDefinitionCRFDao()
                .findByStudyEventDefinitionIdAndCRFId(this.expressionWrapper.getStudyBean(), studyEventDefinition.getId(), crf.getId());
    }

    public boolean checkSyntax(String expression) {
        if (expression.startsWith(SEPERATOR) || expression.endsWith(SEPERATOR)) {
            return false;
        }
        String[] splitExpression = expression.split(ESCAPED_SEPERATOR);
        int patternIndex = 0;
        for (int i = splitExpression.length - 1; i >= 0; i--) {
            if (!match(splitExpression[i], pattern[patternIndex++])) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRuleExpressionSyntax(String expression) {
        if (expression.startsWith(SEPERATOR) || expression.endsWith(SEPERATOR)) {
            return false;
        }
        String[] splitExpression = expression.split(ESCAPED_SEPERATOR);
        int patternIndex = 0;
        for (int i = splitExpression.length - 1; i >= 0; i--) {
            if (!match(splitExpression[i], rulePattern[patternIndex++])) {
                return false;
            }
        }
        return true;
    }

    private boolean match(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private ItemDAO getItemDao() {
        itemDao = this.itemDao != null ? itemDao : new ItemDAO(ds);
        return itemDao;
    }

    private ItemDataDAO getItemDataDao() {
        itemDataDao = this.itemDataDao != null ? itemDataDao : new ItemDataDAO(ds);
        return itemDataDao;
    }

    private CRFVersionDAO getCrfVersionDao() {
        crfVersionDao = this.crfVersionDao != null ? crfVersionDao : new CRFVersionDAO(ds);
        return crfVersionDao;
    }

    private CRFDAO getCrfDao() {
        crfDao = this.crfDao != null ? crfDao : new CRFDAO(ds);
        return crfDao;
    }

    private ItemGroupDAO getItemGroupDao() {
        itemGroupDao = this.itemGroupDao != null ? itemGroupDao : new ItemGroupDAO(ds);
        return itemGroupDao;
    }

    private EventDefinitionCRFDAO getEventDefinitionCRFDao() {
        eventDefinitionCRFDao = this.eventDefinitionCRFDao != null ? eventDefinitionCRFDao : new EventDefinitionCRFDAO(ds);
        return eventDefinitionCRFDao;
    }

    private StudyEventDefinitionDAO getStudyEventDefinitionDao() {
        studyEventDefinitionDao = this.studyEventDefinitionDao != null ? studyEventDefinitionDao : new StudyEventDefinitionDAO(ds);
        return studyEventDefinitionDao;
    }

    private StudyEventDAO getStudyEventDao() {
        studyEventDao = this.studyEventDao != null ? studyEventDao : new StudyEventDAO(ds);
        return studyEventDao;
    }
    
    private ItemGroupMetadataDAO getItemGroupMetadataDao() {
    	itemGroupMetadataDao = this.itemGroupMetadataDao != null ? itemGroupMetadataDao : new ItemGroupMetadataDAO(ds);
        return itemGroupMetadataDao;
    }
    
    private EventCRFDAO getEventCRFDao() {
    	eventCRFDao = this.eventCRFDao != null ? eventCRFDao : new EventCRFDAO(ds);
        return eventCRFDao;
    }

    public void setExpressionWrapper(ExpressionObjectWrapper expressionWrapper) {
        this.expressionWrapper = expressionWrapper;
    }

}
