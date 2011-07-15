package com.org.action;

import java.util.ArrayList;
import java.util.List;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.process.WorkItem;
import org.jbpm.samarjit.Mytest4;
import org.jbpm.samarjit.Mytest5;
import org.jbpm.samarjit.StatelessWorkflowManager;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;

import com.opensymphony.xwork2.ActionSupport;

public class HelloWorld extends ActionSupport {

	private static final long serialVersionUID = 1L;
	String greetings = null;

	public String getGreetings() {
		return greetings;
	}

	public void setGreetings(String greetings) {
		this.greetings = greetings;
	}

	public String viewSuccess() throws Exception {
		setGreetings("View Success");
		return SUCCESS;
	}

	public String viewPage() throws Exception {
		setGreetings("View Page Method");
		return SUCCESS;
	}
	
	public String callWorkFlow() throws Exception{
		setGreetings("callWorkFlow");
		StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
		swflMgr.readWorkflowFiles(HelloWorld.class
				.getResourceAsStream("test.bpmn"));
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
		swflMgr.startProcess("mytest");
		return SUCCESS;
	}
	
	public String restoreWorkflow() throws Exception{
		setGreetings("restoreWorkflow");
		StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
		swflMgr.readWorkflowFiles(HelloWorld.class.getResourceAsStream("test.bpmn"));
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
		System.out.println("WorkItemID:" + workItem.getId() + " " + workItem.getParameters().get("Comment"));
		swflMgr.completeWorkItem(workItem.getId(), null);
		return SUCCESS;
	}
}
