CREATE FUNCTION event_crf_trigger() RETURNS "trigger"
    AS 'DECLARE
	pk INTEGER;
	entity_name_value TEXT;
BEGIN
	IF (TG_OP = ''UPDATE'') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Event CRF status changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Status'';
		IF(OLD.status_id = ''1'' AND NEW.status_id = ''2'') THEN
		    IF (NEW.electronic_signature_status) THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, ''14'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE                    
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, ''8'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;                    
		ELSIF (OLD.status_id = ''1'' AND NEW.status_id = ''4'') THEN
		    IF (NEW.electronic_signature_status) THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, ''15'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            ELSE
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, ''10'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
            END IF;
		ELSIF (OLD.status_id = ''4'' AND NEW.status_id = ''2'') THEN
    		IF (NEW.electronic_signature_status) THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, ''16'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
		    ELSE
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
                    VALUES (pk, ''11'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
		    END IF;
		END IF;
		---------------
		END IF;

		IF(OLD.date_interviewed <> NEW.date_interviewed) THEN
		---------------
		--Event CRF date interviewed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Date interviewed'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, ''9'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.date_interviewed, NEW.date_interviewed, NEW.event_crf_id);
		---------------
		END IF;

		IF((OLD.interviewer_name <> NEW.interviewer_name) AND (OLD.interviewer_name <> '''')) THEN		---------------
		--Event CRF interviewer name
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Interviewer Name'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, ''9'', now(), NEW.update_id, ''event_crf'', NEW.event_crf_id, entity_name_value, OLD.interviewer_name, NEW.interviewer_name, NEW.event_crf_id);
		---------------
		END IF;
	RETURN NULL; --return values ignored for ''after'' triggers
	END IF;
END;
'
    LANGUAGE plpgsql;


ALTER FUNCTION public.event_crf_trigger() OWNER TO clinica;



CREATE FUNCTION global_subject_trigger() RETURNS "trigger"
    AS 'DECLARE
	pk INTEGER;
	entity_name_value TEXT;
BEGIN
	IF (TG_OP = ''INSERT'') THEN
		---------------
		--Subject created
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
			VALUES (pk, ''5'', now(), NEW.owner_id, ''subject'', NEW.subject_id);
		RETURN NULL; --return values ignored for ''after'' triggers
		---------------
	ELSIF (TG_OP = ''UPDATE'') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Subject status changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Status'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''6'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.status_id, NEW.status_id);
		---------------
		END IF;

		IF(OLD.unique_identifier <> NEW.unique_identifier) THEN
		---------------
		--Subject value changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Person ID'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''7'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.unique_identifier, NEW.unique_identifier);
		---------------
		END IF;

		IF(OLD.date_of_birth <> NEW.date_of_birth) THEN
		---------------
		--Subject value changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Date of Birth'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''7'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.date_of_birth, NEW.date_of_birth);
		---------------
		END IF;

        IF(OLD.gender <> NEW.gender) THEN
   		---------------
   		--Subject value changed
   		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
   		SELECT INTO entity_name_value ''Gender'';
   		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
		VALUES (pk, ''7'', now(), NEW.update_id, ''subject'', NEW.subject_id, entity_name_value, OLD.gender, NEW.gender);
   		---------------
   		END IF;
		
	RETURN NULL; --return values ignored for ''after'' triggers
	END IF;
END;
'
    LANGUAGE plpgsql;


ALTER FUNCTION public.global_subject_trigger() OWNER TO clinica;



CREATE FUNCTION item_data_trigger() RETURNS "trigger"
    AS 'DECLARE
	pk INTEGER;
	entity_name_value TEXT;
	std_evnt_id INTEGER;
	crf_version_id INTEGER;
BEGIN
	IF (TG_OP = ''DELETE'') THEN
		---------------
		--Item data deleted (by deleting an event crf)
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = OLD.item_id;
        SELECT INTO std_evnt_id ec.study_event_id FROM event_crf ec WHERE ec.event_crf_id = OLD.event_crf_id;
        SELECT INTO crf_version_id ec.crf_version_id FROM event_crf ec WHERE ec.event_crf_id = OLD.event_crf_id;
		IF (OLD.update_id < 1) THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, event_crf_id, study_event_id, event_crf_version_id)
                VALUES (pk, ''13'', now(), OLD.update_id, ''item_data'', OLD.item_data_id, entity_name_value, OLD.value, OLD.event_crf_id, std_evnt_id, crf_version_id);
        ELSE
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, event_crf_id, study_event_id, event_crf_version_id)
                VALUES (pk, ''13'', now(), OLD.owner_id, ''item_data'', OLD.item_data_id, entity_name_value, OLD.value, OLD.event_crf_id, std_evnt_id, crf_version_id);
        END IF;
		RETURN NULL; --return values ignored for ''after'' triggers
	ELSIF (TG_OP = ''UPDATE'') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Item data status changed (by removing an event crf)
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, ''12'', now(), NEW.update_id, ''item_data'', NEW.item_data_id, entity_name_value, OLD.status_id, NEW.status_id, NEW.event_crf_id);
		---------------
		END IF;

		IF(OLD.value <> NEW.value) THEN
		---------------
		--Item data updated
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
			VALUES (pk, ''1'', now(), NEW.update_id, ''item_data'', NEW.item_data_id, entity_name_value, OLD.value, NEW.value, NEW.event_crf_id);
		---------------
		END IF;
		RETURN NULL; --return values ignored for ''after'' triggers
	END IF;
RETURN NULL; --return values ignored for ''after'' triggers
END;
'
    LANGUAGE plpgsql;


ALTER FUNCTION public.item_data_trigger() OWNER TO clinica;



CREATE FUNCTION study_subject_trigger() RETURNS "trigger"
    AS 'DECLARE
	pk INTEGER;
	entity_name_value TEXT;
    old_unique_identifier TEXT;
    new_unique_identifier TEXT;

BEGIN
	IF (TG_OP = ''INSERT'') THEN
		---------------
		--Study subject created
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
			VALUES (pk, ''2'', now(), NEW.owner_id, ''study_subject'', NEW.study_subject_id);
		RETURN NULL; --return values ignored for ''after'' triggers
		---------------
	ELSIF (TG_OP = ''UPDATE'') THEN
		IF(OLD.status_id <> NEW.status_id) THEN
		---------------
		--Study subject status changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Status'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''3'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.status_id, NEW.status_id);
		---------------
		END IF;

		IF(OLD.label <> NEW.label) THEN
		---------------
		--Study subject value changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Study Subject ID'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''4'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.label, NEW.label);
		---------------
		END IF;

		IF(OLD.secondary_label <> NEW.secondary_label) THEN
		---------------
		--Study subject value changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Secondary Subject ID'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''4'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.secondary_label, NEW.secondary_label);
		---------------
		END IF;

		IF(OLD.enrollment_date <> NEW.enrollment_date) THEN
		---------------
		--Study subject value changed
		SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
		SELECT INTO entity_name_value ''Enrollment Date'';
		INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
			VALUES (pk, ''4'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, OLD.enrollment_date, NEW.enrollment_date);
		---------------
		END IF;

        IF(OLD.study_id <> NEW.study_id) THEN
        ---------------
        --Subject reassigned
        SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
        SELECT INTO entity_name_value ''Study id'';
        SELECT INTO old_unique_identifier study.unique_identifier FROM study study WHERE study.study_id = OLD.study_id;
        SELECT INTO new_unique_identifier study.unique_identifier FROM study study WHERE study.study_id = NEW.study_id;
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''27'', now(), NEW.update_id, ''study_subject'', NEW.study_subject_id, entity_name_value, old_unique_identifier, new_unique_identifier);
        ---------------
        END IF;

	RETURN NULL; --return values ignored for ''after'' triggers
	END IF;
END;
'
    LANGUAGE plpgsql;


ALTER FUNCTION public.study_subject_trigger() OWNER TO clinica;

CREATE OR REPLACE FUNCTION study_event_trigger() RETURNS "trigger"
    AS 'DECLARE
	pk INTEGER;
BEGIN
	IF (TG_OP = ''INSERT'') THEN
        SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
        IF(NEW.subject_event_status_id = ''1'') THEN
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''17'', now(), NEW.owner_id, ''study_event'', NEW.study_event_id, ''Status'',''0'', NEW.subject_event_status_id);
        END IF;
    END IF;

	IF (TG_OP = ''UPDATE'') THEN
		IF(OLD.subject_event_status_id <> NEW.subject_event_status_id) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            IF(NEW.subject_event_status_id = ''1'') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''17'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''3'') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''18'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''4'') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''19'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''5'') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''20'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''6'') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''21'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
            ELSIF(NEW.subject_event_status_id = ''7'') THEN
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''22'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.subject_event_status_id, NEW.subject_event_status_id);
		    END IF;
	    END IF;
        IF(OLD.status_id <> NEW.status_id) THEN
            IF(NEW.status_id = ''5'') THEN
                SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
                VALUES (pk, ''23'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Status'', OLD.status_id, NEW.status_id);
            END IF;
        END IF;
        IF(OLD.date_start <> NEW.date_start) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''24'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Start date'', OLD.date_start, NEW.date_start);
        END IF;
        IF(OLD.date_end <> NEW.date_end) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''25'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''End date'', OLD.date_end, NEW.date_end);
        END IF;
        IF(OLD.location <> NEW.location) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''26'', now(), NEW.update_id, ''study_event'', NEW.study_event_id, ''Location'', OLD.location, NEW.location);
        END IF;
    	RETURN NULL; --return values ignored for ''after'' triggers
	END IF;
	RETURN NULL;
END;'
    LANGUAGE plpgsql;

ALTER FUNCTION public.study_event_trigger() OWNER TO clinica;

--group assignment trigger
CREATE OR REPLACE FUNCTION subject_group_assignment_trigger() RETURNS "trigger"
    AS 'DECLARE
	pk INTEGER;
	group_name TEXT;
	old_group_name TEXT;
	new_group_name TEXT;
BEGIN
	IF (TG_OP = ''INSERT'') THEN
        SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
        SELECT INTO group_name sg.name FROM study_group sg WHERE sg.study_group_id = NEW.study_group_id;
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
        VALUES (pk, ''28'', now(), NEW.owner_id, ''subject_group_map'', NEW.study_subject_id, ''Status'','''', group_name);
    END IF;
	IF (TG_OP = ''UPDATE'') THEN
		IF(OLD.study_group_id <> NEW.study_group_id) THEN
            SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
            SELECT INTO old_group_name sg.name FROM study_group sg WHERE sg.study_group_id = OLD.study_group_id;
            SELECT INTO new_group_name sg.name FROM study_group sg WHERE sg.study_group_id = NEW.study_group_id;
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (pk, ''29'', now(), NEW.update_id, ''subject_group_map'', NEW.study_subject_id, ''Status'',old_group_name, new_group_name);
	    END IF;
    	RETURN NULL; --return values ignored for ''after'' triggers
	END IF;
	RETURN NULL;
END;'
    LANGUAGE plpgsql;

ALTER FUNCTION public.subject_group_assignment_trigger() OWNER TO clinica;

CREATE OR REPLACE FUNCTION repeating_item_data_trigger()
  RETURNS "trigger" AS
'DECLARE
 pk INTEGER;
 entity_name_value TEXT;
 std_evnt_id INTEGER;
 crf_version_id INTEGER;
 validator_id INTEGER;
 event_crf_status_id INTEGER;


BEGIN
 IF (TG_OP = ''INSERT'') THEN
  ---------------  
  SELECT INTO pk NEXTVAL(''audit_log_event_audit_id_seq'');
  SELECT INTO entity_name_value item.name FROM item WHERE item.item_id = NEW.item_id;
        SELECT INTO std_evnt_id ec.study_event_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
        SELECT INTO crf_version_id ec.crf_version_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
 SELECT INTO validator_id ec.validator_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
 SELECT INTO event_crf_status_id ec.status_id FROM event_crf ec WHERE ec.event_crf_id = NEW.event_crf_id;
 
        IF (NEW.status_id = ''2'' AND NEW.ordinal > 1 AND validator_id > 0 AND event_crf_status_id  = ''4'') THEN -- DDE
          
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, new_value, event_crf_id, study_event_id, event_crf_version_id)
                VALUES (pk, ''28'', now(), NEW.owner_id, ''item_data'', NEW.item_data_id, entity_name_value, NEW.value, NEW.event_crf_id, std_evnt_id, crf_version_id);
        ELSE 
          IF(NEW.status_id =''2'' AND NEW.ordinal > 1  AND event_crf_status_id  = ''2'') THEN--ADE
                INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, new_value, event_crf_id, study_event_id, event_crf_version_id)
                VALUES (pk, ''28'', now(), NEW.owner_id, ''item_data'', NEW.item_data_id, entity_name_value, NEW.value, NEW.event_crf_id, std_evnt_id, crf_version_id);
          END IF;
       END IF;
  RETURN NULL; --return values ignored for ''after'' triggers
 
 END IF;
