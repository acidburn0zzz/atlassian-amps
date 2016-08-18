-- This script must be run as a user with the "SYSDBA" role
DECLARE
  v_count INTEGER := 0;
  v_sid VARCHAR2(20);
BEGIN
  SELECT SYS_CONTEXT('userenv','instance_name') INTO v_sid FROM DUAL;

  -- Ensure we're in the root container
  EXECUTE IMMEDIATE 'ALTER SESSION SET CONTAINER=CDB$ROOT';

  -- Configure the Data Pump directory
  EXECUTE IMMEDIATE q'{CREATE OR REPLACE DIRECTORY DATA_PUMP_DIR AS 'v_data_pump_dir'}';

  -- Does the JIRA pluggable DB exist?
  SELECT COUNT (1) INTO v_count FROM cdb_pdbs WHERE pdb_name = 'JIRA_PDB';
  IF v_count > 0
  THEN
    -- Yes, close and drop it
    EXECUTE IMMEDIATE 'ALTER PLUGGABLE DATABASE JIRA_PDB CLOSE';
    EXECUTE IMMEDIATE 'DROP PLUGGABLE DATABASE JIRA_PDB INCLUDING DATAFILES';
  END IF;

  -- [Re]create the JIRA pluggable DB, switch to it, and open it
  EXECUTE IMMEDIATE 'CREATE PLUGGABLE DATABASE JIRA_PDB ' ||
                    'ADMIN USER jira_dba IDENTIFIED BY jira_dba ' ||
                    'FILE_NAME_CONVERT = (''/u01/app/oracle/oradata/' || v_sid || '/pdbseed/'',''/u01/app/oracle/oradata/' || v_sid || '/JIRA_PDB/'')';
  EXECUTE IMMEDIATE 'ALTER SESSION SET CONTAINER=JIRA_PDB';
  EXECUTE IMMEDIATE 'ALTER PLUGGABLE DATABASE OPEN';

  -- Create the JIRA user/schema in the JIRA DB
  EXECUTE IMMEDIATE 'CREATE USER v_jira_user IDENTIFIED BY v_jira_pwd';
  EXECUTE IMMEDIATE 'GRANT CONNECT, RESOURCE, IMP_FULL_DATABASE TO v_jira_user';
  EXECUTE IMMEDIATE 'GRANT READ, WRITE ON DIRECTORY DATA_PUMP_DIR TO v_jira_user';
END;