package org.jbpm.samarjit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.h2.tools.Server;
import org.jbpm.samarjit.dao.WorkflowDAO;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;

public class Mytest4 {
	private static Server tcpserver;
	private static Server webserver;

	public static void startH2() {
		try {
			tcpserver = Server.createTcpServer().start();
			webserver = Server.createWebServer().start();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void stopH2() {
		tcpserver.stop();
		webserver.stop();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ArrayList<String> list = WorkflowDAO.getcurrentTask("163");
		System.out.println(list.toString());
		// procc
		StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
		swflMgr.readWorkflowFiles(Mytest4.class.getResourceAsStream("sample.bpmn"));
		final List<String> processEventList = new ArrayList<String>();
		final ProcessEventListener processEventListener = new ProcessEventListener() {
			public void afterNodeLeft(ProcessNodeLeftEvent event) {}
			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
				processEventList.add(event.toString() + " \n");
			}
			public void afterProcessCompleted(ProcessCompletedEvent event) {
				processEventList.add(event + " process completed!\n");
			}
			public void afterProcessStarted(ProcessStartedEvent event) {
				processEventList.add(event + " process started...\n");
			}
			public void beforeNodeLeft(ProcessNodeLeftEvent event) {}
			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {}
			public void beforeProcessCompleted(ProcessCompletedEvent event) {}
			public void beforeProcessStarted(ProcessStartedEvent event) {}
			public void beforeVariableChanged(ProcessVariableChangedEvent event) {
				processEventList.add(event + " \n");
			}
			public void afterVariableChanged(ProcessVariableChangedEvent event) {
				processEventList.add(event + " \n");
			}
		};
		swflMgr.getRuntime().addEventListener(processEventListener);

		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
		swflMgr.startProcess("sample");
		service();
		// long currentProcessInst = swflMgr.startProcess("mytest");

		// if(1==1)throw new
		// Exception("Break out before completion for testing");
		// // swflMgr.restoreWorkflowSession();
		//
		// System.out.println("Process instance length="+currentProcessInst);
		// System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState()
		// == ProcessInstance.STATE_ACTIVE));
		// WorkItem workItem = workItemHandler.getWorkItem();
		// System.out.println("WorkItemID:"+workItem.getId()+" "+workItem.getParameters().get("Comment"));
		// swflMgr.completeWorkItem(workItem.getId(), null);
		// System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState()
		// == ProcessInstance.STATE_ACTIVE));
		// swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
		// workItem = workItemHandler.getWorkItem();
		// System.out.println("WorkItemID:"+workItem
		// .getId()+" "+workItem
		// .getParameters()
		// .get("Comment"));
		// swflMgr.completeWorkItem(
		// workItem.getId(),
		// null);
		// System.out.println("state:"+(StatelessRuntime.eINSTANCE
		// .getProcessInstanceManager()
		// .getProcessInstance(currentProcessInst)
		// .getState() == ProcessInstance.STATE_ACTIVE));
		//
		// swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
		// workItem = workItemHandler.getWorkItem();
		// System.out.println("WorkItemID:"+workItem.getId()+" "+workItem.getParameters().get("Comment"));
		// swflMgr.completeWorkItem(
		// workItem.getId(),
		// null);
		// System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState()
		// == ProcessInstance.STATE_ACTIVE));
		// System.out.println("Process Events=" +processEventList);
		// // swflMgr.startProcess("com.sample.evaluation");
	}

	public static void service() {
		System.out.println("hello");
	}
}