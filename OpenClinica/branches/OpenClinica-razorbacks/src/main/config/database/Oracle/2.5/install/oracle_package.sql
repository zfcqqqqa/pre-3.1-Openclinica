CREATE OR REPLACE package clinica_table_api is

/*--------------------------------------------------------------------------
*
* File       : oracle_package.sql
*
* Subject    : Package procedures that are called from triggers
*
* Parameters : none
*
* Conditions : tables and sequences should exist before creating the
*              package
*
* Author/Dt  : Shriram Mani  05/16/2008
*
* Comments   : Some of the queries are simplified. It can be further
*              simplified, but not necessary.
*
--------------------------------------------------------------------------*/

procedure event_crf_trigger(
      tg_op varchar2
     ,newrec  in out event_crf%rowtype
     ,oldrec event_crf%rowtype default null
   );

procedure global_subject_trigger(
      tg_op varchar2
     ,newrec  in out subject%rowtype
     ,oldrec subject%rowtype default null
   );

procedure item_data_trigger(
      tg_op varchar2
     ,newrec  in out item_data%rowtype
     ,oldrec item_data%rowtype default null
   );

procedure study_subject_trigger(
      tg_op varchar2
     ,newrec  in out study_subject%rowtype
     ,oldrec study_subject%rowtype default null
   );

procedure study_event_trigger(
      tg_op varchar2
     ,newrec in out study_event%rowtype
     ,oldrec study_event%rowtype default null
   );

procedure subject_grp_assign_trigger(
      tg_op varchar2
     ,newrec in out subject_group_map%rowtype
     ,oldrec subject_group_map%rowtype default null
   );

procedure repeating_item_data_trigger(
      tg_op varchar2
     ,newrec in out item_data%rowtype
     ,oldrec item_data%rowtype default null
   );
   
end;
/


CREATE OR REPLACE package body clinica_table_api is

procedure event_crf_trigger(
      tg_op varchar2
     ,newrec in out event_crf%rowtype
     ,oldrec event_crf%rowtype default null
   ) is
event_type_id    varchar2(10) := null;
entity_name_value varchar2(200);
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        IF nvl(OLDREC.status_id, '-1') <> nvl(NEWREC.status_id, '-1') THEN
        ---------------
        --Event CRF status changed
        entity_name_value := 'Status';
        --
        IF (nvl(OLDREC.status_id, '-1') = '1' AND nvl(NEWREC.status_id, '-1') = '2') THEN
            --
		    IF (NEWREC.electronic_signature_status = '1') THEN
              event_type_id := '14';
            ELSE
              event_type_id := '8';
            END IF;
            --
        ELSIF (nvl(OLDREC.status_id, '-1') = '1' AND nvl(NEWREC.status_id, '-1') = '4') THEN
            --
		    IF (NEWREC.electronic_signature_status = '1') THEN
              event_type_id := '15';
            ELSE
              event_type_id := '10';
            END IF;
            --
        ELSIF (nvl(OLDREC.status_id, '-1') = '4' AND nvl(NEWREC.status_id, '-1') = '2') THEN
            --
		    IF (NEWREC.electronic_signature_status = '1') THEN
              event_type_id := '16';
            ELSE
              event_type_id := '11';
            END IF;
            --
        END IF;
        --
        if event_type_id is not null then
            INSERT INTO audit_log_event(
                audit_id,
                audit_log_event_type_id,
                audit_date,
                user_id,
                audit_table,
                entity_id,
                entity_name,
                old_value,
                new_value,
                event_crf_id
              ) VALUES (
                audit_log_event_audit_id_seq.nextval,
                event_type_id,
                sysdate,
                NEWREC.update_id,
                'event_crf',
                NEWREC.event_crf_id,
                entity_name_value,
                OLDREC.status_id,
                NEWREC.status_id,
                NEWREC.event_crf_id
              );
        END IF;
       END IF;

        IF (nvl(OLDREC.date_interviewed, '01-JAN-1000') <> nvl(NEWREC.date_interviewed, '01-JAN-1000')) THEN
          --Event CRF date interviewed
          entity_name_value := 'Date interviewed';
          event_type_id := '9';
          --
            INSERT INTO audit_log_event(
                audit_id,
                audit_log_event_type_id,
                audit_date,
                user_id,
                audit_table,
                entity_id,
                entity_name,
                old_value,
                new_value,
                event_crf_id
              ) VALUES (
                audit_log_event_audit_id_seq.nextval,
                event_type_id,
                sysdate,
                NEWREC.update_id,
                'event_crf',
                NEWREC.event_crf_id,
                entity_name_value,
                OLDREC.date_interviewed,
                NEWREC.date_interviewed,
                NEWREC.event_crf_id
              );
        END IF;

        IF((nvl(OLDREC.interviewer_name, ' ') <> nvl(NEWREC.interviewer_name, ' ')) AND (OLDREC.interviewer_name is not null)) THEN
          --Event CRF interviewer name
          entity_name_value := 'Interviewer Name';
          event_type_id := '9';
          --
            INSERT INTO audit_log_event(
                audit_id,
                audit_log_event_type_id,
                audit_date,
                user_id,
                audit_table,
                entity_id,
                entity_name,
                old_value,
                new_value,
                event_crf_id
              ) VALUES (
                audit_log_event_audit_id_seq.nextval,
                event_type_id,
                sysdate,
                NEWREC.update_id,
                'event_crf',
                NEWREC.event_crf_id,
                entity_name_value,
                OLDREC.interviewer_name,
                NEWREC.interviewer_name,
                NEWREC.event_crf_id
              );
        END IF;
    END IF;
