package org.jbpm.samarjit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.samarjit.dao.WorkflowDAO;
import org.jbpm.samarjit.mynodeinst.StatelessEndNodeInstance;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.EventNode;
import org.jbpm.workflow.core.node.EventNodeInterface;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.NodeInstance;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.node.EventNodeInstance;
import org.jbpm.workflow.instance.node.EventNodeInstanceInterface;


public class StatelessProcessInstance  implements StatelessWorkflowEvent,WorkflowProcessInstance, org.drools.runtime.process.ProcessInstance, NodeInstanceContainer {
	private Process currentProcess;
	private Map<String, List<EventListener>> eventListeners = new HashMap<String, List<EventListener>>();
	private Map<String, List<EventListener>> externalEventListeners = new HashMap<String, List<EventListener>>();
	private int state = STATE_PENDING;
	private long nodeInstanceCounter = 0;
	private final List<NodeInstance> nodeInstances = new ArrayList<NodeInstance>();
	private long id = 0;
	private Map<String, Object> variables = new HashMap<String, Object>();
	
	public StatelessProcessInstance(Process p){
		currentProcess = p;
	}
	
	public void internalStart() {
    	StartNode startNode = getRuleFlowProcess().getStart();
    	if (startNode != null) {
    		NodeInstance  startNodeInst = ((NodeInstance) getNodeInstance(startNode));
    		WorkflowDAO.createProcessInstance(startNodeInst);
    		startNodeInst.trigger(null, null);
    	}
    	
    }
	
	public void start() {
    	synchronized (this) {
            if ( getState() != ProcessInstanceImpl.STATE_PENDING ) {
                throw new IllegalArgumentException( "A process instance can only be started once" );
            }
            setState( ProcessInstanceImpl.STATE_ACTIVE );
            internalStart();
		}
    }

	
	private RuleFlowProcess getRuleFlowProcess() {
		return (RuleFlowProcess) getProcess();
	}

	public String toString() {
		final StringBuilder sb = new StringBuilder("WorkflowProcessInstance:");
		sb.append(getId());
		sb.append(" [processId=");
		sb.append(getProcessId());
		sb.append(",state=");
		sb.append(getState());
		sb.append("]");
		return sb.toString();
	}

	



	
	public void signalEvent(String type, Object event) {
		// TODO Auto-generated method stub
		System.out.println("Stateless Process Instance signalEvent empty()()()"+event);
		synchronized (this) {
			if (getState() != ProcessInstance.STATE_ACTIVE) {
				return;
			}
			List<EventListener> listeners = eventListeners.get(type);
			if (listeners != null) {
				for (EventListener listener : listeners) {
					listener.signalEvent(type, event);
				}
			}
			listeners = externalEventListeners.get(type);
			if (listeners != null) {
				for (EventListener listener : listeners) {
					listener.signalEvent(type, event);
				}
			}
			for (org.drools.definition.process.Node node : getWorkflowProcess().getNodes()) {
				if (node instanceof EventNodeInterface) {
					if (((EventNodeInterface) node).acceptsEvent(type, event)) {
						if (node instanceof EventNode && ((EventNode) node).getFrom() == null) {
							EventNodeInstance eventNodeInstance = (EventNodeInstance) getNodeInstance(node);
							eventNodeInstance.signalEvent(type, event);
						} else {
							List<NodeInstance> nodeInstances = getNodeInstances(node
									.getId());
							if (nodeInstances != null && !nodeInstances.isEmpty()) {
								for (NodeInstance nodeInstance : nodeInstances) {
									((EventNodeInstanceInterface) nodeInstance)
											.signalEvent(type, event);
								}
							}
						}
					}
				}
			}
			if (((org.jbpm.workflow.core.WorkflowProcess) getWorkflowProcess()).isDynamic()) {
				for (org.drools.definition.process.Node node : getWorkflowProcess().getNodes()) {
					if (type.equals(node.getName()) && node.getIncomingConnections().isEmpty()) {
		    			NodeInstance nodeInstance = getNodeInstance(node);
		                ((org.jbpm.workflow.instance.NodeInstance) nodeInstance)
		                	.trigger(null, NodeImpl.CONNECTION_DEFAULT_TYPE);
		    		}
				}
			}
		}
		
	}



	
	public String[] getEventTypes() {
		// TODO Auto-generated method stub
		return null;
	}



	
	public String getProcessId() {
		// TODO Auto-generated method stub
		return currentProcess.getId();
	}



	
	public Process getProcess() {
		return currentProcess;
	}



	
	public long getId() {
		return id;
	}

