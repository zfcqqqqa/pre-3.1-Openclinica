DROP TRIGGER useraccount_AUDIT_TRIGGER ON USER_ACCOUNT;
DROP FUNCTION useraccount_AUDIT_SQL();


CREATE FUNCTION useraccount_AUDIT_SQL() RETURNS OPAQUE AS '
DECLARE
	pk INTEGER;
BEGIN 
SELECT INTO PK NEXTVAL(''audit_log_sequence'');

INSERT INTO AUDIT_EVENT (AUDIT_ID, AUDIT_DATE, AUDIT_TABLE, 
				USER_ID, ENTITY_ID, REASON_FOR_CHANGE)
	VALUES
	(pk, now(), 
	''USER_ACCOUNT'', 
	NEW.UPDATE_ID,
	NEW.USER_ID,
	''UPDATE TRIGGER FIRED'');

IF OLD.USER_NAME IS NOT NULL AND (OLD.USER_NAME <> NEW.USER_NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''USER_NAME'', OLD.USER_NAME, NEW.USER_NAME);
	
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
IF OLD.PASSWD IS NOT NULL AND (OLD.PASSWD <> NEW.PASSWD)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PASSWD'', OLD.PASSWD, NEW.PASSWD);
	
END IF;
IF OLD.FIRST_NAME IS NOT NULL AND (OLD.FIRST_NAME <> NEW.FIRST_NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''FIRST_NAME'', OLD.FIRST_NAME, NEW.FIRST_NAME);
	
END IF;
IF OLD.LAST_NAME IS NOT NULL AND (OLD.LAST_NAME <> NEW.LAST_NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''LAST_NAME'', OLD.LAST_NAME, NEW.LAST_NAME);
	
END IF;
IF OLD.EMAIL IS NOT NULL AND (OLD.EMAIL <> NEW.EMAIL)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''EMAIL'', OLD.EMAIL, NEW.EMAIL);
	
END IF;
IF OLD.ACTIVE_STUDY IS NOT NULL AND (OLD.ACTIVE_STUDY <> NEW.ACTIVE_STUDY)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''ACTIVE_STUDY'', OLD.ACTIVE_STUDY, NEW.ACTIVE_STUDY);
	
END IF;
IF OLD.INSTITUTIONAL_AFFILIATION IS NOT NULL AND (OLD.INSTITUTIONAL_AFFILIATION <> NEW.INSTITUTIONAL_AFFILIATION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''INSTITUTIONAL_AFFILIATION'', OLD.INSTITUTIONAL_AFFILIATION, NEW.INSTITUTIONAL_AFFILIATION);
	
END IF;
IF OLD.STATUS_ID IS NOT NULL AND (OLD.STATUS_ID <> NEW.STATUS_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''STATUS_ID'', OLD.STATUS_ID, NEW.STATUS_ID);
	
END IF;
IF OLD.DATE_LASTVISIT IS NOT NULL AND (OLD.DATE_LASTVISIT <> NEW.DATE_LASTVISIT)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_LASTVISIT'', OLD.DATE_LASTVISIT, NEW.DATE_LASTVISIT);
	
END IF;
IF OLD.PASSWD_TIMESTAMP IS NOT NULL AND (OLD.PASSWD_TIMESTAMP <> NEW.PASSWD_TIMESTAMP)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PASSWD_TIMESTAMP'', OLD.PASSWD_TIMESTAMP, NEW.PASSWD_TIMESTAMP);
	
END IF;
IF OLD.PASSWD_CHALLENGE_QUESTION IS NOT NULL AND (OLD.PASSWD_CHALLENGE_QUESTION <> NEW.PASSWD_CHALLENGE_QUESTION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PASSWD_CHALLENGE_QUESTION'', OLD.PASSWD_CHALLENGE_QUESTION, NEW.PASSWD_CHALLENGE_QUESTION);
	
END IF;
IF OLD.PASSWD_CHALLENGE_ANSWER IS NOT NULL AND (OLD.PASSWD_CHALLENGE_ANSWER <> NEW.PASSWD_CHALLENGE_ANSWER)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PASSWD_CHALLENGE_ANSWER'', OLD.PASSWD_CHALLENGE_ANSWER, NEW.PASSWD_CHALLENGE_ANSWER);
	
END IF;
IF OLD.PHONE IS NOT NULL AND (OLD.PHONE <> NEW.PHONE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''PHONE'', OLD.PHONE, NEW.PHONE);
	
END IF;
IF OLD.USER_TYPE_ID IS NOT NULL AND (OLD.USER_TYPE_ID <> NEW.USER_TYPE_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''USER_TYPE_ID'', OLD.USER_TYPE_ID, NEW.USER_TYPE_ID);
	
END IF;

return null;
END;
' LANGUAGE 'plpgsql';


CREATE TRIGGER USERACCOUNT_AUDIT_TRIGGER
AFTER UPDATE
ON USER_ACCOUNT
FOR EACH ROW
EXECUTE PROCEDURE useraccount_audit_sql();