END;
--------------------------------------------------------------------------------

procedure global_subject_trigger(
      tg_op varchar2
     ,newrec in out subject%rowtype
     ,oldrec subject%rowtype default null
   ) is
event_type_id    varchar2(10) := null;
entity_name_value varchar2(200);
BEGIN
    --
    IF (TG_OP = 'INSERT') THEN
        ---------------
        --Subject created
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
            VALUES (audit_log_event_audit_id_seq.nextval, '5', sysdate, NEWREC.owner_id, 'subject', NEWREC.subject_id);
        ---------------
    ELSIF (TG_OP = 'UPDATE') THEN
        IF nvl(OLDREC.status_id, '-1') <> nvl(NEWREC.status_id, '-1') THEN
        ---------------
        --Subject status changed
        entity_name_value  := 'Status';
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval, '6', sysdate, NEWREC.update_id, 'subject', NEWREC.subject_id, entity_name_value, OLDREC.status_id, NEWREC.status_id);
        ---------------
        END IF;

        IF(nvl(OLDREC.unique_identifier, '-1') <> nvl(NEWREC.unique_identifier, '-1')) THEN
        ---------------
        --Subject value changed
        entity_name_value  := 'Person ID';
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval, '7', sysdate, NEWREC.update_id, 'subject', NEWREC.subject_id, entity_name_value, OLDREC.unique_identifier, NEWREC.unique_identifier);
        ---------------
        END IF;

        IF(nvl(OLDREC.date_of_birth, '01-JAN-1000') <> nvl(NEWREC.date_of_birth, '01-JAN-1000')) THEN
        ---------------
        --Subject value changed
        entity_name_value  := 'Date of Birth';
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval, '7', sysdate, NEWREC.update_id, 'subject', NEWREC.subject_id, entity_name_value, OLDREC.date_of_birth, NEWREC.date_of_birth);
        ---------------
        END IF;

        IF(nvl(OLDREC.gender, '-1') <> nvl(NEWREC.gender, '-1')) THEN
        ---------------
        --Subject value changed
        entity_name_value  := 'Gender';
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval, '7', sysdate, NEWREC.update_id, 'subject', NEWREC.subject_id, entity_name_value, OLDREC.gender, NEWREC.gender);
        ---------------
        END IF;

    END IF;
END;
--------------------------------------------------------------------------------

procedure item_data_trigger(
      tg_op varchar2
     ,newrec in out item_data%rowtype
     ,oldrec item_data%rowtype default null
   ) is
