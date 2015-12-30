DROP TRIGGER study_AUDIT_TRIGGER ON STUDY;
DROP FUNCTION study_AUDIT_SQL();


CREATE FUNCTION study_AUDIT_SQL() RETURNS OPAQUE AS '
DECLARE
	pk INTEGER;
BEGIN 
SELECT INTO PK NEXTVAL(''audit_log_sequence'');
INSERT INTO AUDIT_EVENT (AUDIT_ID, AUDIT_DATE, AUDIT_TABLE, 
				USER_ID, ENTITY_ID, REASON_FOR_CHANGE)
	VALUES
	(pk, now(), 
	''STUDY'', 
	NEW.UPDATE_ID,
	NEW.STUDY_ID,
	''UPDATE TRIGGER FIRED'');
IF OLD.NAME IS NOT NULL AND (OLD.NAME <> NEW.NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''NAME'', OLD.NAME, NEW.NAME);
	
END IF;
IF OLD.PARENT_STUDY_ID IS NOT NULL AND (OLD.PARENT_STUDY_ID <> NEW.PARENT_STUDY_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk, ''PARENT_STUDY_ID'', OLD.PARENT_STUDY_ID, NEW.PARENT_STUDY_ID);
	
END IF;
IF OLD.UNIQUE_IDENTIFIER IS NOT NULL AND (OLD.UNIQUE_IDENTIFIER <> NEW.UNIQUE_IDENTIFIER)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''UNIQUE_IDENTIFIER'', OLD.UNIQUE_IDENTIFIER, NEW.UNIQUE_IDENTIFIER);
	
END IF;
IF OLD.SUMMARY IS NOT NULL AND (OLD.SUMMARY <> NEW.SUMMARY)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''SUMMARY'', OLD.SUMMARY, NEW.SUMMARY);
	
END IF;
IF OLD.DATE_PLANNED_START IS NOT NULL AND (OLD.DATE_PLANNED_START <> NEW.DATE_PLANNED_START)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_PLANNED_START'', OLD.DATE_PLANNED_START, NEW.DATE_PLANNED_START);
	
END IF;
IF OLD.DATE_PLANNED_END IS NOT NULL AND (OLD.DATE_PLANNED_END <> NEW.DATE_PLANNED_END)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_PLANNED_END'', OLD.DATE_PLANNED_END, NEW.DATE_PLANNED_END);
	
END IF;
IF OLD.SECONDARY_IDENTIFIER IS NOT NULL AND (OLD.SECONDARY_IDENTIFIER <> NEW.SECONDARY_IDENTIFIER)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''SECONDARY_IDENTIFIER'', OLD.SECONDARY_IDENTIFIER, NEW.SECONDARY_IDENTIFIER);
	
END IF;
IF OLD.DATE_CREATED IS NOT NULL AND (OLD.DATE_CREATED <> NEW.DATE_CREATED)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_CREATED'', OLD.DATE_CREATED, NEW.DATE_CREATED);
	
END IF;
IF OLD.DATE_UPDATED IS NOT NULL AND (OLD.DATE_UPDATED <> NEW.DATE_UPDATED)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_UPDATED'', OLD.DATE_UPDATED, NEW.DATE_UPDATED);
	
END IF;
IF OLD.OWNER_ID IS NOT NULL AND (OLD.OWNER_ID <> NEW.OWNER_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''OWNER_ID'', OLD.OWNER_ID, NEW.OWNER_ID);
	
END IF;
IF OLD.UPDATE_ID IS NOT NULL AND (OLD.UPDATE_ID <> NEW.UPDATE_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''UPDATE_ID'', OLD.UPDATE_ID, NEW.UPDATE_ID);
	
END IF;
IF OLD.TYPE_ID IS NOT NULL AND (OLD.TYPE_ID <> NEW.TYPE_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''TYPE_ID'', OLD.TYPE_ID, NEW.TYPE_ID);
	
END IF;
IF OLD.STATUS_ID IS NOT NULL AND (OLD.STATUS_ID <> NEW.STATUS_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''STATUS_ID'', OLD.STATUS_ID, NEW.STATUS_ID);
	
END IF;
IF OLD.PRINCIPAL_INVESTIGATOR IS NOT NULL AND (OLD.PRINCIPAL_INVESTIGATOR <> NEW.PRINCIPAL_INVESTIGATOR)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PRINCIPAL_INVESTIGATOR'', OLD.PRINCIPAL_INVESTIGATOR, NEW.PRINCIPAL_INVESTIGATOR);
	
END IF;
IF OLD.FACILITY_NAME IS NOT NULL AND (OLD.FACILITY_NAME <> NEW.FACILITY_NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_NAME'', OLD.FACILITY_NAME, NEW.FACILITY_NAME);
	
