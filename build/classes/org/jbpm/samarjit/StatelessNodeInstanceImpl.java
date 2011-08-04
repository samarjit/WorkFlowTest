package org.jbpm.samarjit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.RuntimeDroolsException;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.event.ProcessEventSupport;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.spi.ProcessContext;
import org.drools.time.TimeUtils;
import org.jbpm.process.core.Context;
import org.jbpm.process.core.ContextContainer;
import org.jbpm.process.core.context.exception.ExceptionScope;
import org.jbpm.process.core.context.exclusive.ExclusiveGroup;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.ContextInstanceContainer;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.process.instance.context.exception.ExceptionScopeInstance;
import org.jbpm.process.instance.context.exclusive.ExclusiveGroupInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.samarjit.dao.WorkflowDAO;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.core.node.StateBasedNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.node.EventBasedNodeInstanceInterface;
import org.mvel2.MVEL;
 
public  abstract class StatelessNodeInstanceImpl implements StatelessNodeInstance, EventBasedNodeInstanceInterface, EventListener, Comparable<NodeInstance>  {

	private static final long serialVersionUID = 511l;
	private static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{(\\S+)\\}", Pattern.DOTALL);
	public static final int STARTED = 0;
	public static final int COMPLETED = 2;
	
	private long id;
    private long nodeId;
    private StatelessProcessInstance processInstance;
    //StatelessProcessInstance is also containing nodesInstances
    private org.jbpm.workflow.instance.NodeInstanceContainer nodeInstanceContainer;
    
    private List<Long> timerInstances;
	private Map<String, Object> variables = new HashMap<String, Object>();
	private int state = 0; 
	
	public int getState(){
		return state;
	}
	public void setState(int statenew){
		this.state = statenew;
	}
	public final void trigger(NodeInstance from, String type) {
    	boolean hidden = false;
    	if (getNode().getMetaData().get("hidden") != null) {
    		hidden = true;
    	}
    	if (!hidden) {
    		setState(STARTED);
    		WorkflowDAO.createNodeInstance(this);
    		getProcessEventSupport().fireBeforeNodeTriggered(this, null /*kruntime*/);
    	}System.out.println("StatelessNodeInstance():trigger..."+from);
        internalTrigger(from, type);
        if (!hidden) {
        	getProcessEventSupport().fireAfterNodeTriggered(this,null /*kruntime*/);
        }
    }
	
	
	protected ProcessEventSupport getProcessEventSupport() {
		return StatelessRuntime.eINSTANCE.getEventSupport();
	}

//	public abstract void internalTrigger(NodeInstance from, String type);
	//internalTrigger implementation is required to be done
	
	/**
	 * This internalTrigger does not call to triggerCompleted() because by default every node is not self completing. A subclass's
	 * internalTrigger() method is to be called in case of self completing nodes. For example an action node is self completing node
	 * but a workItem node is not self completing node.
	 * @param from
	 * @param type
	 */
	public void internalTrigger(NodeInstance from, String type){
		triggerEvent(ExtendedNodeImpl.EVENT_NODE_ENTER);
		//StateBasedNodeInstance // activate timers
		if(getNode() instanceof StateBasedNode){
			Map<Timer, DroolsAction> timers = getEventBasedNode().getTimers();
			if (timers != null) {
				addTimerListener();
				timerInstances = new ArrayList<Long>(timers.size());
				TimerManager timerManager = StatelessRuntime.eINSTANCE.getTimerManager();
				for (Timer timer: timers.keySet()) {
					TimerInstance timerInstance = createTimerInstance(timer); 
					timerManager.registerTimer(timerInstance, (ProcessInstance) getProcessInstance());
					timerInstances.add(timerInstance.getId());
				}
			}
		}
		System.out.println("StatelessNodeInstance.. internalTrigger() trigger!");
		
	}

