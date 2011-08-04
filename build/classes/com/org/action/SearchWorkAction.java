package com.org.action;

import java.util.ArrayList;

import org.jbpm.samarjit.dao.WorkflowDAO;

import com.opensymphony.xwork2.ActionSupport;

public class SearchWorkAction extends ActionSupport {
	public String taskId;
	public ArrayList<String> runningTask;
	public ArrayList<String> completedTask;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public ArrayList<String> getRunningTask() {
		return runningTask;
	}

	public void setRunningTask(ArrayList<String> runningTask) {
		this.runningTask = runningTask;
	}

	public ArrayList<String> getCompletedTask() {
		return completedTask;
	}

	public void setCompletedTask(ArrayList<String> completedTask) {
		this.completedTask = completedTask;
	}

	public String execute() {
		 setRunningTask(WorkflowDAO.getcurrentTask("166"));
		 setCompletedTask(WorkflowDAO.getCompletedTask("166"));

		 for(String r:runningTask){
			 System.out.println("Running Task : "+r);
		 }
		 for(String c:completedTask){
			 System.out.println("Completed Task : "+c);
		 }
		return SUCCESS;
	}

}