event_type_id    varchar2(10) := null;
entity_name_value varchar2(200);
std_evnt_id INTEGER;
crf_version_id INTEGER;
luser_id  audit_log_event.user_id%type;
--     
BEGIN
    --
    IF (TG_OP = 'DELETE') THEN
        ---------------
        --Item data deleted (by deleting an event crf)
        begin
          SELECT item.name
            INTO entity_name_value
            FROM item
           WHERE item.item_id = OLDREC.item_id;
        exception
          when others then
            entity_name_value := null;
        end;
        --
        begin
          SELECT ec.study_event_id
            INTO std_evnt_id
            FROM event_crf ec 
           WHERE ec.event_crf_id = OLDREC.event_crf_id;
          --
        exception
          when others then
            std_evnt_id := null;
        end;
        --
        begin
          SELECT ec.crf_version_id 
            INTO crf_version_id 
            FROM event_crf ec
           WHERE ec.event_crf_id = OLDREC.event_crf_id;
        exception
          when others then
            crf_version_id := null;
        end;
        --
		IF (OLDREC.update_id < 1) THEN
          luser_id := oldrec.update_id;
        else
          luser_id := oldrec.owner_id;
        end if;
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, event_crf_id,study_event_id,event_crf_version_id)
            VALUES (audit_log_event_audit_id_seq.nextval , '13', sysdate, luser_id, 'item_data', OLDREC.item_data_id, entity_name_value, OLDREC.value, OLDREC.event_crf_id,std_evnt_id,crf_version_id);
        
        ELSIF (TG_OP = 'UPDATE') THEN
        IF nvl(OLDREC.status_id, '-1') <> nvl(NEWREC.status_id, '-1') THEN
        ---------------
        --Item data status changed (by removing an event crf)
        begin
          SELECT item.name
            INTO entity_name_value
            FROM item
           WHERE item.item_id = NEWREC.item_id;
        exception
          when others then
            entity_name_value := null;
        end;
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (audit_log_event_audit_id_seq.nextval , '12', sysdate, NEWREC.update_id, 'item_data', NEWREC.item_data_id, entity_name_value, OLDREC.status_id, NEWREC.status_id, NEWREC.event_crf_id);
        ---------------
        END IF;

        IF(nvl(OLDREC.value, ' ') <> nvl(NEWREC.value, ' ')) THEN
        ---------------
        --Item data updated
        begin
          SELECT item.name
            INTO entity_name_value
            FROM item
           WHERE item.item_id = NEWREC.item_id;
        exception
          when others then
            entity_name_value := null;
        end;
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value, event_crf_id)
            VALUES (audit_log_event_audit_id_seq.nextval , '1', sysdate, NEWREC.update_id, 'item_data', NEWREC.item_data_id, entity_name_value, OLDREC.value, NEWREC.value, NEWREC.event_crf_id);
        ---------------
        END IF;
    END IF;
END;
--------------------------------------------------------------------------------

procedure study_subject_trigger(
      tg_op varchar2
     ,newrec in out study_subject%rowtype
     ,oldrec study_subject%rowtype default null
   ) is
event_type_id         varchar2(10) := null;
entity_name_value     varchar2(200);
old_unique_identifier varchar2(200);
new_unique_identifier varchar2(200);
BEGIN
    --
    IF (TG_OP = 'INSERT') THEN
        ---------------
        --Study subject created
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id)
            VALUES (audit_log_event_audit_id_seq.nextval,'2', sysdate, NEWREC.owner_id, 'study_subject', NEWREC.study_subject_id);
        ---------------
    ELSIF (TG_OP = 'UPDATE') THEN
        IF nvl(OLDREC.status_id, '-1') <> nvl(NEWREC.status_id, '-1') THEN
        ---------------
        --Study subject status changed
        entity_name_value := 'Status';
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval,'3', sysdate, NEWREC.update_id, 'study_subject', NEWREC.study_subject_id, entity_name_value, OLDREC.status_id, NEWREC.status_id);
        ---------------
        END IF;

        IF(nvl(OLDREC.label, ' ') <> nvl(NEWREC.label, ' ')) THEN
        ---------------
        --Study subject value changed
        entity_name_value := 'Study Subject ID';
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval,'4', sysdate, NEWREC.update_id, 'study_subject', NEWREC.study_subject_id, entity_name_value, OLDREC.label, NEWREC.label);
        ---------------
        END IF;

        IF(nvl(OLDREC.secondary_label, ' ') <> nvl(NEWREC.secondary_label, ' ')) THEN
        ---------------
        --Study subject value changed
        entity_name_value := 'Secondary Subject ID';
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval,'4', sysdate, NEWREC.update_id, 'study_subject', NEWREC.study_subject_id, entity_name_value, OLDREC.secondary_label, NEWREC.secondary_label);
        ---------------
        END IF;

        IF(nvl(OLDREC.enrollment_date, '01-JAN-1000') <> nvl(NEWREC.enrollment_date, '01-JAN-1000')) THEN
        ---------------
        --Study subject value changed
        entity_name_value := 'Enrollment Date';
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval,'4', sysdate, NEWREC.update_id, 'study_subject', NEWREC.study_subject_id, entity_name_value, OLDREC.enrollment_date, NEWREC.enrollment_date);
        ---------------
        END IF;

        IF (nvl(OLDREC.study_id, -1) <> nvl(NEWREC.study_id, -1) ) THEN
          ---------------
          --Subject reassigned
          entity_name_value := 'Study id';
          --
          begin
            SELECT study.unique_identifier
              INTO old_unique_identifier  
              FROM study study 
             WHERE study.study_id = OLDREC.study_id;
          exception
             when others then
               old_unique_identifier := null;
          end;
          --
          begin
            SELECT study.unique_identifier
              INTO new_unique_identifier  
              FROM study study
             WHERE study.study_id = NEWREC.study_id;
          exception
             when others then
               new_unique_identifier := null;
          end;
          --
          INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval, '27', sysdate, NEWREC.update_id, 'study_subject', NEWREC.study_subject_id, entity_name_value, old_unique_identifier, new_unique_identifier);
          ---------------
        END IF;

    END IF;
