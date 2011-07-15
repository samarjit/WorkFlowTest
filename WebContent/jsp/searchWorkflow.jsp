<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<script  src="js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="js/raphael.js"></script>
<script type="text/javascript" src="js/drawWorkflow.js"></script>

<script type="text/javascript">
$(document).ready(function(){
	var running = ${runningTask};
	var completed = ${completedTask};
	//alert(running+completed);
	drawWorkflow(running,completed,'sample.bpmn');
  });                    
</script>
</head>
<body>
<h4>Login Page</h4> 
<s:textfield name="taskId" label="Task ID"></s:textfield>

<s:textfield name="address" label="Address"></s:textfield>
<div id="raphaeldiv"></div>   

</body>
</html>