	public void setId(long id){
		this.id  = id;
	}

	
	public String getProcessName() {
		// TODO Auto-generated method stub
		return currentProcess.getName();
	}



	
	public int getState() {
		 return this.state;
	}

	
	

	
	public org.drools.runtime.process.NodeInstance getNodeInstance(long nodeInstanceId) {
		for (NodeInstance nodeInstance: nodeInstances) {
			if (nodeInstance.getId() == nodeInstanceId) {
				return nodeInstance;
			}
		}
		return null;
	}

	
	public Object getVariable(String paramString) {
		// TODO Auto-generated method stub
		return variables.get(paramString);
	}

	
	public void setVariable(String paramString, Object paramObject) {
		// TODO Auto-generated method stub
		variables .put(paramString, paramObject);
	}

	//org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl
	
	public void addEventListener(String type, EventListener listener, boolean external) {
		Map<String, List<EventListener>> eventListeners = 
			external ? this.externalEventListeners : this.eventListeners;
		List<EventListener> listeners = eventListeners.get(type);
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<EventListener>();
			eventListeners.put(type, listeners);
			if (external) { throw new UnsupportedOperationException("External Events ");
//				((InternalProcessRuntime) getKnowledgeRuntime().getProcessRuntime())
//					.getSignalManager().addEventListener(type, this);
			}
		}
		listeners.add(listener);
	}

	
	public void removeEventListener(String type, EventListener listener, boolean external) {
		Map<String, List<EventListener>> eventListeners = external ? this.externalEventListeners
				: this.eventListeners;
		List<EventListener> listeners = eventListeners.get(type);
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				eventListeners.remove(type);
				if (external) {
					StatelessRuntime.eINSTANCE.getSignalManager().removeEventListener(type, this);
				}
			}
		}
	}
	public void setState(int stateAborted) {
		 internalSetState(stateAborted);
		// TODO move most of this to ProcessInstanceImpl
		if (state == ProcessInstance.STATE_COMPLETED
				|| state == ProcessInstance.STATE_ABORTED) {
			StatelessRuntime.eINSTANCE.getEventSupport().fireBeforeProcessCompleted(this, null/*kruntime*/);
			// deactivate all node instances of this process instance
			while (!nodeInstances.isEmpty()) {
				NodeInstance nodeInstance = nodeInstances.get(0);
				((org.jbpm.workflow.instance.NodeInstance) nodeInstance)
						.cancel();
			}
			removeEventListeners();
			StatelessRuntime.eINSTANCE.getProcessInstanceManager().removeProcessInstance(this);
			StatelessRuntime.eINSTANCE.getEventSupport().fireAfterProcessCompleted(this, null/*kruntime*/);

			StatelessRuntime.eINSTANCE.getSignalManager().signalEvent("processInstanceCompleted:" + getId(), this);
			WorkflowDAO.completeProcessInstance(getId());
		}
	}
	
	private void removeEventListeners() {
		for (String type : externalEventListeners.keySet()) {
			StatelessRuntime.eINSTANCE.getSignalManager().removeEventListener(type, this);
		}
	}
	
	
	    
	    public void internalSetState(final int state) {
	    	this.state = state;
	    }

	    
	    
	    
	    //jbpmn nodeInstanceContainer
	    public Collection<org.drools.runtime.process.NodeInstance> getNodeInstances() {
			return new ArrayList<org.drools.runtime.process.NodeInstance>(getNodeInstances(false));
		}

		public Collection<NodeInstance> getNodeInstances(boolean recursive) {
			Collection<NodeInstance> result = nodeInstances;
			if (recursive) {
				result = new ArrayList<NodeInstance>(result);
				for (Iterator<NodeInstance> iterator = nodeInstances.iterator(); iterator
						.hasNext();) {
					NodeInstance nodeInstance = iterator.next();
					if (nodeInstance instanceof NodeInstanceContainer) {
						result
								.addAll(((org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstance)
										.getNodeInstances(true));
					}
				}
			}
			return Collections.unmodifiableCollection(result);
		}
		 

		public NodeInstance getFirstNodeInstance(final long nodeId) {
			for (final Iterator<NodeInstance> iterator = this.nodeInstances
					.iterator(); iterator.hasNext();) {
				final NodeInstance nodeInstance = iterator.next();
				if (nodeInstance.getNodeId() == nodeId) {
					return nodeInstance;
				}
			}
			return null;
		}
		
		public List<NodeInstance> getNodeInstances(final long nodeId) {
			List<NodeInstance> result = new ArrayList<NodeInstance>();
			for (final Iterator<NodeInstance> iterator = this.nodeInstances
					.iterator(); iterator.hasNext();) {
				final NodeInstance nodeInstance = iterator.next();
				if (nodeInstance.getNodeId() == nodeId) {
					result.add(nodeInstance);
				}
			}
			return result;
		}
		
		/*public StartNode getStart() {
	        Node[] nodes = getNodes();
	        for (int i = 0; i < nodes.length; i++) {
	            if (nodes[i] instanceof StartNode) {
	                return (StartNode) nodes[i];
	            }
	        }
	        return null;
	    }*/
		
		/*private Node[] getNodes() {
			// TODO Auto-generated method stub
			return null;
		}*/
	
	
	
		/**
		 * Creates new node Instance based on node type
		 * @see org.jbpm.workflow.instance.NodeInstanceContainer#getNodeInstance(org.jbpm.workflow.core.Node)
		 */
		/*public NodeInstance getNodeInstance(final Node node) {
			INodeInstanceFactory conf = StatelessNodeInstanceFactoryRegistry.INSTANCE.getProcessNodeInstanceFactory(node);
			if (conf == null) {
				throw new IllegalArgumentException("Illegal node type: "
						+ node.getClass());
			}
			StatelessNodeInstanceImpl nodeInstance = (StatelessNodeInstanceImpl) conf
					.getNodeInstance(node, this, this);
			if (nodeInstance == null) {
				throw new IllegalArgumentException("Illegal node type: "
						+ node.getClass());
			}
			 
			return nodeInstance;
		}*/
	
		/**
		 * Creates new node instance based on node type
		 * @see org.jbpm.workflow.instance.NodeInstanceContainer#getNodeInstance(org.drools.definition.process.Node)
		 */
		public NodeInstance getNodeInstance(
				org.drools.definition.process.Node node) {
			  INodeInstanceFactory conf = StatelessNodeInstanceFactoryRegistry.INSTANCE.getProcessNodeInstanceFactory(node);
			if (conf == null) {
				throw new IllegalArgumentException("Illegal node type: "
						+ node.getClass());
			}
			StatelessNodeInstanceImpl nodeInstance = (StatelessNodeInstanceImpl) conf
					.getNodeInstance(node, this, this);
			if (nodeInstance == null) {
				throw new IllegalArgumentException("Illegal node type: "
						+ node.getClass());
			}
			
			return nodeInstance;
		}

		

		public void removeNodeInstance(NodeInstance nodeInstance) {
			this.nodeInstances.remove(nodeInstance);
		}

		public NodeContainer getNodeContainer() {
			return (WorkflowProcess) getProcess();
		}

		public void nodeInstanceCompleted(NodeInstance nodeInstance,
				String outType) {
			if (nodeInstance instanceof StatelessEndNodeInstance || 
	        		((org.jbpm.workflow.core.WorkflowProcess) getWorkflowProcess()).isDynamic()) {
	            if (((org.jbpm.workflow.core.WorkflowProcess) getProcess()).isAutoComplete()) {
	                if (nodeInstances.isEmpty()) {
	                    setState(ProcessInstance.STATE_COMPLETED);
	                }
	            }
	        } else {
	    		throw new IllegalArgumentException(
	    			"Completing a node instance that has no outgoing connection not suppoerted.");
	        }
		}

		private  WorkflowProcess getWorkflowProcess() {
			return (WorkflowProcess) getProcess();
		}

		public Map<String, Object> getVariableMap() {
			return variables;
		}
		
		/** 
		 * This defines the strategy for setting node instance Id
		 * @see org.jbpm.workflow.instance.NodeInstanceContainer#addNodeInstance(org.jbpm.workflow.instance.NodeInstance)
		 */
		public void addNodeInstance(NodeInstance nodeInstance) {
			System.err.println("addNodeInstance:"+id+"   "  + nodeInstanceCounter);
				((StatelessNodeInstanceImpl) nodeInstance).setId(id*1000 + nodeInstanceCounter++);
			this.nodeInstances.add(nodeInstance);
		}
		//does not work as while restart after sometime there will be fresh tasks starting
		@Deprecated 
		public void addNodeInstanceFromDB(NodeInstance nodeInstance) {
			System.err.println("addNodeInstancefromDB:"+id+"   "  + nodeInstanceCounter);
				((StatelessNodeInstanceImpl) nodeInstance).setId(  nodeInstanceCounter++);
				this.nodeInstances.add(nodeInstance);
		}
		
		/**
		 * This is an override to the inbuild id creation. This is to be used only during reload of half executed workflows, so that 
		 * there will be continuation of Ids.
		 * @param id
		 * @since 18 May, 2011
		 */
		public void setNodeInstanceCounter(long id){
			nodeInstanceCounter = id;
		}
	
}
