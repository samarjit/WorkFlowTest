package org.jbpm.samarjit.myengine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.workflow.core.node.StartNode;
import org.xml.sax.SAXException;

public class LwWorkflowManager {

	private List<Process> processes = null;

	public Object readWorkflowFiles(InputStream fileInputStream) throws SAXException, IOException {
		SemanticModules modules = new SemanticModules();
//		modules.addSemanticModule(new ProcessSemanticModule());
		// modules.initSemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
		modules.addSemanticModule(new BPMNExtensionsSemanticModule());
		XmlProcessReader reader = new XmlProcessReader(modules, this.getClass().getClassLoader());
		reader.read(fileInputStream);
		List<Process> tempProcesses = reader.getProcess();
		if(processes == null)processes = new ArrayList<Process>();
		for (Process tempprocess : tempProcesses) {
			boolean found = false;
			for (int i =0; processes!= null && i  <  processes.size() ; i++) {
				Process process = processes.get(i);
				if(process.getId().equals( tempprocess.getId())){
					processes.set(i, tempprocess);
					found = true;
				}
			}
			if(!found){
				processes.add(tempprocess);
			}
		}
		return processes;
	}
	
	public int createProcess(String processId ){
		if(processes.size() > 0){
			return 0;
		}
		return -1;
	}
	
	public Node getStartNode(){
		WorkflowProcess process = (WorkflowProcess) processes.get(0);
		for (Node node : process.getNodes()) {
			System.out.println(node instanceof StartNode);
			return node;
		}
		 
		return null;
	}

	public long getStartNodeId(){
		WorkflowProcess process = (WorkflowProcess) processes.get(0);
		for (Node node : process.getNodes()) {
			System.out.println(node instanceof StartNode);
			return node.getId();
		}
		 
		return -1;
	}
	
	public void doWork(int processId, Node currentNode) {
		System.out.println("Done work for Node#"+currentNode.getName());
	}
	public void doWork(int processId, long nodeId) {
		Node currentNode = getNodeById(processId, nodeId);
		System.out.println("Done work for Node#"+currentNode.getName());
	}

	public List<Node> getCurrentTasks(int processId, Node currentNode) {
		Map<String, List<Connection>> connections = currentNode.getOutgoingConnections();
		List<Node> nodes = new ArrayList<Node>();
		for (Entry<String, List<Connection>> itr : connections.entrySet()) {
//			System.out.println("connection key::"+itr.getKey());
			for (Connection connection : itr.getValue()) {
				System.out.println("connection::"+connection.toString());
				nodes.add(connection.getTo());
			}
		}
		return nodes;
	}
	public List<Node> getCurrentTasks(int processId, long nodeId) {
		Node currentNode = getNodeById(processId, nodeId);
		Map<String, List<Connection>> connections = currentNode.getOutgoingConnections();
		List<Node> nodes = new ArrayList<Node>();
		for (Entry<String, List<Connection>> itr : connections.entrySet()) {
//			System.out.println("connection key::"+itr.getKey());
			for (Connection connection : itr.getValue()) {
				System.out.println("getcurrenttaks::"+connection.toString());
				nodes.add(connection.getTo());
			}
		}
		return nodes;
	}
	
	public Node getNodeById(int processId, long nodeId){
		WorkflowProcess process = (WorkflowProcess) processes.get(processId);
		for (Node node : process.getNodes()) {
			if(nodeId == node.getId())
			return node;
		}
		return null;
	}
	
	public Node getNodeByName(int processId, String nodeName){
		WorkflowProcess process = (WorkflowProcess) processes.get(processId);
		for (Node node : process.getNodes()) {
			if(nodeName.equals(node.getName()))
			return node;
		}
		return null;
	}

	public void setProcesses(List<Process> processobject) {
		processes  =  processobject;
	}
	public List<Process> getProcesses(int processId) {
		return processes;
	}
	public Process getProcess(String processId){
		for (Process process : processes) {
			if(process.getId().equals(processId))
				return process;
			
		}
		return null;
	}
}
