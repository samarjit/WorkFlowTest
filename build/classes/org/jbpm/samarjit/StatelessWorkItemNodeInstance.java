package org.jbpm.samarjit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.drools.WorkItemHandlerNotFoundException;
import org.drools.process.core.Work;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.impl.NodeInstanceResolverFactory;
import org.jbpm.workflow.instance.impl.WorkItemResolverFactory;
import org.mvel2.MVEL;

public class StatelessWorkItemNodeInstance extends StatelessNodeInstanceImpl{
	@SuppressWarnings("unused")
	private static final Pattern PARAMETER_MATCHER = Pattern.compile("#\\{(\\S+)\\}", Pattern.DOTALL);
	private transient WorkItem workItem;
	private long workItemId = -1;
	private List<Long> timerInstances;
	
	public String toString(){
		return "["+getClass().getSimpleName()+"("+getNodeName()+"):_"+getNodeId()+":inst:"+getId()+", workItemId:"+workItemId+"]";
	}
	
//	public void internalTrigger(final StatelessNodeInstance from, String type) {
//	    	super.internalTrigger(from, type);
//	        // TODO this should be included for ruleflow only, not for BPEL
////	        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
////	            throw new IllegalArgumentException(
////	                "A WorkItemNode only accepts default incoming connections!");
////	        }
//	        WorkItemNode workItemNode = getWorkItemNode();
//	        createWorkItem(workItemNode);
//			if (workItemNode.isWaitForCompletion()) {
//			    addWorkItemListener();
//	        }
//			 
//				try {
//				    ((StatelessWorkItemManager) StatelessRuntime.eINSTANCE.getWorkItemManager()).internalExecuteWorkItem(
//		    				(org.drools.process.instance.WorkItem) workItem);
//			    } catch (WorkItemHandlerNotFoundException wihnfe){
//			        getProcessInstance().setState( ProcessInstance.STATE_ABORTED );
//			        throw wihnfe;
//			    }
//			 
//	        if (!workItemNode.isWaitForCompletion()) {
//	            triggerCompleted();
//	        }
//	    	this.workItemId = workItem.getId();
//	    }
	
	public void restartWorkItemInst(){
		 WorkItemNode workItemNode = getWorkItemNode();
	        createWorkItem(workItemNode);
			if (workItemNode.isWaitForCompletion()) {
			    addWorkItemListener();
	        }
			this.workItemId = workItem.getId();	
	}
	 public void triggerCompleted() {
	        triggerCompleted(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, true);
	    }
	 
	 protected WorkItemNode getWorkItemNode() {
	        return (WorkItemNode) getNode();
	    }
	 private void addWorkItemListener() {
	    	getProcessInstance().addEventListener("workItemCompleted", this, false);
	    	getProcessInstance().addEventListener("workItemAborted", this, false);
	    }
	 
	 //WorkItemNodeInstance{ signalEvent()
	 public void signalEvent(String type, Object event) {
	    	if ("workItemCompleted".equals(type)) {
	    		workItemCompleted((WorkItem) event);
	    	} else if ("workItemAborted".equals(type)) {
	    		workItemAborted((WorkItem) event);
	    	} else {
	    		super.signalEvent(type, event);
	    	}
	    }
	 
	 public void workItemCompleted(WorkItem workItem) {
	        if ( workItemId == workItem.getId()
	        		|| ( workItemId == -1 && getWorkItem().getId() == workItem.getId()) ) {
	            removeEventListeners();
	            triggerCompleted(workItem);
	        }
	    }
	 public WorkItem getWorkItem() {
	    	if (workItem == null && workItemId >= 0) {
	    		workItem = (StatelessRuntime.eINSTANCE.getWorkItemManager()).getWorkItem(workItemId);
	    	}
	        return workItem;
	    }
	 public void workItemAborted(WorkItem workItem) {
	        if ( workItemId == workItem.getId()
	        		|| ( workItemId == -1 && getWorkItem().getId() == workItem.getId()) ) {
	            removeEventListeners();
	            triggerCompleted(workItem);
	        }
	    }
	//WorkItemNodeInstance}
	 //Extended Node instance trigger + WorkItemNodeInstance internalTrigger()
	 @Override	
	 public void internalTrigger(NodeInstance from, String type) {
			super.internalTrigger(from, type);
			
			//WorkItemNodeInstance{
			WorkItemNode workItemNode = getWorkItemNode();
	        createWorkItem(workItemNode);
			if (workItemNode.isWaitForCompletion()) {
			    addWorkItemListener();
	        }
			
			try {
			    (StatelessRuntime.eINSTANCE.getWorkItemManager()).internalExecuteWorkItem(
	    				(org.drools.process.instance.WorkItem) workItem);
		    } catch (WorkItemHandlerNotFoundException wihnfe){
		        getProcessInstance().setState( ProcessInstance.STATE_ABORTED );
		        throw wihnfe;
		    }
		    //WorkItemNodeInstance}
		    
		    if (!workItemNode.isWaitForCompletion()) {
	            triggerCompleted();
	        }
	    	this.workItemId = workItem.getId();
	    	
	    	
			// activate timers
			Map<Timer, DroolsAction> timers = getEventBasedNode().getTimers();
			if (timers != null) {
				addTimerListener();
				timerInstances = new ArrayList<Long>(timers.size());
				TimerManager timerManager = StatelessRuntime.eINSTANCE.getTimerManager();
				for (Timer timer: timers.keySet()) {
					TimerInstance timerInstance = createTimerInstance(timer); 
					timerManager.registerTimer(timerInstance, (org.jbpm.process.instance.ProcessInstance) getProcessInstance());
					timerInstances.add(timerInstance.getId());
				}
			}
		}

		
		 
