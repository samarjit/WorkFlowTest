package org.jbpm.samarjit.myengine;

import java.io.FileInputStream;
import java.io.IOException;
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

	public Object readWorkflowFiles(FileInputStream fileInputStream) throws SAXException, IOException {
		SemanticModules modules = new SemanticModules();
//		modules.addSemanticModule(new ProcessSemanticModule());
		// modules.initSemanticModules();
		modules.addSemanticModule(new BPMNSemanticModule());
		modules.addSemanticModule(new BPMNDISemanticModule());
		modules.addSemanticModule(new BPMNExtensionsSemanticModule());
		XmlProcessReader reader = new XmlProcessReader(modules, this.getClass().getClassLoader());
		reader.read(fileInputStream);
		processes = reader.getProcess();
		return processes;
	}
	
	public Node getStartNode(){
		WorkflowProcess process = (WorkflowProcess) processes.get(0);
		for (Node node : process.getNodes()) {
			System.out.println(node instanceof StartNode);
			return node;
		}
		return null;
	}

	public void doWork(WorkflowProcess process, Node currentNode) {
		System.out.println("Done work for "+currentNode.getName());
	}

	public List<Node> getCurrentTasks(WorkflowProcess process, Node currentNode) {
		Map<String, List<Connection>> connections = currentNode.getOutgoingConnections();
		List<Node> nodes = new ArrayList<Node>();
		for (Entry<String, List<Connection>> itr : connections.entrySet()) {
//			System.out.println("connection key::"+itr.getKey());
			for (Connection connection : itr.getValue()) {
				System.out.println("connection::"+connection.getTo());
				nodes.add(connection.getTo());
			}
		}
		return nodes;
	}
}
