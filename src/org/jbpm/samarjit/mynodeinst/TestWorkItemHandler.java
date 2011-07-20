package org.jbpm.samarjit.mynodeinst;

import java.util.ArrayList;
import java.util.List;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class TestWorkItemHandler implements WorkItemHandler {
	
    private List<WorkItem> workItems = new ArrayList<WorkItem>();
    
    /**
     * On start of work item. Called from internalTrigger()
     */
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	workItems.add(workItem);
    	System.out.println("Workitem queuing "+workItems.size()+"  "+ workItem);
    }
    
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    }
    
    public WorkItem getWorkItem() {
    	if (workItems.size() == 0) {
    		return null;
    	}
    	if (workItems.size() == 1) {
    		WorkItem result = workItems.get(0);
    		this.workItems.clear();
    		return result;
    	} else {
    		System.err.println("Samarjit:://for testing removed exception!..More than one work item active");
    		WorkItem result = workItems.get(0);
    		this.workItems.remove(result);
    		return result;
    		//throw new IllegalArgumentException("More than one work item active");
    	}
    }
    
    public List<WorkItem> getWorkItems() {
    	List<WorkItem> result = new ArrayList<WorkItem>(workItems);
//    	workItems.clear();
    	return result;
    }
    
}