	 protected TimerInstance createTimerInstance(Timer timer) {
	    	TimerInstance timerInstance = new TimerInstance();
	    	timerInstance.setDelay(resolveValue(timer.getDelay()));
	    	if (timer.getPeriod() == null) {
	    		timerInstance.setPeriod(0);
	    	} else {
	    		timerInstance.setPeriod(resolveValue(timer.getPeriod()));
	    	}
	    	timerInstance.setTimerId(timer.getId());
	    	return timerInstance;
	    }
	
	 private long resolveValue(String s) {
	    	try {
	    		return TimeUtils.parseTimeString(s);
	    	} catch (RuntimeDroolsException e) {
	    		// cannot parse delay, trying to interpret it
	    		Map<String, String> replacements = new HashMap<String, String>();
	    		Matcher matcher = PARAMETER_MATCHER.matcher(s);
	            while (matcher.find()) {
	            	String paramName = matcher.group(1);
	            	if (replacements.get(paramName) == null) {
	                	VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	                    	resolveContextInstance(VariableScope.VARIABLE_SCOPE, paramName);
	                    if (variableScopeInstance != null) {
	                        Object variableValue = variableScopeInstance.getVariable(paramName);
	                    	String variableValueString = variableValue == null ? "" : variableValue.toString(); 
	    	                replacements.put(paramName, variableValueString);
	                    } else {
	                    	try {
	                    		Object variableValue = MVEL.eval(paramName, new NodeInstanceResolverFactory(this));
	    	                	String variableValueString = variableValue == null ? "" : variableValue.toString();
	    	                	replacements.put(paramName, variableValueString);
	                    	} catch (Throwable t) {
	    	                    System.err.println("Could not find variable scope for variable " + paramName);
	    	                    System.err.println("when trying to replace variable in processId for sub process " + getNodeName());
	    	                    System.err.println("Continuing without setting process id.");
	                    	}
	                    }
	            	}
	            }
	            for (Map.Entry<String, String> replacement: replacements.entrySet()) {
	            	s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
	            }
	            return TimeUtils.parseTimeString(s);
	    	}
	    }
	 
	protected void triggerEvent(String type) {
		ExtendedNodeImpl extendedNode =   (ExtendedNodeImpl) getNode();
		if (extendedNode == null) {
			return;
		}
		List<DroolsAction> actions = extendedNode.getActions(type);
		if (actions != null) {
			for (DroolsAction droolsAction: actions) {
				executeAction(droolsAction);
			}
		}
	}
	
