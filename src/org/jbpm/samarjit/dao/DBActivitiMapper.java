package org.jbpm.samarjit.dao;

import java.util.List;

import org.jbpm.samarjit.dto.ActHiTaskinst;
import org.jbpm.samarjit.dto.ActRuExecution;
import org.jbpm.samarjit.dto.ActRuTask;
import org.jbpm.samarjit.mynodeinst.MockStatelessNodeInstance;

public interface DBActivitiMapper {
	List<ActRuExecution> selectRunningWorkflows();
	
	List<ActRuTask> selectRunTasks(String procInstId); //still not used 2nd Aug
	List<ActHiTaskinst> selectHiTasks(String procInstId); 
	 
	List<MockStatelessNodeInstance> selectMockRunTasks(String procInstId);
	List<MockStatelessNodeInstance> selectMockHiTasks(String procInstId);
 }