END;
--------------------------------------------------------------------------------

procedure study_event_trigger(
      tg_op varchar2
     ,newrec in out study_event%rowtype
     ,oldrec study_event%rowtype default null
   ) is
event_type_id         varchar2(10) := null;
entity_name_value     varchar2(200);
begin   
	IF (TG_OP = 'INSERT') THEN
        
        IF(NEWREC.subject_event_status_id = '1') THEN
          event_type_id := '17';
          entity_name_value := 'Status';
          --            
          INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
          VALUES (audit_log_event_audit_id_seq.nextval, event_type_id, sysdate, NEWREC.owner_id, 'study_event', NEWREC.study_event_id, entity_name_value, '0', NEWREC.subject_event_status_id);
        END IF;
    END IF;
    --
	IF (TG_OP = 'UPDATE') THEN
		IF(OLDREC.subject_event_status_id <> NEWREC.subject_event_status_id) THEN
          entity_name_value := 'Status';
          --            
            IF(NEWREC.subject_event_status_id = '1') THEN
              event_type_id := '17';
            ELSIF(NEWREC.subject_event_status_id = '3') THEN
              event_type_id := '18';
            ELSIF(NEWREC.subject_event_status_id = '4') THEN
              event_type_id := '19';
            ELSIF(NEWREC.subject_event_status_id = '5') THEN
              event_type_id := '20';
            ELSIF(NEWREC.subject_event_status_id = '6') THEN
              event_type_id := '21';
            ELSIF(NEWREC.subject_event_status_id = '7') THEN
              event_type_id := '22';
            ELSIF(NEWREC.subject_event_status_id = '8') THEN
              event_type_id := '31';
		    END IF;
            --
            INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
            VALUES (audit_log_event_audit_id_seq.nextval, event_type_id, sysdate, NEWREC.update_id, 'study_event', NEWREC.study_event_id, entity_name_value, OLDREC.subject_event_status_id, NEWREC.subject_event_status_id);
            --
	    END IF;
        IF(OLDREC.status_id <> NEWREC.status_id) THEN
            IF(NEWREC.status_id = '5') THEN
              --
              entity_name_value := 'Status';
              event_type_id := '23';
              --
              INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
              VALUES (audit_log_event_audit_id_seq.nextval, event_type_id, sysdate, NEWREC.update_id, 'study_event', NEWREC.study_event_id, entity_name_value, OLDREC.status_id, NEWREC.status_id);
              --
            END IF;
        END IF;
        IF(OLDREC.date_start <> NEWREC.date_start) THEN
              --
              entity_name_value := 'Start date';
              event_type_id := '24';
              --
              INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
              VALUES (audit_log_event_audit_id_seq.nextval, event_type_id, sysdate, NEWREC.update_id, 'study_event', NEWREC.study_event_id, entity_name_value, OLDREC.date_start, NEWREC.date_start);
              --
        END IF;
        IF(OLDREC.date_end <> NEWREC.date_end) THEN
              --
              entity_name_value := 'End date';
              event_type_id := '25';
              --
              INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
              VALUES (audit_log_event_audit_id_seq.nextval, event_type_id, sysdate, NEWREC.update_id, 'study_event', NEWREC.study_event_id, entity_name_value, OLDREC.date_end, NEWREC.date_end);
              --
        END IF;
        IF(OLDREC."location" <> NEWREC."location") THEN
              --
              entity_name_value := 'Location';
              event_type_id := '26';
              --
              INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
              VALUES (audit_log_event_audit_id_seq.nextval, event_type_id, sysdate, NEWREC.update_id, 'study_event', NEWREC.study_event_id, entity_name_value, OLDREC."location", NEWREC."location");
              --
        END IF;

	END IF;
