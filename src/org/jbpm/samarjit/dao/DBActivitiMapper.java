package org.jbpm.samarjit.dao;

import java.util.List;

import org.jbpm.samarjit.dto.ActRuExecution;

public interface DBActivitiMapper {
	List<ActRuExecution> selectRunningWorkflows();
}
