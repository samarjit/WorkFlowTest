//alert("I am coming from test.js");
//var str="";$.each($('#form1').serializeArray(),function(i,v){ str+=(v.name+",");});
//JSONstring.make($('#form1').serializeArray())
//it gives a list of comma separated names
/*Create template//
  columns:: $('#form1 tr').find("td:first").each(function(i,v){console.log("<td>"+$(v).text()+"</td>");});
  var str = "<td style='display:table-cell'>{{= texidtvalue}}<input type='hidden' value='{{= texidtvalue}}' id='bingroup__{{= rowcount}}' name='bingroup__{{= texidtvalue}}'/></td>";
  var str2=""
  $.each(fieldlist,function(i,v){ str2 += str.replace(/texidtvalue/g,v)+"\n";});
  str2
*/
 var updateCompositeField = function(obj,dfid){
     var str = $(dfid).val();
     var json = {};
     if(str != null && str != "") 
      json = JSON.parse(str);
     json[obj.name] = obj.value;
     var myJSONText = JSON.stringify(json, replacer,"");
     $(dfid).val(myJSONText);
   };
						    
function replacer(key, value) {
	if (typeof value === 'number'  && !isFinite(value)) {
		return String(value);
	}
	return value;
}
 
function submitcallback(form){
	var idlist = eval("panel_"+form.id);
	alert(idlist);
} 

/**
 * rule is a global object created dynamically by java and appended to generated page, and it is extended here.
 * Extra initialization options are provided through var options; variable inside this function
 * @returns ruleobj which is used to initilalize validator
 */
function initRule_Callback(rule) {
	var options = {
			errorElement:"label",
			errorLabelContainer:"#alertmessage", 
			submitHandler: function(form){
			      			  var result =	submitcallback(form);
			      			  if(result == true)
			      				  form.submit();
						     }
				};
	var  ruleobj = $.extend(rule,options);
	return ruleobj;
}

//Internet applicaiton development tool framework
(function(global){

	//1. iadt object is actually iadt.fn.init class's object
	var iadt = function(){
	 
	 return new iadt.fn.init();
	};

	iadt.fn =  { //iadt.prototype seems to be optional =  what is this for?
	init : function(){
		 
	}
	 
	};
	//options will be initialized in constructor but can be extended
	iadt.options = {
			 divlist: "#multiplerows", 
			 divheaderlist: ".headerlist",
			 listtemplate: "#listtemplate",
			 fieldlist: "" //has to be set for every page use setFieldlist()
	 };	
	
	//the below line is like iadt.fn.init.prototype.newfn where jQoery object is actually its constructor 
	//iadt.fn.init is class and newfn is its member function so it can be called by its object not directly
	iadt.fn.newfn  = function(){
		alert('newfn()');
	};

	 
	//this is a function of the object not class. It is just a property of object
	iadt.sayHello = function(){
		alert('sayHello()');
	}; 
	//iadt.fn.x means x is a prototype method or variable of iadt [1]
	iadt.fn.init.prototype  = iadt.fn;
	iadt.fn.init.constructor = 'iadt';

  
	//alert('compiled');
	global.iadt = iadt;	
})(window);


