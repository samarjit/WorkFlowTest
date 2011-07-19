package com.ycs.wfl.action;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
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

public class WorkflowAction extends ActionSupport implements ApplicationAware{
	
 
	private static final long serialVersionUID = 1L;
	
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
			LwWorkflowManager wflmgr = new LwWorkflowManager();
			ArrayList<String> errors = new ArrayList<String>();
			System.out.println("command:"+command);
			System.out.println("instanceid:"+instanceid);
			System.out.println("nodeid:"+nodeid);

			wflmgr.setProcesses((List<Process>) appContext.get("LW_PROCESS"));
			
			if(command == null || "".equals(command)){
				errors.add("command is missing in request");
			}else{
				if(command.equals("readfiles")){
					System.out.println("locading bpmn files and then caching ..." );
					List<Process> processes = (List<Process>) wflmgr.readWorkflowFiles(getClass().getClassLoader().getResourceAsStream("bpmnfiles/lightweight.bpmn2"));
					appContext.put("LW_PROCESS", processes);
					
				}
				
				if(command.equals("createprocess")){
					instanceid = wflmgr.createProcess("0");
					resultHtml = "{processid:"+instanceid+"}";
				}
				
				if(command.equals("getstartnode")){
					long startNodeId = wflmgr.getStartNodeId();
					resultHtml = "{startnode:"+startNodeId+"}";
				}
				
				if(command.equals("getcurrenttasks")){
					if(instanceid == -1){
						errors.add("instanceid is required");
					}
					if(nodeid == -1){
						errors.add("nodeid is required");
					}
					if(instanceid != -1 && nodeid != -1){
						List<Node> nodeList = wflmgr.getCurrentTasks(instanceid, nodeid);
						String temp = "";
						boolean first = true;
						for (Node node : nodeList) {
							temp += (first)?"":",";
							temp += "{id:" + node.getId() + ", name: '" + node.getName() + "'}";
							System.out.println("{id:"+node.getId()+", name: '"+ node.getName()+"'}"+temp);
							first = false;
						}
						resultHtml = "{currenttasks:["+temp+"]}";
					}
				}
				
				if(command.equals("dowork")){
					if(instanceid == -1){
						errors.add("instanceid is required");
					}
					if(nodeid == -1){
						errors.add("nodeid is required");
					}
					if(instanceid != -1 && nodeid != -1){
						wflmgr.doWork(instanceid, nodeid);
						resultHtml = "{success:"+nodeid+"}";
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
