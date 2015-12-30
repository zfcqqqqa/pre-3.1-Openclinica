DROP TRIGGER crf_AUDIT_TRIGGER ON CRF;
DROP FUNCTION crf_AUDIT_SQL();


CREATE FUNCTION crf_AUDIT_SQL() RETURNS OPAQUE AS '
DECLARE
	pk INTEGER;
BEGIN 
SELECT INTO PK NEXTVAL(''audit_log_sequence'');

INSERT INTO AUDIT_EVENT (AUDIT_ID, AUDIT_DATE, AUDIT_TABLE, 
				USER_ID, ENTITY_ID, REASON_FOR_CHANGE)
	VALUES
	(pk, now(), 
	''CRF'', 
	NEW.UPDATE_ID,
	NEW.CRF_ID,
	''AUDIT_EVENT_UPDATE_TRIGGER_FIRED'');

IF OLD.CRF_ID IS NOT NULL AND (OLD.CRF_ID <> NEW.CRF_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''CRF_ID'', OLD.CRF_ID, NEW.CRF_ID);
	
END IF;

IF OLD.LABEL IS NOT NULL AND (OLD.LABEL <> NEW.LABEL)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''LABEL'', OLD.LABEL, NEW.LABEL);
	
END IF;
IF OLD.NAME IS NOT NULL AND (OLD.NAME <> NEW.NAME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''NAME'', OLD.NAME, NEW.NAME);
	
END IF;
IF OLD.STATUS_ID IS NOT NULL AND (OLD.STATUS_ID <> NEW.STATUS_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''STATUS_ID'', OLD.STATUS_ID, NEW.STATUS_ID);
	
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
IF OLD.DESCRIPTION IS NOT NULL AND (OLD.DESCRIPTION <> NEW.DESCRIPTION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DESCRIPTION'', OLD.DESCRIPTION, NEW.DESCRIPTION);
	
END IF;

return null;
END;
' LANGUAGE 'plpgsql';


CREATE TRIGGER crf_AUDIT_TRIGGER
AFTER UPDATE
ON CRF
FOR EACH ROW
EXECUTE PROCEDURE crf_audit_sql();