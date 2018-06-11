$(document).ready(function() {

	$('#search-form').submit(function(event) {
		// stop submit the form, we will post it manually.
		event.preventDefault();
		fire_ajax_submit();
	});
});
// var setting = {
// data: {
// key: {
// title:"t"
// },
// simpleData: {
// enable: true
// }
// },
// callback: {
// beforeClick: beforeClick,
// onClick: onClick
// }
// };

function showLog(str) {
	if (!log)
		log = $("#log");
	log.append("<li class='" + className + "'>" + str + "</li>");
	if (log.children("li").length > 8) {
		log.get(0).removeChild(log.children("li")[0]);
	}
}

function getTime() {
	var now = new Date(), h = now.getHours(), m = now.getMinutes(), s = now
			.getSeconds();
	return (h + ":" + m + ":" + s);
}

// 点击按钮“go”进行的操作
function fire_ajax_submit() {
	var search = {};
	search["readCommunity"] = $('#readCommunity').val();
	search["setCommunity"] = $('#setCommunity').val();
	search["syntax"] = $('#syntax').val();
	search["host"] = $('#host').val();
	search["version"] = getVersionValue();
	search["oid"] = $('#oid').val();
	search["port"] = $('#port').val();
	search["setValue"] = $('#setValue').val();
	search["timeout"] = $('#timeout').val();
	search["retransmits"] = $('#retransmits').val();
	search["nonRepeaters"] = $('#nonRepeaters').val();
	search["maxRepetitions"] = $('#maxRepetitions').val();
	search["priPass"] = $('#priPass').val();
	search["authPass"] = $('#authPass').val();
	var operationvar = $('#operation').val();
	var url = "";
	if (operationvar == 1) {
		url = "/api/snmpGet"
	} else if (operationvar == 2) {
		url = "/api/snmpGetnext"
	} else if (operationvar == 3) {
		url = "/api/snmpSet"
	} else if (operationvar == 4) {
		url = "/api/snmpWalk"
	} else if (operationvar == 5) {
		url = "/api/snmpTable"
	} else if (operationvar == 6) {
		url = "/api/snmpGetBulk"
	}
	$.ajax({
		type : 'POST',
		contentType : "application/json",
		url : url,
		data : JSON.stringify(search),
		dataType : 'json',
		cache : false,
		timeout : 600000,
		success : function(data) {
			$('#result').val(data.result);
			console.log("SUCCESS : ", data);
			if ($("#operation").val() == 2) {
				$("#oid").val(
						data.result.substring(0, data.result.indexOf(" ")));
			}
		},
		error : function(e) {
			$('#result').val(e.responseText);
			console.log("ERROR : ", e);

		}
	})
}
// 加载MIB树
tree = new dTree('tree'); // 创建一个对象.
tree.check = true;
tree.add(0, -1, '<a href="javascript:" oncontextmenu="rightMouse(1);">ISO</a>');
var i = 0;
var treeJson = "";

function doUpload() {
	var formData = new FormData($("#uploadForm")[0]);
	var firstNodes = 0;

	$.ajax({
		url : '/api/upload',
		type : 'POST',
		data : formData,
		dataType : 'json', // 接受数据格式
		async : false,
		cache : false,
		contentType : false,
		processData : false,
		success : function(data) {
			treeJson = data;
			var tt = "";
			var firstNodes = 0;
			$.each(data, function(index) {
				// 循环获取数据

				var nodeId = data[index].id;
				var parentId = data[index].parent;
				var value = data[index].value;
				var oid = data[index].oid;
				var nodeName = data[index].name;
				var syntax = data[index].syntax;
				var desc = data[index].desc;
				if (firstNodes == 0) {
					tree.add(nodeId, 0, nodeName,
							"javaScript:onClickTreeNode('" + oid + "');",
							nodeName, oid, "", "", false);
					firstNodes = 1;
				} else {

					tree.add(nodeId, parentId, nodeName,
							"javaScript:onClickTreeNode('" + oid + "');",
							nodeName, oid, "", "", false);
				}
			});
			tree.draw();
			tree.closeAll();
			i++;
		},
		error : function(returndata) {
			alert(returndata);
		}

	});

	// tree.openAll();
}
// 选取节点，获得节点oid值

