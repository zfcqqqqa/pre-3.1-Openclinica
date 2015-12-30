DROP TRIGGER query_AUDIT_TRIGGER ON QUERY;
DROP FUNCTION query_AUDIT_SQL();

--probably already depreciated, to be replaced by filter_audit_triggers.sql
--tom hickerson, 12/06/2004

CREATE FUNCTION query_AUDIT_SQL() RETURNS OPAQUE AS '
DECLARE
	pk INTEGER;
BEGIN 
SELECT INTO PK NEXTVAL(''audit_log_sequence'');

INSERT INTO AUDIT_EVENT (AUDIT_ID, AUDIT_DATE, AUDIT_TABLE, 
				USER_ID, ENTITY_ID, REASON_FOR_CHANGE)
	VALUES
	(pk, now(), 
	''QUERY'', 
	NEW.UPDATE_ID,
	NEW.QUERY_ID,
	''UPDATE TRIGGER FIRED'');

IF OLD.WHERE_CLAUSE IS NOT NULL AND (OLD.WHERE_CLAUSE <> NEW.WHERE_CLAUSE)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''WHERE_CLAUSE'', OLD.WHERE_CLAUSE, NEW.WHERE_CLAUSE);
	
END IF;
IF OLD.APPROVER_ID IS NOT NULL AND (OLD.APPROVER_ID <> NEW.APPROVER_ID)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''APPROVER_ID'', OLD.APPROVER_ID, NEW.APPROVER_ID);
	
END IF;
IF OLD.RESEARCH_PROPOSAL IS NOT NULL AND (OLD.RESEARCH_PROPOSAL <> NEW.RESEARCH_PROPOSAL)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''RESEARCH_PROPOSAL'', OLD.RESEARCH_PROPOSAL, NEW.RESEARCH_PROPOSAL);
	
END IF;
IF OLD.SIB_REQUIREMENT IS NOT NULL AND (OLD.SIB_REQUIREMENT <> NEW.SIB_REQUIREMENT)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''SIB_REQUIREMENT'', OLD.SIB_REQUIREMENT, NEW.SIB_REQUIREMENT);
	
END IF;
IF OLD.RUN_TIME IS NOT NULL AND (OLD.RUN_TIME <> NEW.RUN_TIME)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''RUN_TIME'', OLD.RUN_TIME, NEW.RUN_TIME);
	
END IF;
IF OLD.NUM_RUNS IS NOT NULL AND (OLD.NUM_RUNS <> NEW.NUM_RUNS)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''NUM_RUNS'', OLD.NUM_RUNS, NEW.NUM_RUNS);
	
END IF;
IF OLD.DATE_LAST_RUN IS NOT NULL AND (OLD.DATE_LAST_RUN <> NEW.DATE_LAST_RUN)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DATE_LAST_RUN'', OLD.DATE_LAST_RUN, NEW.DATE_LAST_RUN);
	
END IF;
IF OLD.SQL_STATEMENT IS NOT NULL AND (OLD.SQL_STATEMENT <> NEW.SQL_STATEMENT)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''SQL_STATEMENT'', OLD.SQL_STATEMENT, NEW.SQL_STATEMENT);
	
END IF;
IF OLD.DESCRIPTION IS NOT NULL AND (OLD.DESCRIPTION <> NEW.DESCRIPTION)
THEN
      INSERT INTO AUDIT_EVENT_VALUES (AUDIT_ID, COLUMN_NAME, OLD_VALUE, NEW_VALUE)
	VALUES
	(pk,''DESCRIPTION'', OLD.DESCRIPTION, NEW.DESCRIPTION);
	
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

return null;
END;
' LANGUAGE 'plpgsql';


CREATE TRIGGER query_AUDIT_TRIGGER
AFTER UPDATE
ON QUERY
FOR EACH ROW
EXECUTE PROCEDURE query_audit_sql();