package org.jbpm.samarjit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.openjpa.util.UnsupportedException;
import org.apache.xml.utils.UnImplNode;
import org.drools.WorkItemHandlerNotFoundException;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;

public class StatelessWorkItemManager implements WorkItemManager{
	private static final long serialVersionUID = 510L;
	/*     */   private long workItemCounter;
	/*  41 */   private Map<Long, WorkItem> workItems = new ConcurrentHashMap<Long, WorkItem>();
	/*     */  // private InternalKnowledgeRuntime kruntime;
	/*  43 */   private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
	
	/**
	 * Queue workItems so. workItemCounter is still starting from 0 since restart of workflow instance. These will become instance id
	 * and must be available in eventListener to be executed later on. Event listeners is added in 
	 * StatelessWorkItemNodeInstance.internalTrigger().addWokItemListener(thisInstance) and in the internalTrigger last line 
	 * workItemId is being set for that instance.
	 * @see org.drools.process.instance.WorkItemManager#internalExecuteWorkItem(org.drools.process.instance.WorkItem)
	 */
	//DefaultWorkItemManager
	
	public void internalExecuteWorkItem(WorkItem workItem) {
		/*  65 */     //TODO check ((WorkItemImpl)workItem).setId(++this.workItemCounter);
		/*  66 */     internalAddWorkItem(workItem);
		/*  67 */     WorkItemHandler handler = (WorkItemHandler)this.workItemHandlers.get(workItem.getName());
		/*  68 */     if (handler != null)
		/*  69 */       handler.executeWorkItem(workItem, this);
		/*     */     else 
			            throw new WorkItemHandlerNotFoundException("Could not find work item handler for " + workItem.getName(), workItem.getName());
		/*     */   }
		/*     */ 
	 public void internalAddWorkItem(WorkItem workItem)
		/*     */   {
		/*  75 */     this.workItems.put(new Long(workItem.getId()), workItem);
		/*     */ 
		/*  77 */     if (workItem.getId() > this.workItemCounter)
		/*  78 */       this.workItemCounter = workItem.getId();
		/*     */   }
		
		public void completeWorkItem(long id, Map<String, Object> results) {
			/* 107 */     WorkItem workItem = (WorkItem)this.workItems.get(new Long(id));
			/*     */ 
			/* 109 */     if (workItem != null) {
			/* 110 */       workItem.setResults(results);
			/* 111 */       ProcessInstance processInstance = StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(workItem.getProcessInstanceId());
			/* 112 */       workItem.setState(2);
			/*     */ 
			/* 114 */       if (processInstance != null) {
			/* 115 */         processInstance.signalEvent("workItemCompleted", workItem);
			/*     */       }
			/* 117 */       this.workItems.remove(new Long(id));
			/*     */     }
		}
		
		public void abortWorkItem(long id) {
			/* 122 */     WorkItemImpl workItem = (WorkItemImpl)this.workItems.get(new Long(id));
			/*     */ 
			/* 124 */     if (workItem != null) {
			/* 125 */       ProcessInstance processInstance = StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(workItem.getProcessInstanceId());
			/* 126 */       workItem.setState(3);
			/*     */ 
			/* 128 */       if (processInstance != null) {
			/* 129 */         processInstance.signalEvent("workItemAborted", workItem);
			/*     */       }
			/* 131 */       this.workItems.remove(new Long(id));
			/*     */     }
		}
		
		public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
			/* 136 */     this.workItemHandlers.put(workItemName, handler);
		}
		 
		 
		
		public void internalAbortWorkItem(long id) {
			/*  83 */     WorkItemImpl workItem = (WorkItemImpl)this.workItems.get(new Long(id));
			/*     */ 
			/*  85 */     if (workItem != null) {
			/*  86 */       WorkItemHandler handler = (WorkItemHandler)this.workItemHandlers.get(workItem.getName());
			/*  87 */       if (handler != null) {
			/*  88 */         handler.abortWorkItem(workItem, this);
			/*     */       } else {
			/*  90 */         this.workItems.remove(Long.valueOf(workItem.getId()));
			/*  91 */         throw new WorkItemHandlerNotFoundException("Could not find work item handler for " + workItem.getName(), workItem.getName());
			/*     */       }
			/*     */ 
			/*  94 */       this.workItems.remove(Long.valueOf(workItem.getId()));
			/*     */     }
		}
		
		public Set<WorkItem> getWorkItems() {
			/*  99 */     return new HashSet<WorkItem>(workItems.values());
		}
		
		public WorkItem getWorkItem(long id) {
			/* 103 */     return ((WorkItem)this.workItems.get(Long.valueOf(id)));
		}
		
		public void clear() {
			/* 140 */     this.workItems.clear();
		}	
		
		public void restoreWorkItems(){
			throw new UnsupportedException("restoreWorkItems():StatelessWorkItemManager");
		}
		public Map<String, WorkItemHandler> getWorkItemHandlers() {
			return workItemHandlers;
		}
}
