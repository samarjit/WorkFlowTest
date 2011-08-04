package org.jbpm.samarjit.mynodeinst;

import org.apache.commons.lang.xwork.NotImplementedException;
import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.samarjit.StatelessNodeInstance;
import org.jbpm.samarjit.StatelessNodeInstanceImpl;



/**
 * Created for Restarting join nodes. It requires from Node argument in 
 * internalTrigger(from, string. getNodeId() must be implemented in order to
 * make join nodes work properly
 * @author Samarjit
 *
 */
public class MockStatelessNodeInstance implements StatelessNodeInstance {

	private long id;
	private long nodeId;
	private String nodeName;
	private String processid;
	
	private int state =  StatelessNodeInstanceImpl.STARTED; //0 - state is required for differentiating history tasks from running tasks 
	
	public int getState(){
		return state;
	}
	public void setState(int statenew){
		this.state = statenew;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

    
	@Override
	public String toString() {
		return "MockStatelessNodeInstance [id=" + id + ", nodeId=" + nodeId +" state="+state
				+ "]";
	}

	
	public void trigger(NodeInstance from, String type) {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public void cancel() {
		throw new RuntimeException("Unimplemented for mock node");
	}

	public Node getNode() {
		throw new NotImplementedException("Unimplemented for mock node");
		//return null;
	}

	public ContextInstance resolveContextInstance(String contextId, Object param) {
		throw new NotImplementedException("Unimplemented for mock node");
		//return null;
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
		return this.nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
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
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	
}