RETURN NULL; --return values ignored for ''after'' triggers
END; ' LANGUAGE plpgsql VOLATILE;

ALTER FUNCTION repeating_item_data_trigger() OWNER TO clinica;

CREATE TRIGGER event_crf_update
    AFTER UPDATE ON event_crf
    FOR EACH ROW
    EXECUTE PROCEDURE event_crf_trigger();

CREATE TRIGGER global_subject_insert_update
    AFTER INSERT OR UPDATE ON subject
    FOR EACH ROW
    EXECUTE PROCEDURE global_subject_trigger();

CREATE TRIGGER item_data_update
    AFTER DELETE OR UPDATE ON item_data
    FOR EACH ROW
    EXECUTE PROCEDURE item_data_trigger();

CREATE TRIGGER study_subject_insert_updare
    AFTER INSERT OR UPDATE ON study_subject
    FOR EACH ROW
    EXECUTE PROCEDURE study_subject_trigger();

CREATE TRIGGER study_event_insert_update
    AFTER INSERT OR UPDATE ON study_event
    FOR EACH ROW
    EXECUTE PROCEDURE study_event_trigger();

CREATE TRIGGER subject_group_map_insert_update
    AFTER INSERT OR UPDATE ON subject_group_map
    FOR EACH ROW
    EXECUTE PROCEDURE subject_group_assignment_trigger();

CREATE TRIGGER repeating_data_insert
  AFTER INSERT
  ON item_data
  FOR EACH ROW
  EXECUTE PROCEDURE repeating_item_data_trigger();