function onClickTreeNode(oid) {
	// 当节点为叶子节点时，oid值需要加上".0"
	if ($("#isLeaf").val() == "true") {
		$("#oid").val(oid + ".0");
	} else {
		$("#oid").val(oid);
	}
	$("#isLeaf").val("false")

	$.each(treeJson, function(index) {
		// 循环获取数据
		if (oid == treeJson[index].oid) {
			$("#description").val(treeJson[index].desc);
			$("#syntax").val(treeJson[index].syntax);
			$("#nodeName").val(treeJson[index].name);
			return false;
		}

	});
}

function doSelectOper() {
	if ($("#operation").val() == 3) {
		$("#setValue").attr("disabled", false);
	}

}

function rightMouse() {
	alert(1);
}

// 弹出隐藏层
function ShowDiv(show_div, bg_div) {
	document.getElementById(show_div).style.display = 'block';
	document.getElementById(bg_div).style.display = 'block';
	var bgdiv = document.getElementById(bg_div);
	bgdiv.style.width = document.body.scrollWidth;
	$("#" + bg_div).height($(document).height());
	// bgdiv.style.height = $(document).height();
}
// 关闭弹出层

function CloseDiv(show_div, bg_div) {
	document.getElementById(show_div).style.display = 'none';
	document.getElementById(bg_div).style.display = 'none';
};
// 密码显示和隐藏
var isShow = true;
var isShowPri = true;

function change(passVar) {
	if (passVar == "auth") {
		var v = document.getElementById("authPassword");
		if (isShow) {
			v.type = "text";
			document.getElementById("clickauth").innerHTML = "<a href=\"javascript:change('auth');\">hide</a>"
			isShow = false;
		} else {
			v.type = "password";
			isShow = true;
			document.getElementById("clickauth").innerHTML = "<a href=\"javascript:change('auth');\">show</a>"
		}
	} else {
		var v = document.getElementById("priPassword");
		if (isShowPri) {
			v.type = "text";
			document.getElementById("clickpri").innerHTML = "<a href=\"javascript:change('pri');\">hide</a>"
			isShowPri = false;
		} else {
			v.type = "password";
			isShowPri = true;
			document.getElementById("clickpri").innerHTML = "<a href=\"javascript:change('pri');\">show</a>"
		}

	}
}
// 取version值

function getVersionValue() {
	var value = '';
	var radio = document.getElementsByName("version");
	for (var i = 0; i < radio.length; i++) {
		if (radio[i].checked == true) {
			value = radio[i].value;
			break;
		}
	}
	return value;
}
// grid加载数据
var lastrow, lastcell;

