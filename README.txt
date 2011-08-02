select * from ACT_RU_TASK order by id_ ;
select * from ACT_HI_TASKINST order by execution_id_;

--alter table ID_ drop screen_URL_;
 

select * from ACT_RU_EXECUTION ;
--select * from INformation_schema.tables  where table_name in ('ACT_HI_PROCINST','ACT_RU_EXECUTION');


select * from ACT_HI_PROCINST ;



--delete from act_ru_identitylink ; delete from ACT_RU_TASK ; delete from  ACT_RU_EXECUTION ;delete from ACT_HI_TASKINST ; delete from ACT_HI_PROCINST; commit;
--update act_ge_property  set value_ ='231' where name_  = 'next.dbid';

--select * from ACT_GE_BYTEARRAY ;
--select * from ACT_RU_IDENTITYLINK;


mvn eclipse:eclipse
mvn antrun:run -P start.h2
mvn antrun:run -P stop.h2
 