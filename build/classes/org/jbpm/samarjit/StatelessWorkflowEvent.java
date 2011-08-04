package org.jbpm.samarjit;

import org.drools.runtime.process.EventListener;

public interface StatelessWorkflowEvent {
	//org.jbpm.workflow.instance.WorkflowProcessInstance
	void addEventListener(String type, EventListener eventListener, boolean external);
    
    void removeEventListener(String type, EventListener eventListener, boolean external);
}
