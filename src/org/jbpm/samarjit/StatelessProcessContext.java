package org.jbpm.samarjit;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessContext;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
public class StatelessProcessContext implements ProcessContext{
	
	private StatelessNodeInstanceImpl nodeInstance;
 
	
	
	public KnowledgeRuntime getKnowledgeRuntime() {
		return null;
	}

	public ProcessInstance getProcessInstance() {
		return nodeInstance.getProcessInstance();
	}

	public NodeInstance getNodeInstance() {
		return nodeInstance;
	}

    public Object getVariable(String variableName) {
        if (nodeInstance != null) {
            return nodeInstance.getVariable(variableName);
        } else {
            return ((WorkflowProcessInstance) getProcessInstance()).getVariable(variableName);
        }
    }
    
    public void setVariable(String variableName, Object value) {
        if (nodeInstance != null) {
            nodeInstance.setVariable(variableName, value);
        } else {
            ((WorkflowProcessInstance) getProcessInstance()).setVariable(variableName, value);
        }
    }

	public void setNodeInstance(StatelessNodeInstanceImpl nodeInstance) {
		this.nodeInstance = nodeInstance;
	}

}
