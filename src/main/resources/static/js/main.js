$(document).ready(function() {

	$('#search-form').submit(function(event) {
		// stop submit the form, we will post it manually.
		event.preventDefault();
		fire_ajax_submit();
	});
});

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
	$("#setDiv").hide();
	$("#resultDiv").show();
	$("#tableDiv").hide();
	$("#bth-getlist").attr("disabled",true);
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
	if ($('#file').val().length == 0) {
		return;
	}
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
			treeJson=mergeJsonObject(treeJson,data);
			var tt = "";
			var firstNodes = 0;
			$.each(data, function(index) {
				// 循环获取数据

				var nodeId = data[index].id;
				var parentId = data[index].parent;
				var value = data[index].value;
				var oid = data[index].oid;
				var nodeN = data[index].name;
				var syntax = data[index].syntax;
				var desc = data[index].desc;
				if (firstNodes == 0) {
					
					firstNodes = 1;
					if (i > 0) {
						tree.add(parseInt(i.toString() + nodeId), 0, nodeN,
								"javaScript:onClickTreeNode('" + oid + "');",
								nodeN, oid, "", "", false);
					} else {
						tree.add(nodeId, 0, nodeN,
								"javaScript:onClickTreeNode('" + oid + "');",
								nodeN, oid, "", "", false);
					}
				} else {
					if (i > 0) {
						tree.add(parseInt(i.toString() + nodeId),
								parseInt(i.toString() + parentId),
								nodeN, "javaScript:onClickTreeNode('" + oid
										+ "');", nodeN, oid, "", "", false);

					} else {
						tree.add(nodeId, parentId, nodeN,
								"javaScript:onClickTreeNode('" + oid + "');",
								nodeN, oid, "", "", false);
					}
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
			$("#nodeN").val(treeJson[index].name);
			return false;
		}

	});
	if ($("#nodeN").val().indexOf("Entry") > 0 || $("#nodeN").val().indexOf("Table") > 0 ) {
		$("#bth-tableview").attr("disabled", false);
	}else{
		$("#bth-tableview").attr("disabled", true);
	}
}
// 当操作选择set时，setvalue可输入数据
function doSelectOper() {
	if ($("#operation").val() == 3) {
		$("#setValue").attr("disabled", false);
	} else {
		$("#setValue").attr("disabled", true);
	}

}
// 右键菜单，未实现
function rightMouse() {
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
var editVal;
var setTableGrid;
var jqdata;
function pageInit() {
	jQuery("#setTable").jqGrid("clearGridData");
	setTableGrid = $("#setTable").jqGrid(
			{
				width : "90%",
				height:260,
				dataType : 'local',
				cellEdit : true,
				cellsubmit : "clientArray",
				colNames : [ "id", "Name", "Syntax", "Value", "oid", "access",
						"update" ],
				colModel : [
				// {name:"rowid",index:"rowid",align:'center'},
				{
					name : "id",
					index : "id",
					align : 'center',hidden:true
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
				}, {
					name : "update",
					index : "update",
					align : 'center',
					hidden : true
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
				afterEditCell : function(rowid, celname, value, iRow, iCol) {
					lastrow = iRow;
					lastcell = iCol;
					editVal = value;
				},
				beforeSaveCell : function(rowid, celname, value, iRow, iCol) {
					if (editVal != value) {
						$("#setTable").jqGrid('setCell', rowid, 'update', '1',
						'not-editable-cell');
						$("#setTable").jqGrid('setCell', rowid, celname, '', {
							background : 'LightGreen'
						});
						$("#bth-batchsetsave").attr("disabled", false);
					}
					editVal = "";
				}
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
	getList();
}

/* 批量Set,提交表单 */
function saveMultiSet() {
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
			alert(data.result);
		}
	});

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
	if ($("#nodeN").val().indexOf("Entry") > 0) {
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
var tablejsonvar = [];
var accessvar = {};
var syntaxvar = {};
var names ;
var viewTableGrid;

function createJqgridJson(str) {
	
	var model = [];
	var jsons = [];
	names=[];
	var j = {};
	// 构造jqgri表头
	names.push("instance");
	for (var n = 0; n < tree.aNodes.length; n++) {
		var treeNodeOid = tree.aNodes[n].target;
		if (treeNodeOid == undefined ) {
			continue;
		}
		for (var i = 0; i < treeJson.length; i++) {
			if (treeNodeOid.indexOf($("#oid").val()) > -1) {
				
				if (treeJson[i].id == tree.aNodes[n].target.replace(/[\.]/g,"")) {
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
// if (accessvar[names[k]] == "read-write") {
							j["oid" + names[k]] = jsonStr[index1].oid;
// } else {
// j["oid" + names[k]] = "";
// }
						j["update"] = "0";
						nameCount++;
						jstr += JSON.stringify(j) + ",";
						breakFlag = true;
						break;
					}

					j[names[k]] = jsonStr[index1].value;
// if (accessvar[names[k]] == "read-write") {
						j["oid" + names[k]] = jsonStr[index1].oid;
// } else {
// j["oid" + names[k]] = "";
// }
					nameCount++;
					break;

				}
			}

		});

	});

	jstr = jstr.substring(0, jstr.length - 1) + "]";
	jqdata = JSON.parse(jstr);
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
	// 计算viewTable的宽度
	var widths=document.body.scrollWidth;
	var aa=Math.round(($("#tableDiv").width()/$(document).width())*100);// search_tb某个div
	var bb=widths*aa/100-10;
	var selectRowid;
	viewTableGrid=jQuery("#viewTable").jqGrid(
			{
				datatype : "json", // 请求数据返回的类型。可选json,xml,txt
				colNames : name, // jqGrid的列显示名字
				colModel : model,
				height:260,
				width:bb,
				
		        shrinkToFit: false,
		        scrollrows: true,
				cellEdit : false,
				cellsubmit : "clientArray",
				// rowNum : 50, // 一页显示多少条
				// rowList : [ 10, 20, 30 ], // 可供用户选择一页显示多少条
				// pager : '#viewpager', // 表格页脚的占位符(一般是div)的id
				sortname : '', // 初始化的时候排序的字段
				sortorder : "", // 排序方式,可选desc,asc
				// mtype : "post", // 向后台请求数据的ajax的类型。可选post,get
				viewrecords : true,
				// caption : "Table View" // 表格的标题名字
// multiselect: true,
// multiboxonly:true,
// beforeSelectRow: beforeSelectRow,// js方法
				beforeSelectRow: function (rowid, e) {
					if(selectRowid!=rowid){
						$("#"+selectRowid+ " td").css("background-color","white");
						
					}
					selectRowid=rowid;
					$("#viewTable").jqGrid('resetSelection'); 
					$("#viewTable").setSelection(rowid);
					$("#"+rowid+ " td").css("background-color","lightGreen");
					return(true);
// var $myGrid = $(this),
// i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
// cm = $myGrid.jqGrid('getGridParam', 'colModel');
// return (cm[i].name === 'cb');
					},
				afterEditCell : function(rowid, celname, value, iRow, iCol) {
					viewlastrow = iRow;
					viewlastcell = iCol;
					editVal = value;
				},
				beforeSaveCell : function(rowid, celname, value, iRow, iCol) {
					if (editVal != value) {
						$("#viewTable").jqGrid('setCell', rowid, 'update', '1',
								'not-editable-cell');
						$("#viewTable").jqGrid('setCell', rowid, celname, '', {
							background : 'LightGreen'
						});
						$("#bth-tablesave").attr("disabled", false);
					}
					editVal = "";
				}
			});

	

	// 将jqdata的值循环添加进jqGrid
	for (var i = 0; i <= jqdata.length; i++) {
		jQuery("#viewTable").jqGrid('addRowData', i + 1, jqdata[i]);
		// jQuery("#viewTable").jqGrid('setCell',jqdata[i].instance,jqdata[i].ipRouteDest,{color:'red'});
	}
	var grid = $("#viewTable");
	// 获取表格的初始话model
	// var colModel = grid.jqGrid().getGridParam("colModel") ;
	var colModel = $('#viewTable').jqGrid('getGridParam', 'colModel');
	var cellLenth = colModel.length;
	// 设置所有列可编辑（如果行数据添加后，只有默认的几列是可修改的，这样做吧）
	for (var i = 1; i < cellLenth; i++) {
		if (accessvar[colModel[i].name] == "read-write") {
			colModel[i].editable = true;
			colModel[i].style = "background-color:#ff0000";
			// grid.jqGrid('setCell',colModel[i],"",{color:'red'});
			// $("#viewTable").setColProp(colModel[i],{color:'red'});
			// colModel[i].css("background-color", "pink");

		}
 if (colModel[i].name.substring(0, 3) == "oid") {
 jQuery("#viewTable").setGridParam().hideCol(colModel[i].name)
 .trigger("reloadGrid");
 }
	}
	jQuery("#viewTable").setGridParam().hideCol("update").trigger("reloadGrid");
	$("#cb_"+grid[0].id).hide();
	showTable();
}
// tableView功能中的只选中一行
// function beforeSelectRow()
// {
// // $("#viewTable").jqGrid('resetSelection');
// // return(true);
// var $myGrid = $(this),
// i = $.jgrid.getCellIndex($(e.target).closest('td')[0]),
// cm = $myGrid.jqGrid('getGridParam', 'colModel');
// return (cm[i].name === 'cb');
//
// }
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
	search["access"] = JSON.stringify(accessvar);
	search["syntax"] = JSON.stringify(syntaxvar);
	search["data"] = getFormJsonTable();

	// Data=
	var jsonParam = JSON.stringify(search);
	$.ajax({
		type : 'POST',
		url : '/api/snmpTableBatchSet',
		contentType : "application/json",
		dataType : 'json', // 接受数据格式
		data : jsonParam,
		success : function(data) {
			alert(data.result);
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

function treeInit() {
	$("#file").val("");
	tree = new dTree('tree');
	tree.add(0, -1,
			'<a href="javascript:" oncontextmenu="rightMouse(1);">ISO</a>');
	tree.draw();

}
// tableview中修改某一行数据
function editRow(){
	
	
}
function showSetDiv(){
	$("#setDiv").show();
	$("#resultDiv").hide();
	$("#tableDiv").hide();
	$("#bth-getlist").attr("disabled", false);
	
}
function showTable(){
	$("#setDiv").hide();
	$("#resultDiv").hide();
	$("#tableDiv").show();
	$("#bth-getlist").attr("disabled", false);
	
}
function getList(){
	var ids = $("#setTable").jqGrid('getDataIDs');
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
	
	var jsonParam = JSON.stringify(search);
	$.ajax({
		type : 'POST',
		url : '/api/snmpGetList',
		contentType : "application/json",
		dataType : 'json', // 接受数据格式
		data : jsonParam,
		success : function(data) {
			var jqdata = JSON.parse(data.result);
			jQuery("#setTable").jqGrid("clearGridData");
			for (var i = 0; i <= jqdata.length; i++) {
				jQuery("#setTable").jqGrid('addRowData', i + 1, jqdata[i]);
// jQuery("#viewTable").jqGrid('setCell',jqdata[i].value,jqdata[i].ipRouteDest,{color:'red'});
			}
			var ids = $("#setTable").jqGrid('getDataIDs');
			for (var j = 0; j < ids.length; j++) {
				if (setTableGrid.getCell(ids[j], "access") != "read-write") {
					$("#setTable").jqGrid('setCell', ids[j], 'value', '',
							'not-editable-cell');
				}
			}
			jQuery("#setTable").setGridParam().trigger("reloadGrid");
		}
	});
	
}
function editRow(){
	var selectRowid;
	
	jQuery("#editRowTable").jqGrid("clearGridData");
	 $("#editRowTable").jqGrid(
			{
				width : "95%",
				height:400,
				dataType : 'local',
				cellEdit : true,
				cellsubmit : "clientArray",
// multiselect :true,
				colNames : [ "id","operate", "Name", "Syntax", "Value", "oid", "access"],
				colModel : [
				// {name:"rowid",index:"rowid",align:'center'},
					{	
						name : "id",
						index : "id",
						align : 'center',hidden:true
					}, {
	                    label: 'operate', name: 'operate', index: 'operate', align: 'center',
	                    formatter: function (cellvalue, options, rowObject) {
	                        return "<a href='javascript:BindBtnUpMethod("+rowObject.id+")'>up </a>  <a href='javascript:BindBtnDownMethod("+rowObject.id+")'> down </a> <a href='javascript:BindBtnLeftMethod("+rowObject.id+")'>delete</a>";
	                       
	                    },
	                },{	
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
					align : 'center',hidden:true
				}, {
					name : "access",
					index : "access",
					align : 'center'
				}],

				viewrecords : true,
				rowNum : 15,
				rowList : [ 15, 20, 25, 30 ],
				jsonReader : {
					root : "rows",
					page : "page",
					total : "total",
					records : "records",
					repeatitems : false

				},
					afterEditCell : function(rowid, celname, value, iRow, iCol) {
						$("#bth-tablesave").attr("disabled", false);
						lastrow = iRow;
						lastcell = iCol;
						editVal = value;
					},
					beforeSaveCell : function(rowid, celname, value, iRow, iCol) {
						if (editVal != value) {
							$("#editRowTable").jqGrid('setCell', rowid, 'update', '1',
							'not-editable-cell');
							$("#bth-tablesave").attr("disabled", false);
						}
						editVal = "";
					}
			});
	 	var id= $("#viewTable").jqGrid('getGridParam','selrow');
		var oidSel=viewTableGrid.getCell(id, "oid")
				for (var j = 0; j < names.length; j++) {
					var name=names[j];
					if(name.substring(0,3)=="oid" || name=="instance")continue;


						var rows = [ {
							"id" : j+1,
							"name" : name,
							"syntax" : syntaxvar[name],
							"value" : viewTableGrid.getCell(id, name),
							"oid" :viewTableGrid.getCell(id, "oid"+name) + ".0",
							"access" : accessvar[name]
						} ];
						$("#editRowTable").jqGrid('addRowData', j + 1, rows[0]);
					if (accessvar[name] != "read-write") {
						$("#editRowTable").jqGrid('setCell', j+1, 'value', '',
								'not-editable-cell');
					}
						
						
					
				}
}


// 摘要：
// 内容：按钮左移
function BindBtnLeftMethod(rowid) {
	
// 删除选中行
// rowid++;
// var rowid = $("#editRowTable").jqGrid('getGridParam', 'selrow');
if (rowid == "" || rowid == undefined || rowid == null) {
   alert("移除行未选中！");
    return;
} else {
    var flag = $("#editRowTable").jqGrid("delRowData", rowid);
    if (!flag) {
       alert("移除操作失败！");
    }
}
}
// 摘要：
// 内容：按钮上移
function BindBtnUpMethod(rowid) {
	$("#editRowTable").jqGrid("saveCell", lastrow, lastcell);
// rowid++;
  var obj = $("#editRowTable");
  var IDs = obj.getDataIDs();
  if (rowid == "" || rowid == null || rowid == undefined) {
     alert("No record selected!");
      return false;
  }
  var rowIndex = getRowIndexByRowId(IDs, rowid);
  if (rowIndex == 0) {
     alert("You can't move upward.!");
  } else {
      var srcrowid = getRowIdByRowIndex(IDs, rowIndex - 2);
      var rowUpId = getRowIdByRowIndex(IDs, rowIndex - 1);
      var rowData = obj.jqGrid('getRowData', rowid);
      var rowUpData = obj.jqGrid('getRowData', rowUpId);
      // 删除当前行
      obj.delRowData(rowid);
      obj.delRowData(rowUpId);
      // 新插入一行
      obj.addRowData(rowid, rowData, "after", srcrowid);
      obj.addRowData(rowUpId, rowUpData, "after", rowid);
      // 默认再选中新增的这个行
      obj.setSelection(rowid);
  }
}

// 摘要：
// 内容：根据rowid，获取索引值
function getRowIndexByRowId(Ids, id) {
  var index = 0;
  for (var i = 0; i < Ids.length; i++) {
      if (Ids[i] == id) {
          index = i;
      }
  }
  return index;
}

// 摘要：
// 内容：根据索引值获取rowid值
function getRowIdByRowIndex(Ids, index) {
  var rowid = "";
  for (var i = 0; i < Ids.length; i++) {
      if (i == index) {
          rowid = Ids[i];
      }
  }
  return rowid;
}

// 摘要：
// 内容：按钮下移
function BindBtnDownMethod(rowid) {
	$("#editRowTable").jqGrid("saveCell", lastrow, lastcell);
// rowid++;
  var obj = $("#editRowTable");
  var IDs = obj.getDataIDs();
// var rowid = obj.jqGrid('getGridParam', 'selrow');
  if (rowid == "" || rowid == null || rowid == undefined) {
     alert("No record selected!");
      return false;
  }
  var rowIndex = getRowIndexByRowId(IDs, rowid);
  if (rowIndex == (IDs.length - 1)) {
     alert('The last line can not be moved down!');
  } else {
      var srcrowid = getRowIdByRowIndex(IDs, rowIndex - 1);
      var rowDownId = getRowIdByRowIndex(IDs, rowIndex + 1);
      var rowData = obj.jqGrid('getRowData', rowid);
      var rowDownData = obj.jqGrid('getRowData', rowDownId);
      // 删除当前行
      obj.delRowData(rowid);
      obj.delRowData(rowDownId);
      // 新插入一行
      obj.addRowData(rowDownId, rowDownData, "after", srcrowid);
      obj.addRowData(rowid, rowData, "after", rowDownId);
      // 默认再选中新增的这个行
      obj.setSelection(rowid);
  }
}

/* 批量Set,提交表单 */
function tableEditSave() {
	$("#editRowTable").jqGrid("saveCell", lastrow, lastcell);
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
	search["access"] = JSON.stringify(accessvar);
	search["syntax"] = JSON.stringify(syntaxvar);
	search["data"] = getFormJsonTable();

	// Data=
	var jsonParam = JSON.stringify(search);
	$.ajax({
		type : 'POST',
		url : '/api/snmpTableBatchSet',
		contentType : "application/json",
		dataType : 'json', // 接受数据格式
		data : jsonParam,
		success : function(data) {
			alert(data.result);
		}
	});

}
function getFormJsonTable() {
	var tmp = [];
	var Data = "";
	// 获取当前表格的所有数据
	var o = jQuery("#editRowTable");
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

/**
 * 合并两个json对象属性为一个对象
 * 
 * @param jsonbject1
 * @param jsonbject2
 * @returns resultJsonObject
 */
function mergeJsonObject (jsonbject1, jsonbject2)
{
var resultJsonObject=[];
var attr2=0;
for(var attr in jsonbject1){
resultJsonObject[attr]=jsonbject1[attr];
attr2++;
}
if(attr2>0){
	attr2--;
	for(var attr in jsonbject2){
		resultJsonObject[attr2]=jsonbject2[attr];
		attr2++;
		}
}else{
	for(var attr in jsonbject2){
		resultJsonObject[attr]=jsonbject2[attr];
		}
}



   return resultJsonObject;
}