function createMenu(){
    //	$.ajax({
    //  url: 'menuHeader.html',
    // 
    //  type: 'GET',
    //  async: false,
    //  success: function(data){ // grab content from another page
    //		$('.menuheader').html(data);
    //	},
    //  dataType: 'html'
    //});
    
    var menuxml = "";
    $.ajax({
        url: 'menu.xml',
        type: 'GET',
        async: false,
        success: function(data){ // grab content from another page
            menuxml = data;
        },
        dataType: 'xml'
    });
   
    var tabmenu = menuxml.getElementsByTagName("tab");
    var headertable = document.createElement('table');
    var row = document.createElement('tr');
    var jsonArray = [];
    for (var i = 0; i < tabmenu.length; i++) {
        var tabname = tabmenu[i].getAttribute("name");
        var tabclass = tabmenu[i].getAttribute("class");
        var tabid = tabmenu[i].getAttribute("id");
        var cell = document.createElement('td');
        var div = document.createElement('div');
        div.setAttribute("id", tabid);
        div.setAttribute("class", tabclass);
        var text = document.createTextNode(tabname);
        div.appendChild(text);
        cell.appendChild(div);
        row.appendChild(cell);
        
        // menu 
        var menu = tabmenu[i].getElementsByTagName("menu");
        var cell_len = 0;
        for (var k = 0; k < menu.length; k++) {
            var pos = menu[k].getAttribute("position");
            if (pos > cell_len) {
                cell_len = pos;
            }
        }
        
        var outercell = [];
        var outertable = document.createElement('table');
        var outerrow = document.createElement('tr');
        for (var l = 0; l < cell_len; l++) {
            var cellpos = l + 1;
            outercell[l] = document.createElement('td');
            for (var j = 0; j < menu.length; j++) {
                var menuname = menu[j].getAttribute("name");
                var menuclass = menu[j].getAttribute("class");
                var menuposition = menu[j].getAttribute("position");
                if (menuposition == cellpos) {
                    var menutable = document.createElement('table');
                    menutable.setAttribute("class", menuclass);
                    var menurow = document.createElement('tr');
                    var menucell = document.createElement('th');
                    var menunamenode = document.createTextNode(menuname);
                    menucell.appendChild(menunamenode);
                    menurow.appendChild(menucell);
                    menutable.appendChild(menurow);
                    var submenu = menu[j].getElementsByTagName("submenu");
                    for (var m = 0; m < submenu.length; m++) {
                        var href = submenu[m].getAttribute("onclick");
                        var submenuname = submenu[m].firstChild.nodeValue;
                        var submenurow = document.createElement('tr');
                        var submenucell = document.createElement('td');
                        var submenua = document.createElement('a');
                        submenua.setAttribute("href", href);
                        var submenudiv = document.createElement('div');
                        var submenunamenode = document.createTextNode(submenuname);
                        submenudiv.appendChild(submenunamenode);
                        submenua.appendChild(submenudiv);
                        submenucell.appendChild(submenua);
                        submenurow.appendChild(submenucell);
                        menutable.appendChild(submenurow);
                    }
                    outercell[l].appendChild(menutable);
                }
            }
            outerrow.appendChild(outercell[l]);
        }
        outertable.appendChild(outerrow);
        var menudiv = document.createElement('div');
        menudiv.setAttribute("style", "display:block;");
        menudiv.appendChild(outertable);
        var tempdiv = document.createElement('div');
        tempdiv.appendChild(menudiv);
       // 		alert(tempdiv.innerHTML);
        var json = {
            "id": tabid,
            "menu": tempdiv.innerHTML
        }
        jsonArray[i] = json;
    }
    
    headertable.appendChild(row);
    $(".menuheader").html(headertable);
   
    for (var n = 0; n < jsonArray.length; n++) {
        var tabid = jsonArray[n].id;
        var menudiv = jsonArray[n].menu;
        $("#" + tabid).menu({
            content: menudiv, // grab content from this page
            showSpeed: 400,
            width: 300
        });
    }
    
    
    // BUTTONS
    $('.fg-button').hover(function(){
        $(this).addClass('ui-state-focus');
    }, function(){
        $(this).removeClass('ui-state-focus');
    });
    //MENUS DYNAMIC
    //     $.get('menu1Content.html', function(data){ // grab content from another page
    //     alert("data from html:"+ data);
    //		$('#tab1').menu({ 
    //					content: data,//$('#menu1').html(), // grab content from this page
    //					showSpeed: 400,
    //					width: 300
    //				});
    //		});
    
    
    //MENUS DYNAMIC END
    iadt.refreshHoverIcon();
};

  
  var screenMode = "addrow";
  
  iadt.setFieldlist = function (fldlst){
	iadt.options.fieldlist = fldlst;
  };
  
  iadt.getFieldlist = function (){
	  if(iadt.options.fieldlist == "")iadt.showMessage("fieldlist must be populated for each page");
		return iadt.options.fieldlist  ;
	  };
	  
  /**
   * Static function to copy selected field to hidden
   * @param obj
   * @param spanid
   * @param txtid
   * @param populateFieldsToModify callback
   */

   iadt.copySeltohidden = function(obj,spanid, txtid, populateFieldsToModify){
	  if(obj.value != 'New'){
		  
		  document.getElementById(txtid).value = obj.value;
		  $('#'+spanid).hide();
		 
		   
	  }else{
		  document.getElementById(txtid).value = '';
		  //document.getElementById(hid).value = '';
		  $('#'+spanid).show();
		  
		  $('.field ,.field2').each(function(){
				$(this).val("");
			});
		   
		  obj.value = 'New';
	  }
	  
	  if(populateFieldsToModify!= null)
		  populateFieldsToModify();
  };
  
   iadt.refreshHoverIcon = function() {
		 //hover states on the static widgets
//	    $('span.icons').live( 'hover', 
//				function() { $(this).toggleClass('ui-state-error');   } 
//			);
	   $('body').delegate("span", 'hover', 
				function() { $(this).toggleClass('ui-state-error');   } 
			);
	};
  
   iadt.showMessage = function(message) { 
		var str = "<div  style='width:100%'>"+message+"<span class='icons'><span class='ui-icon ui-icon-closethick' style='float:right' onclick='iadt.hideMessage(this)'></span></span></div>";
		if($('.infoBar').length == 0){
			alert('a DIV element with class .infoBar is required for showing messages \n'+message);

		}
		$('.infoBar').append(str);
		$('.infoBar').slideDown('slow');
		
	};

   iadt.hideMessage = function(objSpanIcon) {
		$(objSpanIcon).parent().parent().remove();
		if ($('.infoBar').find('span').length == 0) {
			$('.infoBar').slideUp('slow');
		}
  };
  
  iadt.populateToFields = function (trobj, radClickCallback) {
		var arList = iadt.getFieldlist();
		//tr:input 
		var dataList =  $(trobj).find(":input");
		$.each(arList,function (i,v){
			var idname = v;
			//first input is radio , skip that
			  $("#"+idname).val(dataList[i+1].value);
		});
		
		iadt.setScreenMode("updaterow");
		
		var radioObj = $('input[type=radio]',trobj);
		
		if(radClickCallback != null)
		radClickCallback(radioObj);
	};
  
  /**
   * Adds row to the list table
 * @param formid
 * @param ... options = Object() which will be added to datamodel
 */
