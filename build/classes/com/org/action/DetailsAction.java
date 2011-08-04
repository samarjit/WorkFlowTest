package com.org.action;

import com.opensymphony.xwork2.ActionSupport;

public class DetailsAction extends ActionSupport{

	public String firstName;
	public String lastName;
	public String address;
	public String city;
	public String state;


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
	public String getDetails(){
		System.out.println(firstName+" "+lastName+" "+address+" "+city+" "+state );
		return SUCCESS;
	}
}
