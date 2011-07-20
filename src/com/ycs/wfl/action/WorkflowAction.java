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
import org.drools.definition.process.Process;
import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.jbpm.samarjit.Mytest2;
import org.jbpm.samarjit.StatelessRuntime;
import org.jbpm.samarjit.StatelessWorkflowManager;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;
import com.ycs.wfl.exception.WflException;

/**
 * http://localhost:8182/WorkFlow/wfl.action?command=readfiles
 * http://localhost:8182/WorkFlow/wfl.action?command=restoresessions
 * http://localhost:8182/WorkFlow/wfl.action?command=getprocesslist
 * http://localhost:8182/WorkFlow/wfl.action?command=createprocess
 * http://localhost:8182/WorkFlow/wfl.action?command=getcurrenttasks&instanceid=242
 * http://localhost:8182/WorkFlow/wfl.action?command=getworkitems&instanceid=242
 * http://localhost:8182/WorkFlow/wfl.action?command=getworkitemsall
 * http://localhost:8182/WorkFlow/wfl.action?command=dowork&instanceid=242
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
	@Action(value="wfl", results={
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
			System.out.println("instanceid:"+instanceid);
			System.out.println("nodeid:"+nodeid);
			TestWorkItemHandler testWorkItemHandler = null;
			
			if(appContext.containsKey("W_PROCESS")){
				swflMgr = (StatelessWorkflowManager) appContext.get("W_PROCESS");
				Map<String, WorkItemHandler> workItemHandlers = swflMgr.getRuntime().getWorkItemManager().getWorkItemHandlers();
				if(workItemHandlers.containsKey("Human Task")){
				 testWorkItemHandler  = (TestWorkItemHandler) workItemHandlers.get("Human Task");
				}
			}
			 
			
			final List<String> processEventList = new ArrayList<String>();
			final ProcessEventListener processEventListener = new ProcessEventListener() {
				public void afterNodeLeft(ProcessNodeLeftEvent event) {
//					processEventList.add(event);
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
//					processEventList.add(event);
				}

				public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
//					processEventList.add(event);
				}

				public void beforeProcessCompleted(ProcessCompletedEvent event) {
//					processEventList.add(event);
				}

				public void beforeProcessStarted(ProcessStartedEvent event) {
//					processEventList.add(event);
				}

				public void beforeVariableChanged(ProcessVariableChangedEvent event) {
					processEventList.add(event+" \n");
				}

				public void afterVariableChanged(ProcessVariableChangedEvent event) {
					processEventList.add(event+" \n");
				}
			};
			swflMgr.getRuntime().addEventListener(processEventListener);
			
			
			
			if(command == null || "".equals(command)){
				errors.add("command is missing in request");
			}else{
				if(command.equals("readfiles")){
					System.out.println("locading bpmn files and then caching ..." );
					
					List<Process> processes = swflMgr.readWorkflowFiles(Mytest2.class.getResourceAsStream("Evaluation.bpmn"));
					appContext.put("W_PROCESS", swflMgr);
					
				}
				if(command.equals("restoresessions")){
					testWorkItemHandler = new TestWorkItemHandler();
					swflMgr.registerWorkItemHandler("Human Task", testWorkItemHandler);
					swflMgr.restoreWorkflowSession();
					 Map<String, WorkItemHandler> workItemHandlers = swflMgr.getRuntime().getWorkItemManager().getWorkItemHandlers();
					 if(workItemHandlers.containsKey("Human Task")){
						 testWorkItemHandler  = (TestWorkItemHandler) workItemHandlers.get("Human Task");
					 }
					appContext.put("W_PROCESS", swflMgr);
					
				}
				if(command.equals("getprocesslist")){
					  Collection<ProcessInstance> processesInstances = swflMgr.getRuntime().getProcessInstanceManager().getProcessInstances();
					String temp = "";
					boolean first = true;
					for (Iterator procItr = processesInstances.iterator(); procItr.hasNext();) {
						ProcessInstance processInstance = (ProcessInstance) procItr.next();
						temp += (first)?"{'":",{processid: '";
						temp += processInstance.getProcessId();
						temp += "', instanceid:'";
						temp += processInstance.getId();
						temp += "'}";
					} 
					resultHtml = "{processlist: ["+temp+"]";
				}
				
				if(command.equals("createprocess")){
					if(processid == null){
						errors.add("processid is required");
					}
					if(processid != null){
						testWorkItemHandler = new TestWorkItemHandler();
						swflMgr.registerWorkItemHandler("Human Task", testWorkItemHandler);
						
						long currentProcessInst = swflMgr.startProcess(processid);
						instanceid = (int) currentProcessInst;
						resultHtml = "{processinstanceid:"+instanceid+", processid: '"+processid+"'}";
					}
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
						if(nodeList == null){
							errors.add("instance not found");
						}else{
						String temp = "";
						boolean first = true;
						for (NodeInstance nodeInstance : nodeList) {
							temp += (first)?"":",";
							temp += "{id:" + nodeInstance.getId() + ", name: '" + nodeInstance.getNodeName() + "', nodeid: '"+nodeInstance.getNode().getId()+"'}";
							first = false;
						}
						System.out.println(temp);
						resultHtml = "{currenttasks:["+temp+"]}";
						}
					}
				}
				if(command.equals("getworkitems")){
					if(instanceid == -1){
						errors.add("instanceid is required");
					}
					if(testWorkItemHandler == null){
						errors.add("internal error: WorkItemHandler was not registered");
					}
					
					if(instanceid != -1 && testWorkItemHandler != null){
						List<WorkItem> workItems = testWorkItemHandler.getWorkItems();
						String temp = "";
						boolean first = true;
						
						for (WorkItem workItem : workItems) {
							if(instanceid == workItem.getProcessInstanceId()){
								temp += (first)?"":",";
								temp += "{instanceid:'"+workItem.getProcessInstanceId()+"',id:" + workItem.getId() + ", name: '" + workItem.getName() + "', actor: '"+workItem.getParameter("ActorId")+"', comment:'"+workItem.getParameter("Comment")+"'}";
								first = false;
							}
						}
						
						resultHtml = "{workitems:["+temp+"]}";
					}
					
				}
				if(command.equals("getworkitemsall")){
					 
					if(testWorkItemHandler == null){
						errors.add("internal error: WorkItemHandler was not registered");
					}
					
					if(testWorkItemHandler != null){
						List<WorkItem> workItems = testWorkItemHandler.getWorkItems();
						String temp = "";
						boolean first = true;
						
						for (WorkItem workItem : workItems) {
							temp += (first)?"":",";
							temp += "{instanceid:'"+workItem.getProcessInstanceId()+"',id:" + workItem.getId() + ", name: '" + workItem.getName() + "', actor: '"+workItem.getParameter("ActorId")+"', comment:'"+workItem.getParameter("Comment")+"'}";
							first = false;
						}
						
						resultHtml = "{workitems:["+temp+"]}";
					}
					
				}
				if(command.equals("dowork")){
					if(instanceid == -1){
						errors.add("instanceid is required");
					}
					if(testWorkItemHandler == null){
						errors.add("internal error: WorkItemHandler was not registered");
					}
					if(instanceid != -1 && testWorkItemHandler != null){
						
						WorkItem  workItem = testWorkItemHandler.getWorkItem();
						
						System.out.println("workItem:"+workItem);
						if(workItem == null) throw new WflException("workItem Not found");
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
		    
		} catch (WflException e) {
			resultHtml =  "{error: "+ e.getLocalizedMessage()+"}";
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
