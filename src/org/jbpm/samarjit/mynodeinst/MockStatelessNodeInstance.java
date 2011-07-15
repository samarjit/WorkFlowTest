package org.jbpm.samarjit.mynodeinst;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.samarjit.StatelessNodeInstance;



/**
 * Created for Restarting join nodes. It requires from Node argument in 
 * internalTrigger(from, string. getNodeId() must be implemented in order to
 * make join nodes work properly
 * @author Samarjit
 *
 */
public class MockStatelessNodeInstance implements StatelessNodeInstance {

	public void setId(long id) {
		this.id = id;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	private long id;
    private long nodeId;
    
	@Override
	public String toString() {
		return "MockStatelessNodeInstance [id=" + id + ", nodeId=" + nodeId
				+ "]";
	}

	
	public void trigger(NodeInstance from, String type) {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public void cancel() {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public Node getNode() {
		return null;
	}

	public ContextInstance resolveContextInstance(String contextId, Object param) {
		return null;
	}

	public long getId() {
		return id;
	}

	public long getNodeId() {
		return nodeId;
	}

	public NodeInstanceContainer getNodeInstanceContainer() {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public String getNodeName() {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public WorkflowProcessInstance getProcessInstance() {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public Object getVariable(String arg0) {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public void setVariable(String arg0, Object arg1) {
		throw new RuntimeException("Unimplemented for mock node");
	}

}
