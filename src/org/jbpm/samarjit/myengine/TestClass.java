package org.jbpm.samarjit.myengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.jbpm.workflow.core.node.StartNode;
import org.xml.sax.SAXException;

public class TestClass {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, SAXException, IOException {
	
		LwWorkflowManager wflmgr = new LwWorkflowManager();
		List<Process> processes = (List<Process>) wflmgr.readWorkflowFiles(new FileInputStream("src/bpmnfiles/lightweight.bpmn2")); 
		 System.out.println("Process read...");
		WorkflowProcess process = (WorkflowProcess) processes.get(0);
		for (Node node : process.getNodes()) {
//			System.out.println(node instanceof StartNode);
//			System.out.println(node.getId());
		}
		Node currentNode  = wflmgr.getStartNode();
		wflmgr.doWork( process, currentNode );
		List<Node>nodes = wflmgr.getCurrentTasks(process, currentNode);
		
		System.out.println(nodes.get(0).getName()+"::"+nodes.get(0).getId());
		wflmgr.doWork( process,nodes.get(0)); //script
		
		nodes = wflmgr.getCurrentTasks(process, nodes.get(0));
		System.out.println(nodes); //split
		
		wflmgr.doWork( process,nodes.get(0)); 
		
		nodes = wflmgr.getCurrentTasks(process, nodes.get(0));
		System.out.println(nodes); //Human task nodes 2
				
		
		wflmgr.doWork( process,nodes.get(0)); 
		wflmgr.doWork( process,nodes.get(1));
		nodes = wflmgr.getCurrentTasks(process, nodes.get(0));

		wflmgr.doWork( process,nodes.get(0));
		nodes = wflmgr.getCurrentTasks(process, nodes.get(0));
		System.out.println(nodes); //Join
		
		wflmgr.doWork( process,nodes.get(0));
		nodes = wflmgr.getCurrentTasks(process, nodes.get(0));
		System.out.println(nodes); //End
	}
	
	
}
