---WCMDOCUMENT Ϊҵ�����ݱ��������ݣ���Ҫ�ṩȫ�ļ��������û��Զ������ݱ�---
--����������ͼ--
create or replace view v_docudoment as
select docid,docchannel,doctitle,doccontent,appendixcontent from wcmdocument where siteid=8 and company<>'SGSBB'

--�������������ɼ���,�������򣺹���������ͼ��_TIME$_TEMP--
create table V_DOCUMENT_TIME$_TEMP
(
  SEQ_ID   NUMBER(10) not Null Primary Key,
  DOCID    NUMBER(10) not null,
  SQL_TYPE NUMBER(2) not null,
  TRS_FLAG NUMBER(2) not null
)
--���ݲɼ������У��������򣺹���������ͼ��_TIME$_SEQ--
create sequence V_DOCUMENT_TIME$_SEQ
minvalue 1
maxvalue 2147483647
start with 3015321
increment by 1
cache 20;

--ҵ�����ݱ��¼ɾ��������,�������򣺹���������ͼ��_TIME_D_TRIG--
CREATE OR REPLACE TRIGGER "V_DOCUMENT_TIME_D_TRIG" AFTER DELETE ON "WCMDOCUMENT" 
  FOR EACH ROW 
  
BEGIN 
    INSERT INTO "V_DOCUMENT_TIME$_TEMP" VALUES("V_DOCUMENT_TIME$_SEQ".NEXTVAL,:OLD."DOCID",3,0); 
  END;
--ҵ�����ݱ��¼��Ӵ�����,�������򣺹���������ͼ��_TIME_I_TRIG--
CREATE OR REPLACE TRIGGER "V_DOCUMENT_TIME_I_TRIG" AFTER INSERT ON "WCMDOCUMENT" 
  FOR EACH ROW 
  
BEGIN 
   INSERT INTO "V_DOCUMENT_TIME$_TEMP" VALUES("V_DOCUMENT_TIME$_SEQ".NEXTVAL,:NEW."DOCID",1,0); 
  END;
--ҵ�����ݱ��¼�޸Ĵ�����,�������򣺹���������ͼ��_TIME_U_TRIG--
  CREATE OR REPLACE TRIGGER "V_DOCUMENT_TIME_U_TRIG" AFTER UPDATE On "WCMDOCUMENT" 
  FOR EACH ROW 
  
BEGIN 
    INSERT INTO "V_DOCUMENT_TIME$_TEMP" VALUES("V_DOCUMENT_TIME$_SEQ".NEXTVAL,:OLD."DOCID",3,0); 
    INSERT INTO "V_DOCUMENT_TIME$_TEMP" VALUES("V_DOCUMENT_TIME$_SEQ".NEXTVAL,:NEW."DOCID",1,0); 
  END;