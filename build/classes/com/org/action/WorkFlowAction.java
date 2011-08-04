package com.org.action;

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
import org.jbpm.samarjit.Mytest4;
import org.jbpm.samarjit.StatelessRuntime;
import org.jbpm.samarjit.StatelessWorkflowManager;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;

import com.opensymphony.xwork2.ActionSupport;

public class WorkFlowAction extends ActionSupport{
	String result = null;
	long currentProcessInst;
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	public String execute() throws Exception{
		StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
		swflMgr.readWorkflowFiles(Mytest4.class.getResourceAsStream("sample.bpmn"));
		final List<String> processEventList = new ArrayList<String>();
		final ProcessEventListener processEventListener = new ProcessEventListener() {
			public void afterNodeLeft(ProcessNodeLeftEvent event) {}
			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {processEventList.add(event.toString() + " \n");}
			public void afterProcessCompleted(ProcessCompletedEvent event) {processEventList.add(event + " process completed!\n");}
			public void afterProcessStarted(ProcessStartedEvent event) {processEventList.add(event + " process started...\n");}
			public void beforeNodeLeft(ProcessNodeLeftEvent event) {}
			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {}
			public void beforeProcessCompleted(ProcessCompletedEvent event) {}
			public void beforeProcessStarted(ProcessStartedEvent event) {}
			public void beforeVariableChanged(ProcessVariableChangedEvent event) {processEventList.add(event + " \n");}
			public void afterVariableChanged(ProcessVariableChangedEvent event) {processEventList.add(event + " \n");}
		};
		swflMgr.getRuntime().addEventListener(processEventListener);
		
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
		
		swflMgr.restoreWorkflowSession();
		WorkItem workItem = workItemHandler.getWorkItem();
//		if(workItem ==  null){
//			setResult("start");
//			 currentProcessInst = swflMgr.startProcess("sample");
//		}else{
			setResult((String)workItem.getParameters().get("TaskName"));
			System.out.println("WorkItemID:" + workItem.getId() + " " + workItem.getParameters().get("Comment"));
			swflMgr.completeWorkItem(workItem.getId(), null);
//		}
		
		return result;
	}
}
