Raphael.fn.arrow = function (x1, y1, x2, y2, size) {
	var angle = Math.atan2(x1-x2,y2-y1);
	angle = (angle / (2 * Math.PI)) * 360;
	var arrowPath = this.path("M" + x2 + " " + y2 + " L" + (x2  - size) + " " + (y2  - size) + " L" + (x2  - size)  + " " + (y2  + size) + " L" + x2 + " " + y2 ).attr("fill","black").rotate((90+angle),x2,y2);
	// var linePath = this.path("M" + x1 + " " + y1 + " L" + x2 + " " + y2);
	return arrowPath;
	// return [linePath,arrowPath];
};


function Point(x,y){
	this.x = x;
	this.y = y;
}

// start_point ---------------> end_point
function Lineseg(start_point,end_point){
	
	this.start_point = start_point;
	this.end_point = end_point;
	this.slope = function(){
		if((end_point.x-start_point.x) == 0)return "YAXIS_PARALLEL";
		else return parseFloat((end_point.y - start_point.y))/(end_point.x-start_point.x); 
	}
}

function Rectangle(x,y,width,height){
	this.x = parseInt(x);
	this.y = parseInt(y);
	this.width = parseInt(width);
	this.height = parseInt(height);
}

function findIntersection(line /* Lineseg */, box /* Rectangle */){ /*
																	 * return
																	 * Point
																	 */
	var outer_point;
	/*
	 * if(line.start_point.x < box.x || line.start_point.x > (box.x +
	 * box.width)){ outer_point = line.start_point; }else{ outer_point =
	 * line.end_point; }assume end_point is always inner_point
	 */
	/* handle yaxis parallel */
	if(line.slope() == "YAXIS_PARALLEL"){
		/* top */
		if(line.start_point.y < box.y){
			return new Point(line.start_point.x,box.y);
		}
		/* bottom */
		if(line.start_point.y > (box.y+box.height)){
			return new Point(line.start_point.x, box.y+box.height);
		}
	}else{
		/* top y=10 box.y */
		if(line.start_point.y < box.y){
			 var colx = (box.y - line.start_point.y)/ line.slope()+line.start_point.x;
			 if(colx > box.x &&  colx < (box.x+box.width)){
				 return new Point(colx, box.y);
			 }
		}
		/* right x=15 box.x+box.width */
		if(line.start_point.x > (box.x+box.width)){
			 var coly =  line.slope()*(box.x+box.width - line.start_point.x)+line.start_point.y;
			 if(coly > box.y &&  coly < (box.y+box.height)){
				 return new Point(box.x+box.width,coly);
			 }
		}
		/* bottom y=20 box.y+box.height */
		if(line.start_point.y > (box.y+box.height)){
			 var colx = (box.y+box.height - line.start_point.y)/ line.slope()+line.start_point.x;
			 if(colx > box.x &&  colx < (box.x+box.width)){
				 return new Point(colx, box.y+box.height);
			 }
		}
		/* left x=10 box.x */
		if(line.start_point.x < box.x){
			 var coly =  line.slope()*(box.x - line.start_point.x)+line.start_point.y;
			 if(coly > box.y &&  coly < (box.y+box.height)){
				 return new Point(box.x,coly);
			 }
		}
	}
		
	return Point(0,0);
}
var running ;
var completed;
function drawWorkflow(r,c,path){
	running = r;
	completed = c;
$.ajax({
    type: "GET",
	url: path,
	dataType: "text",
	success: fnLoadBpmn
});
}
	function fnLoadBpmn(inputxml){
	var bpmnsrc = inputxml;
	var sampleXml = unescape(bpmnsrc);
	if (window.DOMParser)
	  {
	  parser=new DOMParser();
	  xmlDoc=parser.parseFromString(sampleXml,"text/xml");
	  }
	else // Internet Explorer
	  {
	  xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
	  xmlDoc.async="false";
	  xmlDoc.loadXML(sampleXml); 
	  }
	$xml = $( xmlDoc );
	
	processType = $xml.find( "[nodeName='dc:Bounds']" );  
	var paper = Raphael(document.getElementById("raphaeldiv"), 1000, 1000);
	var bpmnEdge = $xml.find("[nodeName='bpmndi:BPMNEdge']");
	bpmnEdge.each(function(i,v){
		var seqFlowId = $(v).attr("bpmnElement");
		var elmName = $xml.find("[id='"+seqFlowId+"']").attr("name");
		var targetId = $xml.find("[id='"+seqFlowId+"']").attr("targetRef");
		var targetShape = $xml.find("[bpmnElement='"+targetId+"']");
		var box = targetShape.find("[nodeName='dc:Bounds']");
		var rectangle = new Rectangle(box.attr("x"),box.attr("y"), box.attr("width"), box.attr("height"));
		
		var di = $(v).find("[nodeName='di:waypoint']");
		var initialPoint = true;	
		var pathString = "";
		var startx, starty, endx, endy;
		var edges = [];
			di.each(function (i2,v2){
				var x = parseInt($(v2).attr("x"));
				var y = parseInt($(v2).attr("y"));
				edges.push(new Point(x,y));
				pathString += (initialPoint)?"M ":"L "	
					if(initialPoint){
						startx = x;  starty = y;
					}else{
						endx = x;  endy = y;
					}	
			    pathString += x +" "+ y+" ";
				initialPoint = false;
			});
			end_point = edges.pop();
			start_point = edges.pop();
		 	lastEdge = new Lineseg(start_point, end_point);
		 	var intersect_point = findIntersection(lastEdge,rectangle);
		 	// paper.circle(intersect_point.x, intersect_point.y, 10);
		 	 
		 	
			paper.path(pathString).attr("stroke","#eee");
		 	paper.arrow(start_point.x,start_point.y, intersect_point.x, intersect_point.y, 5).attr({stroke: "#eee", fill:"#eee"});
			if(typeof(elmName) != 'undefined'){ 
				paper.text((startx+endx)/2, (starty+endy)/2, elmName);
			console.log(elmName + pathString);
			}
		});
	
	var bpmnShape = $xml.find("[nodeName='bpmndi:BPMNShape']");
	bpmnShape.each(function(i,v){
		var shapeId = $(v).attr("bpmnElement");
		var elmName = $xml.find("[id='"+shapeId+"']").attr("name");
		var di = $(v).find("[nodeName='dc:Bounds']");
		var x = parseInt(di.attr("x"));
		var y = parseInt(di.attr("y"));
		var width = parseInt(di.attr("width"));
		var height = parseInt(di.attr("height"));
		var style = getStyle(shapeId);
		var c = paper.rect(x, y, width, height,2).attr(style);
		var textLeftOffset = elmName.length/2*9;
		var t = paper.text(x+width/2, y+height/2, elmName).attr("fill","#000");
		// console.log(elmName+" "+di.tagName+" "+x+","+ y+","+ width+","+
		// height);
	});

 
	function getStyle(id){
		var attr = {stroke:'black' ,"stroke-width":"3",fill:"#E8E8E8",opacity:1.0};
		id = id.replace("_","").trim();
		if(contains(running,id)){
			attr = {stroke:'green' ,"stroke-width":"3",fill:"#00CC66",opacity:1.0};
		}else if(contains(completed,id)){
			attr = {stroke:'red' ,"stroke-width":"3",fill:"#FF6666",opacity:1.0};
		}
		return attr;
	}
	
	function contains(a, obj){
		  for(var i = 0; i < a.length; i++) {
		    if(a[i] == obj){
		      return true;
		    }
		  }
		  return false;
		}
/*
 * //
 * $('#bpmndiv').load($('#bpmnname').val(),{command:'load',bpmnnname:$('#bpmnname').val()});
 * var jqXHR = $.get($('#bpmnname').val(), function (data,status, jqXHR1) {
 * alert(data); var xmlDoc = jqXHR.responseText; $xml = $( jqXHR1 );
 * $processType = $xml.find( 'rootcontainer' ); alert($processType.length );
 * 
 * },"xml");
 */
}