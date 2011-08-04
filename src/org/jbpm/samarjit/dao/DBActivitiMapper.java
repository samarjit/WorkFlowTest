package org.jbpm.samarjit.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jbpm.samarjit.dto.ActHiTaskinst;
import org.jbpm.samarjit.dto.ActRuExecution;
import org.jbpm.samarjit.dto.ActRuTask;
import org.jbpm.samarjit.mynodeinst.MockStatelessNodeInstance;

public interface DBActivitiMapper {
	List<ActRuExecution> selectRunningWorkflows();
	
	List<ActRuTask> selectRunTasks(String procInstId); //still not used 2nd Aug
	List<ActHiTaskinst> selectHiTasks(String procInstId); //still not used 2nd Aug
	 
	List<MockStatelessNodeInstance> selectMockRunTasks(String procInstId);
	List<MockStatelessNodeInstance> selectMockHiTasks(String procInstId);

	Map<String, String> getDeployedWfl(String wflId);

	void deployMakerInsert(@Param("rev") String rev,@Param("wflId") String wflId,@Param("filename") String filename,@Param("filedesc") String filedesc,@Param("maker") String maker);

	void deployMakerUpdate(@Param("rev") String rev,@Param("wflId") String wflId,@Param("filename") String filename,@Param("filedesc") String filedesc,@Param("maker") String maker);

	void saveImagePos(@Param("wflId") String processid,@Param("relx")  String imgposrelX,@Param("rely")  String imgposrelY);


 
	
	
 }
