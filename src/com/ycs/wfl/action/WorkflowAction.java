package com.ycs.wfl.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONSerializer;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.ApplicationAware;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
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
import org.jbpm.samarjit.StatelessWorkflowManager;
import org.jbpm.samarjit.dao.WorkflowDAO;
import org.jbpm.samarjit.diagram.IoUtil;
import org.jbpm.samarjit.diagram.ProcessDiagramGenerator;
import org.jbpm.samarjit.mynodeinst.MockStatelessNodeInstance;
import org.jbpm.samarjit.mynodeinst.TestWorkItemHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.ycs.wfl.exception.WflException;

/**
 * http://localhost:8182/WorkFlow/wfl.action?command=deploy&filename=Evaluation.bpmn 
 * http://localhost:8182/WorkFlow/wfl.action?command=getimage&processid=com.sample.evaluation
 * http://localhost:8182/WorkFlow/wfl.action?command=saveimagpos&processid=com.sample.evaluation
 * http://localhost:8182/WorkFlow/wfl.action?command=readfiles
 * http://localhost:8182/WorkFlow/wfl.action?command=restoresessions
 * http://localhost:8182/WorkFlow/wfl.action?command=getprocesslist
 * http://localhost:8182/WorkFlow/wfl.action?command=getprocessinstancelist
 * http://localhost:8182/WorkFlow/wfl.action?command=createprocessinstance&processid=com.sample.evaluation
 * http://localhost:8182/WorkFlow/wfl.action?command=getcurrenttasks&instanceid=232
 * http://localhost:8182/WorkFlow/wfl.action?command=getworkitems&instanceid=232
 * http://localhost:8182/WorkFlow/wfl.action?command=getworkitemsall
 * http://localhost:8182/WorkFlow/wfl.action?command=dowork&workitemid=232
 * http://localhost:8182/WorkFlow/wfl.action?command=gethistory&instanceid=232
 * http://localhost:8182/WorkFlow/wfl.action?command=getimage&processid=com.sample.evaluation TODO
 * http://localhost:8182/WorkFlow/wfl.action?command=abortprocessinstance&instanceid=233 TODO
 * http://localhost:8182/WorkFlow/jsp/viewworkflow.jsp
 * @author Samarjit
 *
 */
public class  WorkflowAction extends ActionSupport implements ApplicationAware{
	
 
	private static final long serialVersionUID = 1L;
	
	private String processid = null;
	private int instanceid = -1;
	private long nodeid = -1;
	private String command = null ;
	private long workitemid = -1;
	private String filename = null; 
	private String filedesc = null; 
	
	private InputStream inputStream;
	//private Map<String, Object> session;
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	Map<String, Object> appContext;

	private String imgposrelX;

