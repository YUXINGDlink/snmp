<%@ page import="com.dwipal.*,javax.swing.JTree,java.util.HashMap,javax.swing.tree.*" %>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">
<%
    DwSnmpMibTreeGUI tree1 = (DwSnmpMibTreeGUI)application.getAttribute("snmpMibTree");
    JTree myTree = tree1.getTree();    
    myTree.setSelectionRow(Integer.parseInt(request.getParameter("row")));
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
    DwSnmpMibRecord nodeTree = tree1.getSelectedTreeNode();
%>
  <style>
   TD {
     font-size: 10pt;
     font-family: verdana,helvetica;
     text-decoration: none;
     white-space: nowrap;}
  </style>

<script src="js/sarissa.js"></script>
<script>
  function setValueMib(){
    getValueMib(true);
  }
  
  function getValueMib(set){
		var xmlhttp =  new XMLHttpRequest();
		xmlhttp.open('POST', 'mibValue.jsp', true);
		xmlhttp.onreadystatechange = function() {
		  if (xmlhttp.readyState == 4) {
			  if (xmlhttp.status == 0 || xmlhttp.status == 200){
				  	if(xmlhttp.responseXML.getElementsByTagName("val").length!=0){
              document.forms[0].val.value = xmlhttp.responseXML.getElementsByTagName("val")[0].firstChild.data;
            }else{
              alert(xmlhttp.responseXML.getElementsByTagName("err")[0].firstChild.data);
            }
			  }
		  }
		}
		xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    if(set){
      xmlhttp.send("cGet="+document.forms[0].cGet.value+"&cSet="+document.forms[0].cSet.value+"&set=true&row="+document.forms[0].row.value+"&value="+escape(document.forms[0].valSet.value)+"&oid="+escape(document.forms[0].oid.value));
    }
    else{
      xmlhttp.send("cGet="+document.forms[0].cGet.value+"&cSet="+document.forms[0].cSet.value+"&oid="+escape(document.forms[0].oid.value));
    }
  }
</script>
<body bgcolor="#CDCDCD">
<form>
  <input type="hidden" name="row" value="<%=request.getParameter("row")%>"/>
  <table style="border:1px solid;padding-left:30px;padding-right:30px;" cellpadding="5" cellspacing="0" bgcolor="#FEFEFE">
    <tr>
      <td valign="top" colspan="3" align="center">
        <font size="5" face="Courier" style="font-weight:bold"><br>
        MIB Information on<br>
        <%=session.getAttribute("ip")%></font><br>
        <hr width="80%" style="border:1px solid"/><br>
      </td>
    </tr>
    <tr>
      <td valign="top">OID</td>
      <td valign="top" width="10">:</td>
      <td valign="top">
        <input type="text" name="oid" value="<%=tree1.getOid(node)%>" readonly/>
      </td>
    </tr>
    <tr bgColor="#EFEFEF">
      <td valign="top">Name</td>
      <td valign="top">:</td>
      <td valign="top">
        <%=nodeTree.name%>
      </td>
    </tr>
    <tr>
      <td valign="top">Parent</td>
      <td valign="top">:</td>
      <td valign="top">
        <%=nodeTree.parent%>
      </td>
    </tr>
    <tr bgColor="#EFEFEF">
      <td valign="top">Number</td>
      <td valign="top">:</td>
      <td valign="top">
        <%=nodeTree.number%>
      </td>
    </tr>
    <tr>
      <td valign="top">Access</td>
      <td valign="top">:</td>
      <td valign="top">
        <%=nodeTree.access%>
      </td>
    </tr>
    <tr bgColor="#EFEFEF">
      <td valign="top">Syntax</td>
      <td valign="top">:</td>
      <td valign="top">
        <div style="width:300;height:100;overflow:auto">
          <%
            String syn = nodeTree.syntax;
            if(nodeTree.syntax.length()>0){
              if(nodeTree.syntax.charAt(0) == '\n'){
                syn = nodeTree.syntax.substring(1);
              }
            }
            out.println(syn.replaceAll("\n","<br>"));
          %>
        </div>
      </td>
    </tr>
    <tr>
      <td valign="top">Status</td>
      <td valign="top">:</td>
      <td valign="top">
        <%=nodeTree.status%>
      </td>
    </tr>
    <tr bgColor="#EFEFEF">
      <td valign="top">Description</td>
      <td valign="top">:</td>
      <td valign="top">
        <div style="width:500;height:100;overflow:auto"><%=nodeTree.description%></div>
      </td>
    </tr>
    <tr>
      <td valign="top">Type</td>
      <td valign="top">:</td>
      <td valign="top">
        <%=nodeTree.recordType%>
      </td>
    </tr>
    <tr>
      <td colspan="2"></td>
      <td colspan="2">
        <table>
          <tr>
            <td>Get Community</td>
            <td><input type="text" name="cGet" value="<%=session.getAttribute("cGet")%>"/></td>
            <td><input type="button" value="GET" onclick="getValueMib()"/></td>
          </tr>
          <tr>
            <td>Set Community</td>
            <td><input type="text" name="cSet" <%=nodeTree.isWritable()?"":"disabled"%> value="<%=session.getAttribute("cSet")%>"/></td>
            <td><input type="button" value="SET" onclick="setValueMib()" <%=nodeTree.isWritable()?"":"disabled"%>/></td>
          </tr>     
          <tr>
            <td colspan="3">       
              <input type="button" value="CHANGE HOST" onclick="window.parent.location='hosts.jsp'"/>
            </td>
          </tr>
        </table>
      </td>
    </tr>
    <tr bgColor="#EFEFEF">
      <td valign="top">Value</td>
      <td valign="top">:</td>
      <td valign="top">
        <textarea name="val" cols="58" rows="5" readOnly></textarea>
      </td>
    </tr>
  <%
    if(nodeTree.isWritable()){
  %>
    <tr bgColor="#EFEFEF">
      <td valign="top">Value SET</td>
      <td valign="top">:</td>
      <td valign="top">
        <textarea name="valSet" cols="58" rows="5"></textarea>
      </td>
    </tr>  
  <%
    }
  %>
  </table>  
</form>
</body>