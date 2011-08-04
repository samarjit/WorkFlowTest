package org.jbpm.samarjit.diagram;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.samarjit.Mytest2;
import org.jbpm.samarjit.StatelessWorkflowManager;
import org.xml.sax.SAXException;

public class DiagramTest {
	
	
	
	public static void main(String args[]) throws FileNotFoundException, SAXException, IOException {
		StatelessWorkflowManager swm = new StatelessWorkflowManager();
		List<Process> processes = swm.readWorkflowFiles(Mytest2.class.getResourceAsStream("Evaluation.bpmn"));
//		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump((WorkflowProcess) processes.get(0)));
		byte[] diagramBytes = IoUtil.readInputStream(ProcessDiagramGenerator.generatePngDiagram((WorkflowProcess) processes.get(0)), null);
		try{
		FileOutputStream fos = new FileOutputStream("test.png");
		System.out.println();
		fos.write(diagramBytes);
		fos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