	private String imgposrelY;

	 
	@SuppressWarnings("unchecked")
	@Action(value="wfl", results={
			@Result(name="success", type="stream", params={"contentType", "application/json"})
		})
	public String execute() throws Exception{
		String resultHtml = "";
		try {
			JSONObject jobj = new JSONObject("{'name':'undefined command - "+command+"'}");
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
				throw new WflException("command is missing in request");
			} 
				if(command.equals("deploy")){
					if(filename == null){
						throw new WflException("filename is required");
					}
					String retStr = deploy(swflMgr);
					resultHtml = "{deploy: 'success', "+retStr+"}";
				}
				else if(command.equals("getimage")){
					if(processid == null){
						throw new WflException("processid required");
					}
					List<String> imageDtl = WorkflowDAO.getImage(processid);
					String path =  ServletActionContext.getRequest().getContextPath() + "/deploy/" +imageDtl.get(0)+".png";
					resultHtml = "{imageurl: '"+path+"', relx:'"+imageDtl.get(1)+"', rely:'"+imageDtl.get(2)+"'}";
				}
				else if(command.equals("saveimagepos")){
					if(processid == null){
						throw new WflException("processid required");
					}
					 WorkflowDAO.saveImagePos(processid, imgposrelX, imgposrelY);
					 resultHtml = "{imgpos: 'saved'}";
				}
				else if(command.equals("readfiles")){
					System.out.println("locading bpmn files and then caching ..." );
//					ResourceBundle rb = ResourceBundle.getBundle("ApplicationResource");
					System.out.println("bpmn.deployed.path:"+getText("bpmn.deployed.path"));
//					String realpath = ServletActionContext.getServletContext().getRealPath("WEB-INF/classes/bpmmnfiles");
//					System.out.println(realpath);
					String deployedPath = getText("bpmn.deployed.path");
					File file = new File(deployedPath);
					System.out.println(file.exists());
					System.out.println(file.isDirectory());
					
					List<Process> processes = null;
					if(file.exists() && file.isDirectory() && !file.isHidden()){
						
						for (String filenames : file.list()) {
							if(filenames.endsWith(".bpmn")||filenames.endsWith(".bpmn2")||filenames.endsWith(".xml")){
								System.out.println(file.getPath()+File.separatorChar+filenames);
								processes = swflMgr.readWorkflowFiles(new FileInputStream(file.getPath()+File.separatorChar+filenames));
								System.out.println(processes.size()+ processes.get(0).getId());
							}
						}
					}else{
						throw new WflException("Directory not found" + deployedPath);
					}
					appContext.put("W_PROCESS", swflMgr);
					resultHtml = "{readfiles: 'success'}";
				}
				else if(command.equals("restoresessions")){
					testWorkItemHandler = new TestWorkItemHandler();
					swflMgr.registerWorkItemHandler("Human Task", testWorkItemHandler);
					
					if(!swflMgr.isProcessLoaded())
						throw new WflException("readfiles must be called before getprocesslist");
					
					swflMgr.restoreWorkflowSession();
					 Map<String, WorkItemHandler> workItemHandlers = swflMgr.getRuntime().getWorkItemManager().getWorkItemHandlers();
					 if(workItemHandlers.containsKey("Human Task")){
						 testWorkItemHandler  = (TestWorkItemHandler) workItemHandlers.get("Human Task");
					 }
					appContext.put("W_PROCESS", swflMgr);
					resultHtml = "{restoresession: 'success'}";
				}
				else if(command.equals("getprocesslist" )){
					List<Process> processList = null;
						if(!swflMgr.isProcessLoaded())
							throw new WflException("readfiles must be called before getprocesslist");
						processList = swflMgr.getProcessList();
					
					String temp = "";
					boolean first = true;
					if(processList != null){
						for (Process process : processList) {
							temp += (first)?"{processid: '":",{processid: '";
							temp += process.getId();
							temp += "', processname: '";
							temp += process.getName();
							temp += "', packagename: '";
							temp += process.getPackageName();
							temp += "', type: '";
							temp += process.getType();
							temp += "'}";
							first = false;
						}
					}
					resultHtml = "{processlist: ["+temp+"] }";
				}
				else if(command.equals("getprocessinstancelist")){
					  Collection<ProcessInstance> processesInstances = swflMgr.getRuntime().getProcessInstanceManager().getProcessInstances();
					String temp = "";
					boolean first = true;
					for (Iterator procItr = processesInstances.iterator(); procItr.hasNext();) {
						ProcessInstance processInstance = (ProcessInstance) procItr.next();
						temp += (first)?"{processid: '":",{processid: '";
						temp += processInstance.getProcessId();
						temp += "', instanceid:'";
						temp += processInstance.getId();
						temp += "'}";
						first = false;
					} 
					resultHtml = "{getprocessinstancelist: ["+temp+"]}";
				} 
				
				else if(command.equals("createprocessinstance")){
					if(processid == null){
						errors.add("processid is required");
					}
					if(!swflMgr.isProcessLoaded())
						throw new WflException("readfiles must be called before createprocessinstance");
					
					if(processid != null){
						if(testWorkItemHandler == null){
							testWorkItemHandler = new TestWorkItemHandler();
							swflMgr.registerWorkItemHandler("Human Task", testWorkItemHandler);
						}
						
						long currentProcessInst = swflMgr.startProcess(processid);
						instanceid = (int) currentProcessInst;
						resultHtml = "{processinstanceid:"+instanceid+", processid: '"+processid+"'}";
					}
				}
				
				else if(command.equals("getstartnode")){
					 
					resultHtml = "{startnode: 'Not Supported'}";
				}
				
				else if(command.equals("getcurrenttasks")){
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
				else if(command.equals("getworkitems")){
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
				else if(command.equals("getworkitemsall")){
					 
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
				
				else if(command.equals("gethistory")){
					if(instanceid == -1){
						throw new WflException("instanceid is required");
					}
					 
					
					List<MockStatelessNodeInstance> mockNodeList = WorkflowDAO.getHistory(instanceid);
					String temp = "";
					boolean first = true;
					
					for (MockStatelessNodeInstance mockNodeInst : mockNodeList) {
						temp += (first)?"":",";
						first = false;
						processid = mockNodeInst.getProcessid();
						List<Integer> coords = swflMgr.getCoordinatesFromNode(processid, mockNodeInst.getNodeId());
						temp += "{nodeinstanceid:'"+mockNodeInst.getId()+"', nodeid: '"+mockNodeInst.getNodeId()+"', nodename:'"+ mockNodeInst.getNodeName()+"', state:'"+mockNodeInst.getState()+"'," +
								"x:'"+coords.get(0)+"',y:'"+coords.get(1)+"',width:'"+coords.get(2)+"',height:'"+coords.get(3)+"'}";
					}
					resultHtml = "{processid:'"+processid+"',history:["+temp+" ]}";
				}
				else if(command.equals("dowork")){
					if(workitemid == -1){
						errors.add("workitemid is required");
					}
					if(testWorkItemHandler == null){
						errors.add("internal error: WorkItemHandler was not registered");
					}
					if(workitemid != -1 && testWorkItemHandler != null){
						
						WorkItem  workItem = testWorkItemHandler.getWorkItem(workitemid);
						
						System.out.println("workItem:"+workItem);
						if(workItem == null) throw new WflException("workItem Not found");
						System.out.println("WorkItemID:"+workItem
								  .getId()+" "+workItem
								  .getParameters()
								  .get("Comment"));
							swflMgr.completeWorkItem(
									workItem.getId(), 
									null);
						instanceid = (int) workItem.getProcessInstanceId(); 	
//						if(StatelessRuntime.eINSTANCE
//									.getProcessInstanceManager()
//										.getProcessInstance(instanceid) == null)throw new WflException("instanceid not found");	
//							System.out.println("Process state?:"+(StatelessRuntime.eINSTANCE
//									.getProcessInstanceManager()
//										.getProcessInstance(instanceid)
//											.getState() == ProcessInstance.STATE_ACTIVE));
						resultHtml = "{success: 'true', workitemid: "+workItem.getId()+", nodename:'"+workItem.getName()+"', comment: '"+workItem
						  .getParameters()
						  .get("Comment")+"'}";
					}
				 
				
				
				}
			if(errors.size() > 0){
				System.out.println("errors in inputs:"+errors);
				resultHtml =  "{error: "+ JSONSerializer.toJSON(errors).toString()+"}";
			}
			
			try{
			  resultHtml = new JSONObject(resultHtml).toString();
			}catch(JSONException e){
				System.out.println("result JSON parsing log and ignore silently");
				e.printStackTrace();
			}
			
		} catch (WflException e) {
			resultHtml =  "{'error': '"+ e.getLocalizedMessage()+"'}";
		} catch (Exception e) {
			e.printStackTrace();
			String tmpStack="";
			StackTraceElement[]  elm = e.getStackTrace();
			for (int i =0 ; i< elm.length && i < 4; i++ ) {
				tmpStack += " at this "+elm[i]+"\n";
			}
			JSONObject jobj = new JSONObject();
			jobj.put("error",  e.toString() + tmpStack);
			resultHtml = jobj.toString();
		}
		
		inputStream = new ByteArrayInputStream( resultHtml.getBytes() );
		return SUCCESS;
	}
	
	
	
	/**
	 * Creates version in table and copy to work directory. 
	 * Copy image to WebContent/deploy folder
	 * uses: filename
	 * @param swflMgr 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws WflException 
	 */
	private String deploy(StatelessWorkflowManager swflMgr) throws  SAXException, IOException, WflException {
		String tempPath = getText("bpmn.temp.path");
		String deployedPath = getText("bpmn.deployed.path");
		File deployedfile = new File(deployedPath+File.separatorChar+filename);
		File tempFile = new File(tempPath+File.separatorChar+filename);
		int rev = -1;
		String wflId = "";
		try{
			List<Process> processes = swflMgr.readWorkflowFiles(new FileInputStream(tempFile));
		WorkflowProcess workflowProcess = (WorkflowProcess) processes.get(0);
		byte[] diagramBytes = IoUtil.readInputStream(ProcessDiagramGenerator.generatePngDiagram(workflowProcess), null);
		 
			File f = new File(ServletActionContext.getServletContext().getRealPath("deploy")+File.separatorChar+filename+".png");
			try{
				if(f.exists()){
					f.delete();
				}
				f.createNewFile();
			
				FileOutputStream fos = new FileOutputStream(f);
				System.out.println(f.getAbsolutePath());
				fos.write(diagramBytes);
				fos.close();
			}catch(IOException e){
				System.out.println(e);
				throw new WflException("image file creation error");
				}
			
			FileInputStream from = null;
			FileOutputStream to = null;
			try {
				if (deployedfile.exists()) {
					deployedfile.delete();
				}
				deployedfile.createNewFile();

				from = new FileInputStream(tempFile);
				to = new FileOutputStream(deployedfile);
				byte[] buffer = new byte[4096];
				int bytesRead;

				while ((bytesRead = from.read(buffer)) != -1)
					to.write(buffer, 0, bytesRead); // write
				
			} catch (IOException e) {
				e.printStackTrace();
				throw new WflException("BPMN File copy error whily deploying");
			} finally {
				if (from != null)
					try {
						from.close();
					} catch (IOException e) {
						;
					}
				if (to != null)
					try {
						to.close();
					} catch (IOException e) {
						;
					}
			}
			System.out.println("temp file deletion result:"+tempFile.delete());
			
			rev = WorkflowDAO.deployMaker(workflowProcess.getId(),filename,filedesc);
			wflId = workflowProcess.getId();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			throw new WflException("BPMN File Not found- "+filename);
		}
		return " rev:'"+rev+"',processid:'"+wflId+"'"; 
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



	public long getWorkitemid() {
		return workitemid;
	}



	public void setWorkitemid(long workitemid) {
		this.workitemid = workitemid;
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



	public String getFilename() {
		return filename;
	}



	public void setFilename(String filename) {
		this.filename = filename;
	}



	public String getFiledesc() {
		return filedesc;
	}



	public void setFiledesc(String filedesc) {
		this.filedesc = filedesc;
	}



	public void setImgposrelX(String imgposrelX) {
		this.imgposrelX = imgposrelX;
	}



	public void setImgposrelY(String imgposrelY) {
		this.imgposrelY = imgposrelY;
	}

}
