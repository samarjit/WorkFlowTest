package com.ycs.wfl.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONSerializer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ApplicationAware;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.jbpm.samarjit.Mytest2;
import org.jbpm.samarjit.StatelessRuntime;
import org.jbpm.samarjit.StatelessWorkflowManager;
import org.jbpm.samarjit.myengine.LwWorkflowManager;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

/**
 * http://localhost:8182/WorkFlow/wfl.action?command=readfiles&instanceid=0&nodeid=2
 * http://localhost:8182/WorkFlow/wfl.action?command=getprocesslist&instanceid=0&nodeid=2
 * http://localhost:8182/WorkFlow/wfl.action?command=getstartnode&processid=lwtworkflowId&nodeid=1
 * http://localhost:8182/WorkFlow/wfl.action?command=getcurrenttasks&processid=lwtworkflowId&nodeid=7
 * http://localhost:8182/WorkFlow/wfl.action?command=dowork&&processid=lwtworkflowId&nodeid=2
 * 
 * @author Samarjit
 *
 */
public class  WorkflowAction extends ActionSupport implements ApplicationAware{
	
 
	private static final long serialVersionUID = 1L;
	
	private String processid = null;
	private int instanceid = -1;
	private long nodeid = -1;
	private String command = null ;
	
	private InputStream inputStream;
	//private Map<String, Object> session;
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	Map<String, Object> appContext; 
	 
	@SuppressWarnings("unchecked")
	@Action(value="lwwfl", results={
			@Result(name="success", type="stream")
		})
	public String execute() throws Exception{
		String resultHtml = "";
		try {
			JSONObject jobj = new JSONObject("{'name':'hello world'}");
			resultHtml = jobj.toString();
			StatelessWorkflowManager swflMgr = new StatelessWorkflowManager();
			ArrayList<String> errors = new ArrayList<String>();
			System.out.println("command:"+command);
			System.out.println("processid:"+processid);
			System.out.println("instanceid:"+processid);
			System.out.println("nodeid:"+nodeid);

			if(appContext.containsKey("W_PROCESS")){
				swflMgr = (StatelessWorkflowManager) appContext.get("W_PROCESS");
			}
			
			if(command == null || "".equals(command)){
				errors.add("command is missing in request");
			}else{
				if(command.equals("readfiles")){
					System.out.println("locading bpmn files and then caching ..." );
					
					List<Process> processes = swflMgr.readWorkflowFiles(Mytest2.class.getResourceAsStream("Evaluation.bpmn"));
					appContext.put("W_PROCESS", swflMgr);
					
				}
				if(command.equals("restoresessions")){
					swflMgr.restoreWorkflowSession();
					appContext.put("W_PROCESS", swflMgr);
				}
				if(command.equals("getprocesslist")){
					  Collection<ProcessInstance> processesInstances = swflMgr.getRuntime().getProcessInstanceManager().getProcessInstances();
					String temp = "";
					boolean first = true;
					for (Iterator procItr = processesInstances.iterator(); procItr.hasNext();) {
						ProcessInstance processInstance = (ProcessInstance) procItr.next();
						temp += (first)?"":",";
						temp += processInstance.getProcessId();
						
					} 
					resultHtml = "{processlist: ["+temp+"]";
				}
				
				if(command.equals("createprocess")){
					long currentProcessInst = swflMgr.startProcess("com.sample.evaluation");
					instanceid = (int) currentProcessInst;
					resultHtml = "{processinstanceid:"+instanceid+", processid: '"+processid+"'}";
				}
				
				if(command.equals("getstartnode")){
					 
					resultHtml = "{startnode: 'Not Supported'}";
				}
				
				if(command.equals("getcurrenttasks")){
					if(instanceid == -1){
						errors.add("instanceid is required");
					}
					if(instanceid != -1){
						Collection<NodeInstance> nodeList = swflMgr.getNextTasks(instanceid);
						String temp = "";
						boolean first = true;
						for (NodeInstance nodeInstance : nodeList) {
							temp += (first)?"":",";
							temp += "{id:" + nodeInstance.getId() + ", name: '" + nodeInstance.getNodeName() + "'}";
							first = false;
						}
						System.out.println(temp);
						resultHtml = "{currenttasks:["+temp+"]}";
					}
				}
				
				if(command.equals("dowork")){
					if(instanceid == -1){
						errors.add("instanceid is required");
					}
					if(instanceid != -1){
						TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
						swflMgr.registerWorkItemHandler("Human Task", workItemHandler);
						WorkItem  workItem = workItemHandler.getWorkItem();
						  System.out.println("WorkItemID:"+workItem
								  .getId()+" "+workItem
								  .getParameters()
								  .get("Comment"));
							swflMgr.completeWorkItem(
									workItem.getId(), 
									null);
							System.out.println("Process state?:"+(StatelessRuntime.eINSTANCE
									.getProcessInstanceManager()
										.getProcessInstance(instanceid)
											.getState() == ProcessInstance.STATE_ACTIVE));
						resultHtml = "{success: 'true', workitemid: "+workItem.getId()+", nodename:'"+workItem.getName()+"', comment: '"+workItem
						  .getParameters()
						  .get("Comment")+"'}";
					}
				}
				
				
			}
			if(errors.size() > 0){
				System.out.println("errors in inputs:"+errors);
				resultHtml =  "{error: "+ JSONSerializer.toJSON(errors).toString()+"}";
			}
		    
		} catch (Exception e) {
			e.printStackTrace();
			String tmpStack="";
			StackTraceElement[]  elm = e.getStackTrace();
			for (int i =0 ; i< elm.length && i < 4; i++ ) {
				tmpStack += " at "+elm[i]+"\n";
			}
			resultHtml = "{error: '"+e.toString()+tmpStack+"'}";
		}
		inputStream = new ByteArrayInputStream( resultHtml.getBytes() );
		return SUCCESS;
	}
	
	
	
	public int getInstanceid() {
		return instanceid;
	}



	public void setInstanceid(int instanceid) {
		this.instanceid = instanceid;
	}



	public long getNodeid() {
		return nodeid;
	}



	public void setNodeid(long nodeid) {
		this.nodeid = nodeid;
	}



	public String getCommand() {
		return command;
	}



	public void setCommand(String command) {
		this.command = command;
	}

	public String getProcessid() {
		return processid;
	}



	public void setProcessid(String processid) {
		this.processid = processid;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



	public void setApplication(Map<String, Object> app) {
		appContext = app;
	}

}
