<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login Page</title>
</head>
<body>
<h4>Enter the Details</h4> 

<s:form action="getDetailAction.action">
<s:textfield name="firstName" label="First Name"></s:textfield>
<s:textfield name="lastName" label="Last Name"></s:textfield>
<s:textfield name="address" label="Address"></s:textfield>
<s:textfield name="city" label="City"></s:textfield>
<s:textfield name="state" label="State"></s:textfield>
<s:submit label="Submit" align="left" ></s:submit>
</s:form>
</body>
</html>