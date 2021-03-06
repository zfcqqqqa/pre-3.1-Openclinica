set serveroutput on
Rem Create User
Rem
Rem
prompt
Rem
prompt We will now setup the oracle user account that OpenClinica will use.
Rem
prompt
accept user_name prompt "Specify the wanted username for the OpenClinica oracle account: "
prompt
prompt The user &user_name will be created with a default tablespace of openclinica.
Rem
create user &user_name identified by clinica
default tablespace openclinica
quota 0 on system;
Rem
Rem
Rem
Rem Grant roles and privileges
Rem
Rem
Rem
grant connect, resource to &user_name;

grant create materialized view to &user_name;
grant create view to &user_name;
Rem
Rem

Rem
prompt
prompt Please check spool_oracle_setup_db.txt for errors
prompt
Rem
spool off
END;
/