		protected WorkItem createWorkItem(WorkItemNode workItemNode) {
			Work work = workItemNode.getWork();
	        workItem = new WorkItemImpl();
	        
	        ((WorkItemImpl) workItem) .setId(this.getId());
	        ((org.drools.process.instance.WorkItem) workItem).setName(work.getName());
	        ((org.drools.process.instance.WorkItem) workItem).setProcessInstanceId(getProcessInstance().getId());
	        ((org.drools.process.instance.WorkItem) workItem).setParameters(new HashMap<String, Object>(work.getParameters()));
	        for (Iterator<Map.Entry<String, String>> iterator = workItemNode.getInMappings().entrySet().iterator(); iterator.hasNext(); ) {
	            Map.Entry<String, String> mapping = iterator.next();
	            Object parameterValue = null;
	            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
	            if (variableScopeInstance != null) {
	            	parameterValue = variableScopeInstance.getVariable(mapping.getValue());
	            } else {
	            	try {
	            		parameterValue = MVEL.eval(mapping.getValue(), new NodeInstanceResolverFactory(this));
	            	} catch (Throwable t) {
		                System.err.println("Could not find variable scope for variable " + mapping.getValue());
		                System.err.println("when trying to execute Work Item " + work.getName());
		                System.err.println("Continuing without setting parameter.");
	            	}
	            }
	            if (parameterValue != null) {
	            	((org.drools.process.instance.WorkItem) workItem).setParameter(mapping.getKey(), parameterValue);
	            }
	        }/*
	        for (Map.Entry<String, Object> entry: workItem.getParameters().entrySet()) {
	        	if (entry.getValue() instanceof String) {
	        		String s = (String) entry.getValue();
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
				                    System.err.println("when trying to replace variable in string for Work Item " + work.getName());
				                    System.err.println("Continuing without setting parameter.");
			                	}
			                }
	                	}
	                }
	                for (Map.Entry<String, String> replacement: replacements.entrySet()) {
	                	s = s.replace("#{" + replacement.getKey() + "}", replacement.getValue());
	                }
	                ((org.drools.process.instance.WorkItem) workItem).setParameter(entry.getKey(), s);
	        	}
	        }*/
	        return workItem;
		}

	public void triggerCompleted(WorkItem workItem) {
    	this.workItem = workItem;
    	WorkItemNode workItemNode = getWorkItemNode();
    	if (workItemNode != null) {
	        for (Iterator<Map.Entry<String, String>> iterator = getWorkItemNode().getOutMappings().entrySet().iterator(); iterator.hasNext(); ) {
	            Map.Entry<String, String> mapping = iterator.next();
	            VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
	                resolveContextInstance(VariableScope.VARIABLE_SCOPE, mapping.getValue());
	            if (variableScopeInstance != null) {
	            	Object value = workItem.getResult(mapping.getKey());
	            	if (value == null) {
	            		try {
	                		value = MVEL.eval(mapping.getKey(), new WorkItemResolverFactory(workItem));
	                	} catch (Throwable t) {
	                		// do nothing
	                	}
	            	}
	                variableScopeInstance.setVariable(mapping.getValue(), value);
	            } else {
	                System.err.println("Could not find variable scope for variable " + mapping.getValue());
	                System.err.println("when trying to complete Work Item " + workItem.getName());
	                System.err.println("Continuing without setting variable.");
	            }
	        }
    	}
         
            triggerCompleted();
         
    }
		
}
