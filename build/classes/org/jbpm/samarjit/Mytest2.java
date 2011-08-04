package org.jbpm.samarjit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.xml.SemanticModules;
import org.h2.tools.Server;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;

public class Mytest2 {
	private static Server tcpserver;
	private static Server webserver;

	public static void startH2(){
		try {
			tcpserver =  Server.createTcpServer().start();
			webserver = Server.createWebServer().start();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void stopH2(){
		tcpserver.stop();
		webserver.stop();
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
//		startH2();
		SemanticModules modules = new SemanticModules();
//		modules.addSemanticModule(new ProcessSemanticModule());
		// modules.initSemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
		modules.addSemanticModule(new BPMNExtensionsSemanticModule());
		XmlProcessReader reader = new XmlProcessReader(modules,Mytest2.class.getClassLoader());
		reader.read(Mytest2.class.getResourceAsStream("Evaluation.bpmn"));
		 List<Process> processes = reader.getProcess();
//		Collection<Process> processes = kbase.getProcesses();
		 WorkflowProcessImpl procc=null;
		 for (Process process : processes) {
			System.out.println(process.getId());
			RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) process;
			System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess));
			for (Entry<String, Object> map : process.getMetaData().entrySet()) {
				System.out.println("map:" + map.getKey() + " " + map.getValue());
			}
			System.out.println(process.getId());
			Definitions def = (Definitions) process.getMetaData("Definitions");
			System.out.println(def.getTargetNamespace());
			System.out.println(process.getName());
			System.out.println(process.getPackageName());
			System.out.println(process.getType());
			Definitions def2 = (Definitions) process.getMetaData().get("Process");
			WorkflowProcessImpl wflp = (WorkflowProcessImpl)process;
			WorkflowProcess proc = (WorkflowProcess) process;
			procc = wflp;
			
			System.out.println(wflp.getNodes());
			System.out.println(proc.getNodes());
			
		}
		 
//		procc
		StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
		swflMgr.readWorkflowFiles(Mytest2.class.getResourceAsStream("Evaluation.bpmn"));
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
		long currentProcessInst = swflMgr.startProcess("com.sample.evaluation");
		if(1==1)throw new Exception("Break out before completion for testing"); 
//		swflMgr.restoreWorkflowSession();
		 
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
		
		swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
			 workItem = workItemHandler.getWorkItem();
			 System.out.println("WorkItemID:"+workItem.getId()+" "+workItem.getParameters().get("Comment"));
			 swflMgr.completeWorkItem(
						workItem.getId(), 
						null);	
			 System.out.println("state:"+(StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(currentProcessInst).getState() == ProcessInstance.STATE_ACTIVE));	 
		System.out.println("Process Events=" +processEventList);
//		swflMgr.startProcess("com.sample.evaluation");
	}
}