iadt.addrow = function(formid,rowcount){
		var arList = iadt.getFieldlist();

		var rowcount = $(iadt.options.divlist+' table').get(0).rows.length;
		//var str = "<tr class='even'><td><input type='radio' value='"+rowcount+"' name='check' id='check"+rowcount+"' onclick='radClick(this,\""+divlist+"\",\""+divheaderlist+"\");' />"+
		//"&nbsp;<span id='icons'><span class='ui-icon ui-icon-pencil' onclick='editMe(this,\""+divlist+"\",\""+divheaderlist+"\", radClickCallback);' ></span></span>"+
		//"<span id='icons'><span class='ui-icon ui-icon-closethick' onclick='deleteMe(this)' ></span></span></td>"; 
		var templatedata = $(iadt.options.listtemplate).html();
		var datamodel = {rowcount: rowcount,divlist: iadt.options.divlist, divheaderlist: iadt.options.divheaderlist}; 
		var formval = $(formid).serializeObject();
		$.each(arList,function (i,v){
			
			var idname = v;
			//var ishidden = v.indexOf("hidden") >1?"none":"table-cell";
			var val = formval[idname];
			//str+="<td style='display:"+ishidden+"'>"+val+"<input type='hidden' value='"+val+"' id='"+idname+"__"+rowcount+"' name='"+idname+"__"+rowcount+"'/></td>";
			 datamodel[idname]=val;
		});
		 
		if(arguments.length == 2){
			$.extend(datamodel, arguments[1]);
			
		}
		//str+="</tr>";
		 var templateResult = 	$.tmpl(templatedata,datamodel);
		$(iadt.options.divlist+' table').append($('tr',templateResult).parent().html()); 
		
	};

	/**
	 * Modifies a row in list table
	 * @param formid
	 * @param ... options = Object() which will be added to datamodel
	 */
	 iadt.updaterow = function(formid){

		var arList = iadt.getFieldlist();
		var selectedRadio =$(iadt.options.divlist+' input[type=radio]:checked');
		var selectedIndex = selectedRadio.val(); 
		if (typeof(selectedIndex) == 'undefined') {
			iadt.showMessage("Please select a record to modify");
			return;
		}
		//remove all <td> tags
		var TRref = selectedRadio.parent().parent();
		selectedRadio.parent().parent().empty();
		var rowcount =  selectedIndex;
		//var str = "<td><input type='radio' value='"+rowcount+"' name='check' id='check"+rowcount+"' onclick='radClick(this,\""+divlist+"\",\""+divheaderlist+"\",radClickCallback );' />"+
		//"&nbsp;<span id='icons'><span class='ui-icon ui-icon-pencil' onclick='editMe(this,\""+divlist+"\",\""+divheaderlist+"\");' ></span></span>"+
		//"<span id='icons'><span class='ui-icon ui-icon-closethick' onclick='deleteMe(this)' ></span></span></td>"; 
	  	
		var templatedata = $(iadt.options.listtemplate).html();
		/*$.each(arList,function (i,v){
			var idname = v;
			//var ishidden = v.indexOf("hidden") >1?"none":"table-cell";
			var val = $("#"+idname).val();
			str+="<td style='display:"+ishidden+"'>"+val+"<input type='hidden' value='"+val+"' id='"+idname+"__"+rowcount+"' name='"+idname+"__"+rowcount+"'/></td>";
		});*/
		
		var datamodel = {rowcount: rowcount,divlist: iadt.options.divlist, divheaderlist: iadt.options.divheaderlist}; 
		var formval = $(formid).serializeObject();
		$.each(arList,function (i,v){
			var idname = v;
			//var ishidden = v.indexOf("hidden") >1?"none":"table-cell";
			//var val = $("#"+idname).val();
			var val = formval[idname];
			datamodel[idname]=val;
		});
		if(arguments.length == 2){
			$.extend(datamodel, arguments[1]);
			console.dir(datamodel);
		}
		
	    var templateResult = 	$.tmpl(templatedata,datamodel);
	 
		//replace newly created tags inside <tr> 
		 TRref.append(templateResult.find('tr').html());
		
	};
	
	$.fn.serializeObject = function()
	{
	    var o = {};
	    var a = this.serializeArray();
	    $.each(a, function() {
	        if (o[this.name]) {
	            if (!o[this.name].push) {
	                o[this.name] = [o[this.name]];
	            }
	            o[this.name].push(this.value || '');
	        } else {
	            o[this.name] = this.value || '';
	        }
	    });
	    return o;
	};
	
  $.strPad = function(i,l,s) {
		var o = i.toString();
		if (!s) { s = '0'; }
		while (o.length < l) {
			o = s + o;
		}
		return o;
	};
    
	 iadt.detectGears = function(){
	 if (!window.google || !google.gears) {
		    location.href = "http://gears.google.com/?action=install&message=Google gears is required for this demo application" +
		                    "&return=<your website url>";
		  }
	 };
	 
	iadt.setScreenMode = function(s) {
		if($('#form1 .head2 tr:first td').length == 1){
			$('#form1 .head2 td:first').after('<td style="text-align: right; font-weight: normal;"><div class="screenMode"> addrow</div> </td>');
		}
		$('.head2 .screenMode').text("Screen Mode: "+s);
		screenMode = s;
	}

	 function copyTxttohidden(obj, hid){
		  //document.getElementById(hid).value = obj.value;
	 };
	 
	//usage: $.QueryString["param"]  will return the param value
    (function($){
        $.QueryString = (function(a){
            if (a == "") 
                return {};
            var b = {};
            for (var i = 0; i < a.length; ++i) {
                var p = a[i].split('=');
                b[p[0]] = decodeURIComponent(p[1].replace(/\+/g, " "));
            }
            return b;
        })(window.location.search.substr(1).split('&'))
    })(jQuery);
	