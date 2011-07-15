package org.jbpm.samarjit.mynodeinst;


 
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;

import org.jbpm.samarjit.StatelessNodeInstanceImpl;
import org.jbpm.samarjit.StatelessProcessInstance;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;


public class StatelessEndNodeInstance   extends StatelessNodeInstanceImpl {

    private static final long serialVersionUID = 510l;

    public EndNode getEndNode() {
    	return (EndNode) getNode();
    }
    
    public void internalTrigger(final NodeInstance from, String type) {
        super.internalTrigger(from, type);
        if (!org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                "An EndNode only accepts default incoming connections!");
        }
        ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
        if (getEndNode().isTerminate()) {
        	boolean hidden = false;
        	if (getNode().getMetaData().get("hidden") != null) {
        		hidden = true;
        	}
        	 
        	if (!hidden) {
        		 getProcessEventSupport().fireBeforeNodeLeft(this, null /*kruntime*/);
        	}
        	//samarjit to completeEndNodeInstand
        	triggerCompleted(type, true);
        	//((StatelessProcessInstance) getProcessInstance()).setState( ProcessInstance.STATE_COMPLETED );//samarjit
            if (!hidden) {
            	 getProcessEventSupport().fireAfterNodeLeft(this, null /*kruntime*/);
            }
        } else {
            ((NodeInstanceContainer) getNodeInstanceContainer())
                .nodeInstanceCompleted(this, null);
        }
    }

}