package com.org.action;

import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {
	public String userId = null;
	public String password = null;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String execute() {
		System.out.println("userId : "+userId +" Password : "+ password);
		if (userId != null && !userId.trim().equals("")) {
			return SUCCESS;
		} else {
			return "login";
		}
	}
}