END IF;
IF OLD.FACILITY_CITY IS NOT NULL AND (OLD.FACILITY_CITY <> NEW.FACILITY_CITY)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_CITY'', OLD.FACILITY_CITY, NEW.FACILITY_CITY);
	
END IF;
IF OLD.FACILITY_STATE IS NOT NULL AND (OLD.FACILITY_STATE <> NEW.FACILITY_STATE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_STATE'', OLD.FACILITY_STATE, NEW.FACILITY_STATE);
	
END IF;
IF OLD.FACILITY_ZIP IS NOT NULL AND (OLD.FACILITY_ZIP <> NEW.FACILITY_ZIP)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_ZIP'', OLD.FACILITY_ZIP, NEW.FACILITY_ZIP);
	
END IF;
IF OLD.FACILITY_COUNTRY IS NOT NULL AND (OLD.FACILITY_COUNTRY <> NEW.FACILITY_COUNTRY)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_COUNTRY'', OLD.FACILITY_COUNTRY, NEW.FACILITY_COUNTRY);
	
END IF;
IF OLD.FACILITY_RECRUITMENT_STATUS IS NOT NULL AND (OLD.FACILITY_RECRUITMENT_STATUS <> NEW.FACILITY_RECRUITMENT_STATUS)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_RECRUITMENT_STATUS'', OLD.FACILITY_RECRUITMENT_STATUS, NEW.FACILITY_RECRUITMENT_STATUS);
	
END IF;
IF OLD.FACILITY_CONTACT_NAME IS NOT NULL AND (OLD.FACILITY_CONTACT_NAME <> NEW.FACILITY_CONTACT_NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_CONTACT_NAME'', OLD.FACILITY_CONTACT_NAME, NEW.FACILITY_CONTACT_NAME);
	
END IF;
IF OLD.FACILITY_CONTACT_DEGREE IS NOT NULL AND (OLD.FACILITY_CONTACT_DEGREE <> NEW.FACILITY_CONTACT_DEGREE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_CONTACT_DEGREE'', OLD.FACILITY_CONTACT_DEGREE, NEW.FACILITY_CONTACT_DEGREE);
	
END IF;
IF OLD.FACILITY_CONTACT_PHONE IS NOT NULL AND (OLD.FACILITY_CONTACT_PHONE <> NEW.FACILITY_CONTACT_PHONE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_CONTACT_PHONE'', OLD.FACILITY_CONTACT_PHONE, NEW.FACILITY_CONTACT_PHONE);
	
END IF;
IF OLD.FACILITY_CONTACT_EMAIL IS NOT NULL AND (OLD.FACILITY_CONTACT_EMAIL <> NEW.FACILITY_CONTACT_EMAIL)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FACILITY_CONTACT_EMAIL'', OLD.FACILITY_CONTACT_EMAIL, NEW.FACILITY_CONTACT_EMAIL);
	
END IF;
IF OLD.PROTOCOL_TYPE IS NOT NULL AND (OLD.PROTOCOL_TYPE <> NEW.PROTOCOL_TYPE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PROTOCOL_TYPE'', OLD.PROTOCOL_TYPE, NEW.PROTOCOL_TYPE);
	
END IF;
IF OLD.PROTOCOL_DATE_VERIFICATION IS NOT NULL AND (OLD.PROTOCOL_DATE_VERIFICATION <> NEW.PROTOCOL_DATE_VERIFICATION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PROTOCOL_DATE_VERIFICATION'', OLD.PROTOCOL_DATE_VERIFICATION, NEW.PROTOCOL_DATE_VERIFICATION);
	
END IF;
IF OLD.PHASE IS NOT NULL AND (OLD.PHASE <> NEW.PHASE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PHASE'', OLD.PHASE, NEW.PHASE);
	
END IF;
IF OLD.EXPECTED_TOTAL_ENROLLMENT IS NOT NULL AND (OLD.EXPECTED_TOTAL_ENROLLMENT <> NEW.EXPECTED_TOTAL_ENROLLMENT)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''EXPECTED_TOTAL_ENROLLMENT'', OLD.EXPECTED_TOTAL_ENROLLMENT, NEW.EXPECTED_TOTAL_ENROLLMENT);
	
END IF;
IF OLD.SPONSOR IS NOT NULL AND (OLD.SPONSOR <> NEW.SPONSOR)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''SPONSOR'', OLD.SPONSOR, NEW.SPONSOR);
	
END IF;
IF OLD.COLLABORATORS IS NOT NULL AND (OLD.COLLABORATORS <> NEW.COLLABORATORS)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''COLLABORATORS'', OLD.COLLABORATORS, NEW.COLLABORATORS);
	
END IF;
IF OLD.MEDLINE_IDENTIFIER IS NOT NULL AND (OLD.MEDLINE_IDENTIFIER <> NEW.MEDLINE_IDENTIFIER)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''MEDLINE_IDENTIFIER'', OLD.MEDLINE_IDENTIFIER, NEW.MEDLINE_IDENTIFIER);
	
