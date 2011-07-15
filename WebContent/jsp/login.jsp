<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<script  src="js/jquery-1.4.4.min.js"></script>
<script  src="js/jquery.validate.js"></script>
<script type="text/javascript" src="js/fg.menu.js"></script>
<script type="text/javascript" src="js/jquery.tmpl.js"></script>
<script type="text/javascript" src="js/jsonStringify.js"></script>
<script type="text/javascript" src="js/iadtframework.js"></script>

<link type="text/css" href="css/fg.menu.css" rel="stylesheet" />
<link type="text/css" href="css/jquery-ui-1.8.8.custom.css" rel="stylesheet" />
<link href="css/main.css" rel="stylesheet" type="text/css">
<link href="css/body.css" rel="stylesheet" type="text/css">
<link type="text/css" href="css/fg.menu.css" rel="stylesheet" />

<script type="text/javascript">
$(document).ready(function(){
 	createMenu();
  });                    
</script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login Page</title>
</head>
<body>
<h4>Login Page</h4> 
<div class="menuheader"></div>   
<br><br><br><br><br><br><br><br><br><br><br><br><br><br>
<s:form action="loginValidation.action">
<s:textfield name="userId" label="USER ID" value="user"></s:textfield> 

<s:password name="password" label="PASSWORD"></s:password>
<s:submit label="login" align="left"></s:submit>
</s:form>
</body>
</html>