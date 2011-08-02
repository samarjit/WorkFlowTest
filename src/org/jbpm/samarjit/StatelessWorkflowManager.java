package org.jbpm.samarjit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.definition.process.Process;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.xml.sax.SAXException;

public class StatelessWorkflowManager {
	List<Process> processes = null;
	
	public List<Process> readWorkflowFiles(InputStream is) throws FileNotFoundException, SAXException, IOException{
		SemanticModules modules = new SemanticModules();
//		modules.addSemanticModule(new ProcessSemanticModule());
		// modules.initSemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
		modules.addSemanticModule(new BPMNExtensionsSemanticModule());
		XmlProcessReader reader = new XmlProcessReader(modules, this.getClass().getClassLoader());
		reader.read(is);
		List<Process> tempProcesses = reader.getProcess();
		if(processes == null)processes = new ArrayList<Process>();
		HashMap<String, Process> tmphmprocess = new HashMap<String, Process>();
		for (Process proc : processes) {
			tmphmprocess.put(proc.getId(), proc);
		}
		for (Process tempprocess : tempProcesses) {
			tmphmprocess.put(tempprocess.getId(),tempprocess);
		}
		ArrayList<Process> arprocs = new ArrayList<Process>(); 
		for (Entry<String, Process> process : tmphmprocess.entrySet()) {
			arprocs.add(process.getValue());
		}
		processes = arprocs;
		return processes;
	}
	
	public int deploy(){
		return -1;
	}
	
	public Collection<NodeInstance> getNextTasks(int processInstanceId){
		StatelessProcessInstance processInstance = (StatelessProcessInstance) StatelessRuntime.eINSTANCE.getProcessInstanceManager().getProcessInstance(processInstanceId);
		if(processInstance == null)return null;
		Collection<NodeInstance> nInstances = processInstance.getNodeInstances();
		for (NodeInstance nodeInstance : nInstances) {
			System.out.println("#GetTasks() "+nodeInstance.getId()+" "+nodeInstance.getNodeId()+"::"+nodeInstance.getNodeName());
			
		}
		return processInstance.getNodeInstances();
	}
	
	/**
	 * This adds latest process to processInstanceManager
	 * @param processId like com.sample.evaluation
	 * @return
	 */
	public long startProcess(String processId){
		ProcessInstance startProcessInstance = null;
		for (Process process : processes) {
			WorkflowProcessImpl wp = (WorkflowProcessImpl)process;
			if(wp.getId().equals(processId)){
				 startProcessInstance = StatelessRuntime.eINSTANCE.startProcess(wp);
			}
		}
		return startProcessInstance.getId();
	}
	
	public StatelessRuntime getRuntime(){
		return StatelessRuntime.eINSTANCE;
	}
	
	public void completeWorkItem(long id, Map<String, Object> results){
		StatelessRuntime.eINSTANCE.getWorkItemManager().completeWorkItem(id, results);
	}
	
	public void signalEvent(){
		
	}

	public void registerWorkItemHandler(String workItemName, TestWorkItemHandler workItemHandler) {
		StatelessRuntime.eINSTANCE.getWorkItemManager()
				.registerWorkItemHandler(workItemName, workItemHandler);
	}

	public void restoreWorkflowSession() {
		 RestoreWorkflowSession rws = new RestoreWorkflowSession();
		 rws.restoreWorkflowSession(processes);
	}
	
	public List<Process> getProcessList(){
		return processes;
	}
}