function pageInit() {
	jQuery("#setTable").jqGrid("clearGridData");
	var setTableGrid = $("#setTable").jqGrid({
		width : 930,
		dataType : 'local',
		cellEdit : true,
		cellsubmit : "clientArray",
		colNames : [ "id", "Name", "Syntax", "Value", "oid", "access" ],
		colModel : [
		// {name:"rowid",index:"rowid",align:'center'},
		{
			name : "id",
			index : "id",
			align : 'center'
		}, {
			name : "name",
			index : "name",
			align : 'center'
		}, {
			name : "syntax",
			index : "syntax",
			align : 'center'
		}, {
			name : "value",
			index : "value",
			align : 'center',
			editable : true
		}, {
			name : "oid",
			index : "oid",
			align : 'center'
		}, {
			name : "access",
			index : "access",
			align : 'center'
		} ],

		viewrecords : true,
		rowNum : 15,
		// autoHeight:true,
		rowList : [ 15, 20, 25, 30 ],
		jsonReader : {
			root : "rows",
			page : "page",
			total : "total",
			records : "records",
			repeatitems : false

		},
		beforeEditCell : function(rowid, cellname, v, iRow, iCol) {
			lastrow = iRow;
			lastcell = iCol;
		},
		pager : "#addpager",
	}).navGrid('#addpager', {
		add : true,
		edit : true,
		del : true,
		search : false,
		refresh : false
	});
	for (var n = 0; n < tree.aNodes.length; n++) {
		if (document.getElementById("ctree" + tree.aNodes[n].id) == null) {
			continue;
		}
		if (document.getElementById("ctree" + tree.aNodes[n].id).checked == true) {
			if (!tree.aNodes[n]._hc) {
				for (var i = 0; i < treeJson.length; i++) {
					if (treeJson[i].id == tree.aNodes[n].id) {
						var rows = [ {
							"id" : treeJson[i].id,
							"name" : treeJson[i].name,
							"syntax" : treeJson[i].syntax,
							"value" : "",
							"oid" : treeJson[i].oid + ".0",
							"access" : treeJson[i].access
						} ];
						$("#setTable").jqGrid('addRowData', i + 1, rows[0]);
						if (treeJson[i].access == "read-only") {
							// jQuery("#setTable").jqGrid('setCell',treeJson[i].id,{color:'red'},{editable:
							// false});
							// jQuery("#setTable").Rows[i].Cells["value"].ReadOnly
							// = true;

						}

						break;
					}
				}

			}
		}
	}
	var ids = $("#setTable").jqGrid('getDataIDs');
	for (var j = 0; j < ids.length; j++) {
		if (setTableGrid.getCell(ids[j], "access") != "read-write") {
			$("#setTable").jqGrid('setCell', ids[j], 'value', '',
					'not-editable-cell');
		}
	}
}

/* 批量Set,提交表单 */
function saveRows() {
	$("#setTable").jqGrid("saveCell", lastrow, lastcell);
	var search = {};
	search["setCommunity"] = $('#setCommunity').val();
	search["host"] = $('#host').val();
	search["version"] = getVersionValue();
	search["oid"] = $('#oid').val();
	search["port"] = $('#port').val();
	search["timeout"] = $('#timeout').val();
	search["retransmits"] = $('#retransmits').val();
	search["nonRepeaters"] = $('#nonRepeaters').val();
	search["maxRepetitions"] = $('#maxRepetitions').val();
	search["data"] = getFormJson();

	// Data=
	var jsonParam = JSON.stringify(search);
	$.ajax({
		type : 'POST',
		url : '/api/snmpBatchSet',
		contentType : "application/json",
		dataType : 'json', // 接受数据格式
		data : jsonParam,
		success : function(data) {
			alert("over");
		}
	});

	// var rowid = jQuery("#setTable").jqGrid('getGridParam', 'selrow');
	// var rowData = jQuery("#setTable").jqGrid('getRowData', rowid);
	// $('#setTable').jqGrid('saveRow',rowid,
	// function(result){
	// if(result.responseText == ""){
	// return false;
	// }
	// $.messager.alert('提示',eval(result.responseText),'info');
	// return true;
	// },
	// "api/batchSet"
	// );

	// var grid = $("#setTable");
	// var ids = grid.jqGrid('getDataIDs');
	//
	// for (var i = 0; i < ids.length; i++) {
	// grid.jqGrid('saveRow', ids[i]);
	// }
}

