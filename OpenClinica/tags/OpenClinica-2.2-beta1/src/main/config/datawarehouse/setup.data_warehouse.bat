@REM A script to setup jobs to create the OpenClinica data warehouse daily at midnight.
@REM Verify that the location of the psql.exe and pg_data_warehouse.sql files are correct.

AT 00:00 /every:SUNDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties
AT 00:00 /every:MONDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties 
AT 00:00 /every:TUESDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties
AT 00:00 /every:WEDNESDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties
AT 00:00 /every:THURSDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties
AT 00:00 /every:FRIDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties
AT 00:00 /every:SUNDAY java -cp C:\OpenClinica\OpenClinica-2.0.1\conf;C:\OpenClinica\OpenClinica-2.0.1\conf\postgresql-8.1-405.jdbc3.jar UpdateWarehouseJDBC C:\OpenClinica\OpenClinica-2.0.1\conf\update_warehouse_jdbc.properties
