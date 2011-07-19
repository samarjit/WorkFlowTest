package com.ycs.wfl.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONSerializer;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ApplicationAware;
import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.jbpm.samarjit.myengine.LwWorkflowManager;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

/**
 * http://localhost:8182/WorkFlow/lwwfl.action?command=readfiles&instanceid=0&nodeid=2
 * http://localhost:8182/WorkFlow/lwwfl.action?command=getprocesslist&instanceid=0&nodeid=2
 * http://localhost:8182/WorkFlow/lwwfl.action?command=getstartnode&processid=lwtworkflowId&nodeid=1
 * http://localhost:8182/WorkFlow/lwwfl.action?command=getcurrenttasks&processid=lwtworkflowId&nodeid=7
 * http://localhost:8182/WorkFlow/lwwfl.action?command=dowork&&processid=lwtworkflowId&nodeid=2
 * 
 * @author Samarjit
 *
 */
public class LwWorkflowAction extends ActionSupport implements ApplicationAware{
	
 
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
			LwWorkflowManager wflmgr = new LwWorkflowManager();
			ArrayList<String> errors = new ArrayList<String>();
			System.out.println("command:"+command);
			System.out.println("processid:"+processid);
			System.out.println("instanceid:"+processid);
			System.out.println("nodeid:"+nodeid);

			wflmgr.setProcesses((Map<String, Process>) appContext.get("LW_PROCESS"));
			
			if(command == null || "".equals(command)){
				errors.add("command is missing in request");
			}else{
				if(command.equals("readfiles")){
					System.out.println("locading bpmn files and then caching ..." );
					Map<String, Process> processes = (Map<String, Process>) wflmgr.readWorkflowFiles(getClass().getClassLoader().getResourceAsStream("bpmnfiles/lightweight.bpmn2"));
					appContext.put("LW_PROCESS", processes);
					
				}
				if(command.equals("getprocesslist")){
					Map<String, Process> processes = wflmgr.getProcesses();
					 
					resultHtml = "{processlist:"+JSONSerializer.toJSON(processes.keySet())+"}";
				}
				
				if(command.equals("createprocess")){
					instanceid = wflmgr.createProcess(processid);
					resultHtml = "{processinstanceid:"+instanceid+", processid: '"+processid+"'}";
				}
				
				if(command.equals("getstartnode")){
					long startNodeId = wflmgr.getStartNodeId(processid);
					resultHtml = "{startnode:"+startNodeId+"}";
				}
				
				if(command.equals("getcurrenttasks")){
					if(processid == null){
						errors.add("processid is required");
					}
					if(nodeid == -1){
						errors.add("nodeid is required");
					}
					if(processid != null && nodeid != -1){
						List<Node> nodeList = wflmgr.getCurrentTasks(processid, nodeid);
						String temp = "";
						boolean first = true;
						for (Node node : nodeList) {
							temp += (first)?"":",";
							temp += "{id:" + node.getId() + ", name: '" + node.getName() + "'}";
							first = false;
						}
						System.out.println(temp);
						resultHtml = "{currenttasks:["+temp+"]}";
					}
				}
				
				if(command.equals("dowork")){
					if(processid == null){
						errors.add("processid is required");
					}
					if(nodeid == -1){
						errors.add("nodeid is required");
					}
					if(processid != null && nodeid != -1){
						wflmgr.doWork(processid, nodeid);
						Node nodeCompleted = wflmgr.getNodeById(processid, nodeid); 
						resultHtml = "{success: 'true', nodeid: "+nodeid+", nodename:'"+nodeCompleted.getName()+"'}";
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
