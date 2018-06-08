<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.net.InetAddress,java.util.HashMap,java.util.ArrayList,java.io.BufferedReader, java.io.InputStreamReader"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
    <title>Web Based MIB Browser - List Worksation</title>
  </head>
  <script src="js/sarissa.js"></script>
  <script>
    function refreshHost(){
  		var xmlhttp =  new XMLHttpRequest();
      xmlhttp.open('POST', 'hostStatus.jsp', true);
      xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
      xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4) {
          if (xmlhttp.status == 0 || xmlhttp.status == 200){
            var hosts = xmlhttp.responseXML.getElementsByTagName("host");
            for(var i=0; i<hosts.length; i++){
              var ip = hosts[i].firstChild.data;
              var objId = document.getElementById(ip);
              var stat = hosts[i].getAttribute("stat");
              if(objId){
                objId.childNodes[0].childNodes[0].src = "img/"+stat+".gif";
                objId.childNodes[2].childNodes[0].disabled = (stat=='act'?false:true);
              }else{
                manList(document.forms[0].btnAdd, ip, "add", stat);
              }
            }
          }
        }
      }
      xmlhttp.send("flag=all");
      setTimeout("refreshHost()", 10000);
    }
    
    function isValidFormat(ip){
      var add = ip.split(".");
      if(add.length != 4){
        return false;
      }
      for(var i=0;i<add.length;i++){
        if(isNaN(add[i])){
          return false;
        }
        if((add[i]>0||(i!=0&&i!=3))&&add[i]<255){
          continue;
        }else{
          return false;
        }
      }
      return true;
    }
    
    function manList(obj, ip, opr, stat){
      if(opr == "delete"){
        obj.parentNode.parentNode.parentNode.removeChild(obj.parentNode.parentNode);
      }else if(opr == "add"){
        if(!ip){
          ip = document.forms[0].host.value;
          document.forms[0].host.value = "";
        }
        if(!ip){
          alert('Please input the ip!');
          return false;
        }else if(!isValidFormat(ip)){
          alert('Format ip: x.x.x.x!');
          return false;
        }
        if(document.getElementById(ip)!=null){
          alert("It's already on the list!");
          return false;
        }
        var tr = document.createElement("tr");
//        tr.style.border = "1px solid";
        tr.id = ip;
        var td1 = document.createElement("td");
        if(stat){
          td1.innerHTML = "<img src='img/"+stat+".gif' width='35' height='35'/>";
        }else{
          td1.innerHTML = "<img src='img/dact.gif' width='35' height='35'/>";
        }
        var td2 = document.createElement("td");
        td2.innerHTML = ip;
        td2.width = 200;
        var td3 = document.createElement("td");
        if(stat){
          td3.innerHTML = "<input type='button' value='mib status' "+(stat=="act"?"":"disabled")+" onclick=\"document.forms[0].ip.value='"+ip+"';document.forms[0].submit()\"/>";
        }else{
          td3.innerHTML = "<input type='button' value='mib status' disabled "+(stat=="act"?"":"disabled")+" onclick=\"document.forms[0].ip.value='"+ip+"';document.forms[0].submit()\"/>";
        }
        var td4 = document.createElement("td");
        td4.innerHTML = "<input type='button' value='delete' onclick=\"if(confirm('Delete "+ip+" from list?'))manList(this, '"+ip+"', 'delete')\"/>";
        tr.appendChild(td1);
        tr.appendChild(td2);
        tr.appendChild(td3);
        tr.appendChild(td4);
        obj.parentNode.parentNode.parentNode.insertBefore(tr, obj.parentNode.parentNode.nextSibling);
        //return false;
      }
      if(!stat){
        var xmlhttp =  new XMLHttpRequest();
        xmlhttp.open('POST', 'hostProcess.jsp', true);
        xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xmlhttp.send("event="+opr+"&ip="+escape(ip));
      }
    }

    function check(event){
      if(event.keyCode==13){
        document.forms[0].btnAdd.click();
        return false;
      }
    }
  </script>

  <body bgcolor="#CDCDCD">
    <table width="100%" height="100%">
      <tr>
      <td valign="center" align="center">
        <table width="600" style="border:1px solid" bgcolor="#EFEFEF">
          <tr>
            <td valign="bottom" align="center">
              <font size="6" face="Courier">List Workstation</font>
              <hr width="80%" style="border:1px solid"/>
            </td>
            <td valign="top" align="right" title="logout"><img src="img/logout.gif" onclick="window.location='./../process?logout=true'"/></td>
          </tr>
          <tr>
          <td align="center" valign="top" colspan="2">
          <div style="width:600;height:220;overflow:auto">
          <form method="post" action="mainMib.jsp" onsubmit="return false">
            <table border=0 cellpadding=0 cellspacing=0 style="font-family:courier;font-size:2">
              <tr>
                <td colspan="4" align="center"><b>Host IP: </b><input maxlength="20" type="text" name="host" onkeydown="return check(event)"/> <input type="button" name="btnAdd" value="Add" onclick="manList(this,null,'add')"/><br/><br/></td>
              </tr>
            </table>
            <input type="hidden" name="ip"/>
          </form>
          </div>
          </td>
          </tr>
        </table>
      </td>
      </tr>
    </table>    
  </body>
  <script>    
    refreshHost();
  </script>
</html>