END IF;
IF OLD.URL IS NOT NULL AND (OLD.URL <> NEW.URL)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''URL'', OLD.URL, NEW.URL);
	
END IF;
IF OLD.URL_DESCRIPTION IS NOT NULL AND (OLD.URL_DESCRIPTION <> NEW.URL_DESCRIPTION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''URL_DESCRIPTION'', OLD.URL_DESCRIPTION, NEW.URL_DESCRIPTION);
	
END IF;
IF OLD.CONDITIONS IS NOT NULL AND (OLD.CONDITIONS <> NEW.CONDITIONS)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''CONDITIONS'', OLD.CONDITIONS, NEW.CONDITIONS);
	
END IF;
IF OLD.KEYWORDS IS NOT NULL AND (OLD.KEYWORDS <> NEW.KEYWORDS)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''KEYWORDS'', OLD.KEYWORDS, NEW.KEYWORDS);
	
END IF;
IF OLD.ELIGIBILITY IS NOT NULL AND (OLD.ELIGIBILITY <> NEW.ELIGIBILITY)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''ELIGIBILITY'', OLD.ELIGIBILITY, NEW.ELIGIBILITY);
	
END IF;
IF OLD.GENDER IS NOT NULL AND (OLD.GENDER <> NEW.GENDER)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''GENDER'', OLD.GENDER, NEW.GENDER);
	
END IF;
IF OLD.AGE_MAX IS NOT NULL AND (OLD.AGE_MAX <> NEW.AGE_MAX)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''AGE_MAX'', OLD.AGE_MAX, NEW.AGE_MAX);
	
END IF;
IF OLD.AGE_MIN IS NOT NULL AND (OLD.AGE_MIN <> NEW.AGE_MIN)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''AGE_MIN'', OLD.AGE_MIN, NEW.AGE_MIN);
	
END IF;
IF OLD.HEALTHY_VOLUNTEER_ACCEPTED IS NOT NULL AND (OLD.HEALTHY_VOLUNTEER_ACCEPTED <> NEW.HEALTHY_VOLUNTEER_ACCEPTED)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''HEALTHY_VOLUNTEER_ACCEPTED'', OLD.HEALTHY_VOLUNTEER_ACCEPTED, NEW.HEALTHY_VOLUNTEER_ACCEPTED);
	
END IF;
IF OLD.PURPOSE IS NOT NULL AND (OLD.PURPOSE <> NEW.PURPOSE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PURPOSE'', OLD.PURPOSE, NEW.PURPOSE);
	
END IF;
IF OLD.ALLOCATION IS NOT NULL AND (OLD.ALLOCATION <> NEW.ALLOCATION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''ALLOCATION'', OLD.ALLOCATION, NEW.ALLOCATION);
	
END IF;
IF OLD.MASKING IS NOT NULL AND (OLD.MASKING <> NEW.MASKING)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''MASKING'', OLD.MASKING, NEW.MASKING);
	
END IF;
IF OLD.CONTROL IS NOT NULL AND (OLD.CONTROL <> NEW.CONTROL)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''CONTROL'', OLD.CONTROL, NEW.CONTROL);
	
END IF;
IF OLD.ASSIGNMENT IS NOT NULL AND (OLD.ASSIGNMENT <> NEW.ASSIGNMENT)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''ASSIGNMENT'', OLD.ASSIGNMENT, NEW.ASSIGNMENT);
	
END IF;
IF OLD.ENDPOINT IS NOT NULL AND (OLD.ENDPOINT <> NEW.ENDPOINT)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''ENDPOINT'', OLD.ENDPOINT, NEW.ENDPOINT);
	
END IF;
IF OLD.INTERVENTIONS IS NOT NULL AND (OLD.INTERVENTIONS <> NEW.INTERVENTIONS)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''INTERVENTIONS'', OLD.INTERVENTIONS, NEW.INTERVENTIONS);
	
END IF;
IF OLD.DURATION IS NOT NULL AND (OLD.DURATION <> NEW.DURATION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DURATION'', OLD.DURATION, NEW.DURATION);
	
END IF;
IF OLD.SELECTION IS NOT NULL AND (OLD.SELECTION <> NEW.SELECTION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''SELECTION'', OLD.SELECTION, NEW.SELECTION);
	
END IF;
IF OLD.TIMING IS NOT NULL AND (OLD.TIMING <> NEW.TIMING)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''TIMING'', OLD.TIMING, NEW.TIMING);
	
END IF;
return null;
END;
' LANGUAGE 'plpgsql';


CREATE TRIGGER STUDY_AUDIT_TRIGGER
AFTER UPDATE
ON STUDY
FOR EACH ROW
EXECUTE PROCEDURE study_audit_sql();