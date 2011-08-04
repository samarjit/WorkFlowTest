<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>WorkFlow View</title>
<script type="text/javascript" src="../js/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="../js/raphael.js"></script>

<script type="text/javascript">
var paper = null;
var pic_real_width, pic_real_height;

function loadImage(){
	
	
	$("<img/>") // Make in memory copy of image to avoid css issues
    .attr("src",  $("#imgurl").val())
    .load(function() {
        pic_real_width = this.width;   // Note: $(this).width() will not
        pic_real_height = this.height; // work for in memory images.
    });
    
	$("#wfl").html("");
	paper = Raphael(document.getElementById("wfl"), pic_real_width, pic_real_height);
	$("#wflimage").attr("src",$("#imgurl").val()); 
    //paper.image($("#imgurl").val(),  0,   0, pic_real_width, pic_real_height);
    
}
$(document).ready(function(){
	var img = $("#wflimage"); // Get my img elem
	$("<img/>") // Make in memory copy of image to avoid css issues
	    .attr("src", $(img).attr("src"))
	    .load(function() {
	        pic_real_width = this.width;   // Note: $(this).width() will not
	        pic_real_height = this.height; // work for in memory images.
			paper = Raphael(document.getElementById("wfl"), pic_real_width, pic_real_height);
	    });
		
	//paper.image("<%=request.getContextPath()%>/deploy/Evaluation.bpmn.png",  0,   0, pic_real_width, pic_real_height);
});

function createCircle(){
	
	var c = paper.rect(document.getElementById("x").value, document.getElementById("y").value, 40,40);
}

function positionRelative(){
	$("#wflimage").parent().css("left",$("#imgposrelX").val()+"px");
	$("#wflimage").parent().css("top",$("#imgposrelY").val()+"px");
	 
}

function saveImagePos(){
	$("#command").val("saveimagepos");
	var params = $(document.forms[0]).serialize();
	$.get(document.forms[0].action+"?"+params, function(data){
			alert(data);
		});
	
}

function getImageData(){
	$("#command").val("getimage");
	var params = $(document.forms[0]).serialize();
	$.get(document.forms[0].action+"?"+params, function(data){
			alert(data.relx);
			$("#imgposrelX").val(data.relx);
			$("#imgposrelY").val(data.rely);
			$("#imgurl").val(data.imageurl);
			positionRelative();
			loadImage();
		});
}

function drawOverlay(){
	$("#command").val("gethistory");
	var params = $(document.forms[0]).serialize();
	$.get(document.forms[0].action+"?"+params, function(data){
			$("#processid").val(data.processid);
			$.each(data.history, function(i,v){
					alert(v.state +" "+v.x);
					if(v.state == 2)
						paper.image("<%=request.getContextPath()%>/css/images/RedStart.gif", v.x, v.y, 15 , 15);
					else
						paper.image("<%=request.getContextPath()%>/css/images/GreenStart.gif", v.x, v.y, 15 , 15); 
				});
		});
}
</script>

</head>
<body>

<form action="<%=request.getContextPath()%>/wfl.action">
<input 	type="hidden" id="command" name="command" /> 
 
<input id="instanceid" name="instanceid"	value="241" />
<button type="button" onclick="drawOverlay()">Draw Overlay</button>
<input id="processid" name="processid"	value="com.sample.evaluation" />
    <button type="button" onclick="getImageData()">Get Image</button>
<input id="imgurl" 	value="<%=request.getContextPath()%>/deploy/Evaluation.bpmn.png" />
    <button type="button" onclick="loadImage()">Load Image</button>

<h3>Work flow view...</h3>

x<input id="x" > y<input id="y">
<button type="button" onclick="createCircle()">overlay</button>

relx<input id="imgposrelX" name="imgposrelX"> rely<input 	id="imgposrelY" name="imgposrelY">

<button type="button" onclick="positionRelative()">position</button>
<button type="button" onclick="saveImagePos()">Save</button>
</form>

<div style="position: relative">
  <div id="wfl" style="position: absolute; z-index:10"></div>
  <div 	style="position: absolute" >
    <img style="position:relative" id="wflimage" src="<%=request.getContextPath()%>/deploy/Evaluation.bpmn.png" />
  </div>
</div>

</body>
</html>