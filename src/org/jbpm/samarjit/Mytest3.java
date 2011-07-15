package org.jbpm.samarjit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;
import org.xml.sax.SAXException;

public class Mytest3 {
	public static void main(String args[]) throws FileNotFoundException, SAXException, IOException{
		StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
		swflMgr.readWorkflowFiles(Mytest3.class.getResourceAsStream("Evaluation.bpmn"));
		final List<String> processEventList = new ArrayList<String>();
		final ProcessEventListener processEventListener = new ProcessEventListener() {
			public void afterNodeLeft(ProcessNodeLeftEvent event) {
//				processEventList.add(event);
			}

			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
				processEventList.add(event.toString()+" \n");
			}

			public void afterProcessCompleted(ProcessCompletedEvent event) {
				processEventList.add(event+" process completed!\n");
			}

			public void afterProcessStarted(ProcessStartedEvent event) {
				processEventList.add(event+" process started...\n");
			}

			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
//				processEventList.add(event);
			}

			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
//				processEventList.add(event);
			}

			public void beforeProcessCompleted(ProcessCompletedEvent event) {
//				processEventList.add(event);
			}

			public void beforeProcessStarted(ProcessStartedEvent event) {
//				processEventList.add(event);
			}

			public void beforeVariableChanged(ProcessVariableChangedEvent event) {
				processEventList.add(event+" \n");
			}

			public void afterVariableChanged(ProcessVariableChangedEvent event) {
				processEventList.add(event+" \n");
			}
		};
		swflMgr.getRuntime().addEventListener(processEventListener);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
		swflMgr.restoreWorkflowSession();
		long currentProcessInst = StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstances().iterator().next().getId();
		System.out.println("Process instance length="+currentProcessInst);
		System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState() == ProcessInstance.STATE_ACTIVE));
		WorkItem workItem = workItemHandler.getWorkItem();
		System.out.println("WorkItemID:"+workItem.getId()+" "+workItem.getParameters().get("Comment"));
		swflMgr.completeWorkItem(workItem.getId(), null);
		System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState() == ProcessInstance.STATE_ACTIVE));
		swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
		 workItem = workItemHandler.getWorkItem();
		  System.out.println("WorkItemID:"+workItem
				  .getId()+" "+workItem
				  .getParameters()
				  .get("Comment"));
			swflMgr.completeWorkItem(
					workItem.getId(), 
					null);
			System.out.println("state:"+(StatelessRuntime.eINSTANCE
					.getProcessInstanceManager()
						.getProcessInstance(currentProcessInst)
							.getState() == ProcessInstance.STATE_ACTIVE));
		
//		swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
//			 workItem = workItemHandler.getWorkItem();
//			 System.out.println("WorkItemID:"+workItem.getId()+" "+workItem.getParameters().get("Comment"));
//			 swflMgr.completeWorkItem(
//						workItem.getId(), 
//						null);	
			 System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState() == ProcessInstance.STATE_ACTIVE));	 
		System.out.println("Process Events=" +processEventList);
	}
}