END;
--------------------------------------------------------------------------------

procedure subject_grp_assign_trigger(
      tg_op varchar2
     ,newrec in out subject_group_map%rowtype
     ,oldrec subject_group_map%rowtype default null
   ) is
--
new_group_name study_group.name%type;
old_group_name study_group.name%type;
--
begin   
    IF (TG_OP = 'INSERT') THEN
        --
        begin
          SELECT sg.name 
            INTO new_group_name 
            FROM study_group sg 
           WHERE sg.study_group_id = NEWREC.study_group_id;
        exception
           when no_data_found then
             new_group_name := null;
        end;
        --
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
        VALUES (audit_log_event_audit_id_seq.nextval,  '28', sysdate, NEWREC.owner_id, 'subject_group_map', NEWREC.study_subject_id, 'Status','', new_group_name);
      --
    ELSIF (TG_OP = 'UPDATE') THEN
        
        IF nvl(OLDREC.study_group_id, -1) <> nvl(NEWREC.study_group_id, -1) THEN

          begin
            SELECT sg.name 
              INTO old_group_name 
              FROM study_group sg 
             WHERE sg.study_group_id = OLDREC.study_group_id;
          exception
             when no_data_found then
               old_group_name := null;
          end;
          --
          begin
            SELECT sg.name 
              INTO new_group_name 
              FROM study_group sg 
             WHERE sg.study_group_id = NEWREC.study_group_id;
          exception
             when no_data_found then
               new_group_name := null;
          end;
          --
          INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, old_value, new_value)
          VALUES (audit_log_event_audit_id_seq.nextval,  '29', sysdate, NEWREC.update_id, 'subject_group_map', NEWREC.study_subject_id, 'Status', old_group_name, new_group_name);
        END IF;
    END IF;
END;
--------------------------------------------------------------------------------

procedure repeating_item_data_trigger(
      tg_op varchar2
     ,newrec in out item_data%rowtype
     ,oldrec item_data%rowtype default null
   ) is
--
std_evnt_id         event_crf.study_event_id%type;
crf_version_id      event_crf.crf_version_id%type;
validator_id        event_crf.validator_id%type;
event_crf_status_id event_crf.status_id%type;
entity_name_value   item.name%type;
--
begin   
  --
 IF (TG_OP = 'INSERT') THEN
   --  
    begin
      SELECT item.name 
        INTO entity_name_value
        FROM item 
       WHERE item.item_id = NEWREC.item_id;
    exception
      when no_data_found then
        entity_name_value := null;
    end;
    --
    begin
      SELECT ec.study_event_id,
             ec.crf_version_id, 
             ec.validator_id,
             ec.status_id
        INTO 
             std_evnt_id,
             crf_version_id,
             validator_id,
             event_crf_status_id
        FROM event_crf ec 
       WHERE ec.event_crf_id = NEWREC.event_crf_id;
    exception
      when no_data_found then
        std_evnt_id := null;
        crf_version_id := null;
        validator_id := null;
        event_crf_status_id := null;
    end;
    --
    IF (nvl(NEWREC.status_id, -1) = '2' AND 
        nvl(NEWREC.ordinal, -1) > 1 AND 
        nvl(validator_id, -1) > 0 AND 
        nvl(event_crf_status_id, -1)  = '4') THEN -- DDE
      INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, new_value, event_crf_id, study_event_id, event_crf_version_id)
      VALUES (audit_log_event_audit_id_seq.nextval, '30', sysdate, NEWREC.owner_id, 'item_data', NEWREC.item_data_id, entity_name_value, NEWREC.value, NEWREC.event_crf_id, std_evnt_id, crf_version_id);
    ELSE 
    IF (nvl(NEWREC.status_id, -1) = '2' AND 
        nvl(NEWREC.ordinal, -1) > 1 AND 
        nvl(event_crf_status_id, -1)  = '2') THEN -- ADE
        INSERT INTO audit_log_event(audit_id, audit_log_event_type_id, audit_date, user_id, audit_table, entity_id, entity_name, new_value, event_crf_id, study_event_id, event_crf_version_id)
        VALUES (audit_log_event_audit_id_seq.nextval, '30', sysdate, NEWREC.owner_id, 'item_data', NEWREC.item_data_id, entity_name_value, NEWREC.value, NEWREC.event_crf_id, std_evnt_id, crf_version_id);
      END IF;
    END IF;
  END IF;
END;
--------------------------------------------------------------------------------


end;
/
show errors;