	protected void executeAction(DroolsAction droolsAction) {
		Action action = (Action) droolsAction.getMetaData("Action");
		ProcessContext context = new ProcessContext(null/*getProcessInstance().getKnowledgeRuntime()*/);
		StatelessRuntime.eINSTANCE.setNodeInstance(this);
		try {
			action.execute(context); //????????????samarjit how does this works?
		} catch (Exception exception) {
			exception.printStackTrace();
			String exceptionName = exception.getClass().getName();
			ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
				resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, exceptionName);
			if (exceptionScopeInstance == null) {
				exception.printStackTrace();
				throw new IllegalArgumentException(
					"Could not find exception handler for " + exceptionName + " while executing node " + getNodeId());
			}
			exceptionScopeInstance.handleException(exceptionName, exception);
		}
	}
	//extended node impl end
	public String toString(){
		return "["+getClass().getSimpleName()+"("+getNodeName()+"):_"+getNodeId()+":inst:"+getId()+"]";
	}
	protected void triggerCompleted(String type, boolean remove) {
		cancelTimers();
		System.err.println("StatelessNodeInstance():Trigger completed:"+type+" "+this+" remove="+remove);
		if (remove) {
            ((org.jbpm.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
            	.removeNodeInstance(this);
            setState(COMPLETED);//completed
            WorkflowDAO.completeNodeInstance(this);
        }
        Node node = getNode();
        List<Connection> connections = null;
        if (node != null) {
        	connections = node.getOutgoingConnections(type);
        }
        if (connections == null || connections.isEmpty()) {
        	((org.jbpm.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
        		.nodeInstanceCompleted(this, type);
        } else {
	        for (Connection connection: connections) {
	        	// stop if this process instance has been aborted / completed
	        	if (getProcessInstance().getState() != ProcessInstance.STATE_ACTIVE) {
	        		return;
	        	}
	    		triggerConnection(connection);
	        }
        }
    }	
	
	
	protected void triggerConnection(Connection connection) {
    	boolean hidden = false;
    	if (getNode().getMetaData().get("hidden") != null) {
    		hidden = true;
    	}
    	if (!hidden) {
    		 getProcessEventSupport().fireBeforeNodeLeft(this, null);
    	}
    	// check for exclusive group first
    	NodeInstanceContainer parent = getNodeInstanceContainer();
    	if (parent instanceof ContextInstanceContainer) {
    		List<ContextInstance> contextInstances = ((ContextInstanceContainer) parent).getContextInstances(ExclusiveGroup.EXCLUSIVE_GROUP);
    		if (contextInstances != null) {
    			for (ContextInstance contextInstance: new ArrayList<ContextInstance>(contextInstances)) {
    				ExclusiveGroupInstance groupInstance = (ExclusiveGroupInstance) contextInstance;
    				if (groupInstance.containsNodeInstance(this)) {
    					for (NodeInstance nodeInstance: groupInstance.getNodeInstances()) {
    						if (nodeInstance != this) {
    							((org.jbpm.workflow.instance.NodeInstance) nodeInstance).cancel();
    						}
    					}
    					((ContextInstanceContainer) parent).removeContextInstance(ExclusiveGroup.EXCLUSIVE_GROUP, contextInstance);
    				}
    				
    			}
    		}
    	}
    	// trigger next node
        ((org.jbpm.workflow.instance.NodeInstance) ((org.jbpm.workflow.instance.NodeInstanceContainer) getNodeInstanceContainer())
        	.getNodeInstance(connection.getTo()))
        	.trigger(this, connection.getToType());
        if (!hidden) {
        	 getProcessEventSupport().fireAfterNodeLeft(this, null);
        }
    }

	public void setId(final long id) {
        this.id = id;
    }
	
	
	public long getId() {
		 return this.id;
	}

	public void setNodeId(final long nodeId) {
		this.nodeId = nodeId;
	}

	
	public long getNodeId() {
		return this.nodeId;
	}


	
	public String getNodeName() {
		Node node = getNode();
    	return node == null ? "" : node.getName();
	}

	public void setProcessInstance(final StatelessProcessInstance processInstance) {
	    this.processInstance = processInstance;
	}

	
	public StatelessProcessInstance getProcessInstance() {
		  return this.processInstance;
	}


	
	public NodeInstanceContainer getNodeInstanceContainer() {
		 return this.nodeInstanceContainer;
	}

	public void setNodeInstanceContainer(NodeInstanceContainer nodeInstanceContainer) {
        this.nodeInstanceContainer = (org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstanceContainer;
        if (nodeInstanceContainer != null) {
            this.nodeInstanceContainer.addNodeInstance(this);
        }
    }
	//does not work as while restart after sometime there will be fresh tasks starting
	@Deprecated
	public void setNodeInstanceContainerFromDB(StatelessProcessInstance nodeInstanceContainer) {
		 this.nodeInstanceContainer = (org.jbpm.workflow.instance.NodeInstanceContainer) nodeInstanceContainer;
        if (nodeInstanceContainer != null) {
            ((StatelessProcessInstance) this.nodeInstanceContainer).addNodeInstanceFromDB(this);
        }
	}
	
	public Object getVariable(String paramString) {
		return variables.get(paramString);
	}


	
	public void setVariable(String paramString, Object paramObject) {
		// TODO Auto-generated method stub
		variables.put(paramString,paramObject);
	}




	
	public void cancel() {
		//statebasedNodeInstance 
		cancelTimers();
	     removeEventListeners(); 
	     //NodeInstanc
		nodeInstanceContainer.removeNodeInstance(this);
	}


	
	public Node getNode() {
		 return ((org.jbpm.workflow.core.NodeContainer)
		    		this.nodeInstanceContainer.getNodeContainer()).internalGetNode( this.nodeId );
	}

	public Context resolveContext(String contextId, Object param) {
        return ((NodeImpl) getNode()).resolveContext(contextId, param);
    }
	
	private ContextInstanceContainer getContextInstanceContainer(ContextContainer contextContainer) {
    	ContextInstanceContainer contextInstanceContainer = null; 
		if (this instanceof ContextInstanceContainer) {
        	contextInstanceContainer = (ContextInstanceContainer) this;
        } else {
        	contextInstanceContainer = getEnclosingContextInstanceContainer(this);
        }
        while (contextInstanceContainer != null) {
    		if (contextInstanceContainer.getContextContainer() == contextContainer) {
    			return contextInstanceContainer;
    		}
    		contextInstanceContainer = getEnclosingContextInstanceContainer(
				(NodeInstance) contextInstanceContainer);
    	}
        return null;
    }
	
	 private ContextInstanceContainer getEnclosingContextInstanceContainer(NodeInstance nodeInstance) {
	    	NodeInstanceContainer nodeInstanceContainer = nodeInstance.getNodeInstanceContainer();
	    	while (true) {
	    		if (nodeInstanceContainer instanceof ContextInstanceContainer) {
	    			return (ContextInstanceContainer) nodeInstanceContainer;
	    		}
	    		if (nodeInstanceContainer instanceof NodeInstance) {
	    			nodeInstanceContainer = ((NodeInstance) nodeInstanceContainer).getNodeInstanceContainer();
	    		} else {
	    			return null;
	    		}
	    	}
	    }
	 
	
	public ContextInstance resolveContextInstance(String contextId, Object param) {
		 Context context = resolveContext(contextId, param);
	        if (context == null) {
	            return null;
	        }
	        ContextInstanceContainer contextInstanceContainer
	        	= getContextInstanceContainer(context.getContextContainer());
	        if (contextInstanceContainer == null) {
	        	throw new IllegalArgumentException(
	    			"Could not find context instance container for context");
	        }
	        return contextInstanceContainer.getContextInstance(context);
	}


	
	///////Events//////////


	
	public void signalEvent(String type, Object event) {
		if ("timerTriggered".equals(type)) {
    		TimerInstance timerInstance = (TimerInstance) event;
            if (timerInstances.contains(timerInstance.getId())) {
                triggerTimer(timerInstance);
            }
    	}
	}

	private void triggerTimer(TimerInstance timerInstance) {
	    	for (Map.Entry<Timer, DroolsAction> entry: getEventBasedNode().getTimers().entrySet()) {
	    		if (entry.getKey().getId() == timerInstance.getTimerId()) {
	    			executeAction(entry.getValue());
	    			return;
	    		}
	    	}
	 }
	
	public StateBasedNode getEventBasedNode() {
		 return (StateBasedNode) getNode();
	}


	
	public String[] getEventTypes() {
		return new String[] { "timerTriggered" };
	}


	
	public void addEventListeners() {
		if (timerInstances != null && timerInstances.size() > 0) {
    		addTimerListener();
    	}
	}
	
    protected void addTimerListener() {
    	getProcessInstance().addEventListener("timerTriggered", this, false);
    }

	
	public void removeEventListeners() {
		 getProcessInstance().removeEventListener("timerTriggered", this, false);
	}
	
	private void cancelTimers() {
		// deactivate still active timers
		if (timerInstances != null) {
			TimerManager timerManager = StatelessRuntime.eINSTANCE.getTimerManager();
			for (Long id: timerInstances) {
				timerManager.cancelTimer(id);
			}
		}
	}

	public Map<String, Object> getVariableMap() {
		return variables;
	}


	public int compareTo(NodeInstance o) {
		return (int) ( o.getId() - this.id);
	}


	


	 
	 
}