function getFormJson() {
	var tmp = [];
	var Data = "";
	// 获取当前表格的所有数据
	var o = jQuery("#setTable");
	// 获取当前显示的数据
	var rows = o.jqGrid('getRowData');
	var rowNum = o.jqGrid('getGridParam', 'rowNum'); // 获取显示配置记录数量
	var total = o.jqGrid('getGridParam', 'records'); // 获取查询得到的总记录数量
	// 设置rowNum为总记录数量并且刷新jqGrid，使所有记录现出来调用getRowData方法才能获取到所有数据
	o.jqGrid('setGridParam', {
		rowNum : total
	}).trigger('reloadGrid');
	var rows = o.jqGrid('getRowData'); // 此时获取表格所有匹配的
	o.jqGrid('setGridParam', {
		rowNum : rowNum
	}).trigger('reloadGrid'); // 还原原来显示的记录数量
	// return rows;
	return rows;
}
// tableView操作
function tableView() {
	deleteAll();
	var search = {};
	search["readCommunity"] = $('#readCommunity').val();
	search["setCommunity"] = $('#setCommunity').val();
	search["syntax"] = $('#syntax').val();
	search["host"] = $('#host').val();
	search["version"] = getVersionValue();
	if ($("#nodeName").val().indexOf("Entry") > 0) {
		search["oid"] = $('#oid').val();
	} else {
		search["oid"] = $('#oid').val() + ".1";
	}

	search["port"] = $('#port').val();
	search["setValue"] = $('#setValue').val();
	search["timeout"] = $('#timeout').val();
	search["retransmits"] = $('#retransmits').val();
	search["nonRepeaters"] = $('#nonRepeaters').val();
	search["maxRepetitions"] = $('#maxRepetitions').val();
	search["priPass"] = $('#priPass').val();
	search["authPass"] = $('#authPass').val();
	var operationvar = $('#operation').val();
	$.ajax({
		type : 'POST',
		contentType : "application/json",
		url : "/api/snmpTable",
		data : JSON.stringify(search),
		dataType : 'json',
		cache : false,
		timeout : 600000,
		success : function(data) {
			// ShowDiv('tableMainProduct');
			if (eval(data.result).length == 0) {
				alert("There is no data in the table");
				return;
			}
			createJqgridJson(data.result);
			return;
		},
		error : function(e) {
			$('#result').val(e.responseText);
			console.log("ERROR : ", e);

		}
	})
}
// 创建绑定到jqGrid的数据
var viewlastrow, viewlastcell;
var tablejsonvar=[];
var accessvar = {};
var syntaxvar = {};
function createJqgridJson(str) {
	var names = [];
	var model = [];
	var jsons = [];
	
	var j = {};
	// 构造jqgri表头
	names.push("instance");
	for (var n = 0; n < tree.aNodes.length; n++) {
		var treeNodeOid = tree.aNodes[n].target;
		if (treeNodeOid == undefined) {
			continue;
		}
		for (var i = 0; i < treeJson.length; i++) {
			if (treeNodeOid.indexOf($("#oid").val()) > -1) {
				if (treeJson[i].id == tree.aNodes[n].id) {
					if (treeJson[i].isTableColumn == true) {
						names.push(treeJson[i].name);
						accessvar[treeJson[i].name] = treeJson[i].access;
						syntaxvar[treeJson[i].name] = treeJson[i].syntax;
					}
				}
			}
		}
	}
	// names.push("access");
	// str是后台返回的查询结果，需要对其进行解析，并重新组成json，绑定到jqgrid
	var jsonStr = eval('(' + str + ')');
	// 因snmp4j的table功能返回的数据是按照一列一列返回，在后台封装时，将列号放入到了desc字段中，前台进行解析
	var rownum;
	var jsonarray;
	var jstr = "[";
	$.each(jsonStr, function(index) {
		if (index == 0) {
			rownum = jsonStr[index].desc;
		}
		if (rownum != jsonStr[index].desc) {
			return false;
		}
		var idvar = jsonStr[index].id;
		var breakFlag = false;
		var nameCount = 1;

		$.each(jsonStr, function(index1) {
			if (breakFlag == true) {
				return false;
			}
			if (idvar == jsonStr[index1].id) {
				j.instance = jsonStr[index1].id;
				for (var k = nameCount; k < names.length; k++) {
					if (k == names.length - 1) {
						j[names[k]] = jsonStr[index1].value;
						j["oid"+names[k]]=jsonStr[index1].oid;
						j["update"]="0";
						nameCount++;
						jstr += JSON.stringify(j) + ",";
						breakFlag = true;
						break;
					}

					j[names[k]] = jsonStr[index1].value;
					j["oid"+names[k]]=jsonStr[index1].oid;
					nameCount++;
					break;

				}
			}

		});

	});

	jstr = jstr.substring(0, jstr.length - 1) + "]";
	var jqdata = JSON.parse(jstr);
	// 此处因为数据源数组中的结构相同且不为空，直接遍历索引为0的数据即可
	var name = [];
	$.each(jqdata[0], function(key, value) {
		name.push(key);
		model.push({
			name : key,
			index : key
		});
	});
	// 创建jqGrid组件
	
	jQuery("#viewTable").jqGrid({
		datatype : "json", // 请求数据返回的类型。可选json,xml,txt
		colNames : name, // jqGrid的列显示名字
		colModel : model,
		height : 380,
		cellEdit : true,
		cellsubmit : "clientArray",
		// rowNum : 50, // 一页显示多少条
		// rowList : [ 10, 20, 30 ], // 可供用户选择一页显示多少条
		pager : '#viewpager', // 表格页脚的占位符(一般是div)的id
		sortname : '', // 初始化的时候排序的字段
		sortorder : "", // 排序方式,可选desc,asc
		// mtype : "post", // 向后台请求数据的ajax的类型。可选post,get
		viewrecords : true,
		// caption : "Table View" // 表格的标题名字
		beforeEditCell : function(rowid, cellname, v, iRow, iCol) {
			viewlastrow = iRow;
			viewlastcell = iCol;
			 $("#viewTable").jqGrid('setCell', rowid, 'update', '1', 'not-editable-cell'); 
		}
	});
	// 将jqdata的值循环添加进jqGrid
	for (var i = 0; i <= jqdata.length; i++) {
		jQuery("#viewTable").jqGrid('addRowData', i + 1, jqdata[i]);
	}
	var grid = $("#viewTable");
	// 获取表格的初始话model
	// var colModel = grid.jqGrid().getGridParam("colModel") ;
	var colModel = $('#viewTable').jqGrid('getGridParam', 'colModel');
	var cellLenth = colModel.length;
	// 设置所有列可编辑（如果行数据添加后，只有默认的几列是可修改的，这样做吧）
	for (var i = 1; i < cellLenth; i++) {
		if (accessvar[colModel[i].name] == "read-write")
			colModel[i].editable = true;
			
	}

}
// 动态生成grid前，清空jgrid数据
function deleteAll() {

	// document.getElementById("viewTable").innerHTML = "";
	jQuery("#viewTable").html("");
	$.jgrid.gridUnload("viewTable");

}
// table中的数据修改后保存
function saveTable() {
	$("#viewTable").jqGrid("saveCell", viewlastrow, viewlastcell);
	var search = {};
	search["setCommunity"] = $('#setCommunity').val();
	search["host"] = $('#host').val();
	search["version"] = getVersionValue();
	search["oid"] = $('#oid').val();
	search["port"] = $('#port').val();
	search["timeout"] = $('#timeout').val();
	search["retransmits"] = $('#retransmits').val();
	search["nonRepeaters"] = $('#nonRepeaters').val();
	search["maxRepetitions"] = $('#maxRepetitions').val();
	search["access"]=JSON.stringify(accessvar);
	search["syntax"]=JSON.stringify(syntaxvar);
	search["data"] = getViewTableJson();

	// Data=
	var jsonParam = JSON.stringify(search);
	$.ajax({
		type : 'POST',
		url : '/api/snmpTableBatchSet',
		contentType : "application/json",
		dataType : 'json', // 接受数据格式
		data : jsonParam,
		success : function(data) {
			alert("over");
		}
	});

}
function getViewTableJson() {
	var tmp = [];
	var Data = "";
	// 获取当前表格的所有数据
	var o = jQuery("#viewTable");
	// 获取当前显示的数据
	var rows = o.jqGrid('getRowData');
	var rowNum = o.jqGrid('getGridParam', 'rowNum'); // 获取显示配置记录数量
	var total = o.jqGrid('getGridParam', 'records'); // 获取查询得到的总记录数量
	// 设置rowNum为总记录数量并且刷新jqGrid，使所有记录现出来调用getRowData方法才能获取到所有数据
	o.jqGrid('setGridParam', {
		rowNum : total
	}).trigger('reloadGrid');
	var rows = o.jqGrid('getRowData'); // 此时获取表格所有匹配的
	o.jqGrid('setGridParam', {
		rowNum : rowNum
	}).trigger('reloadGrid'); // 还原原来显示的记录数量
	// return rows;
	return rows;
}