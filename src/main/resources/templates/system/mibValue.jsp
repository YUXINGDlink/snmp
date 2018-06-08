<%@ page import="com.av.*,com.dwipal.*,javax.swing.JTree,java.util.HashMap,javax.swing.tree.*,org.snmp4j.*,org.snmp4j.event.*,org.snmp4j.transport.*,org.snmp4j.smi.*,org.snmp4j.mp.*" %>
<%@ page contentType="text/xml" %>
<root>
<%
    if(session.getAttribute("user").equals("user")){
      out.println("<err>Only admin can get/set value</err>");
    }else{
      DwSnmpMibTreeGUI tree1 = (DwSnmpMibTreeGUI)application.getAttribute("snmpMibTree");
      DwSnmpMibBrowserFunctions snmp = tree1.getSnmpObject();
      String ip = session.getAttribute("ip") == null?"127.0.0.1":session.getAttribute("ip").toString();
      int port = session.getAttribute("port") == null?161:Integer.parseInt(session.getAttribute("port").toString());
      String oid = request.getParameter("oid");
      
      if(request.getParameter("cSet")!=null){
        session.setAttribute("cSet", request.getParameter("cSet"));
      }
      if(request.getParameter("cGet")!=null){
        session.setAttribute("cGet", request.getParameter("cGet"));
      }
      
      if(session.getAttribute("cSet")==null){
        session.setAttribute("cSet", "public");
      }
      if(session.getAttribute("cGet")==null){
        session.setAttribute("cGet", "public");
      }
      
      snmp.setIP(ip);
      snmp.setPort(port);
  
      SnmpLib snmpLib = new SnmpLib();
      snmpLib.setHost(ip);
      snmpLib.setPort(port);
      snmpLib.setCommunity(session.getAttribute("cGet").toString(), session.getAttribute("cSet").toString());
  
      if (oid.endsWith("0")) {
        oid = oid.substring(0, oid.lastIndexOf("."));
      } else if (oid.endsWith("*")) {
        oid = oid.substring(0, oid.lastIndexOf("*") - 1);
      } else if (oid.endsWith(")")) {
        oid = oid.substring(0, oid.indexOf("(") - 1);
      } else {
      }
      boolean processGet = true;
      if(request.getParameter("set") != null){
        JTree myTree = tree1.getTree();    
        myTree.setSelectionRow(Integer.parseInt(request.getParameter("row")));
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
        DwSnmpMibRecord nodeTree = tree1.getSelectedTreeNode();
        
        String setValue = request.getParameter("value");
        if(setValue!=null && nodeTree.checkValidValue(setValue)) {
          try{
            snmpLib.snmpSetValue(request.getParameter("oid"), nodeTree.getSyntaxID(), setValue);
          }catch(Exception e){
            out.println("<err>Error in set Value</err>");
            processGet = false;
          }
        }else{
          out.println("<err>Value is not valid</err>");
          processGet = false;
        }
      }
      if(processGet){
        MySnmpResponseHandler hnd = new MySnmpResponseHandler();
        snmpLib.setSnmpResponseHandler(hnd);    
        snmpLib.snmpWalk(oid);
        long start = System.currentTimeMillis();
        do{
          long end = System.currentTimeMillis();  
          if((end-start)>5000){
            out.println("<err>Timeout</err>");
            start = -1;
            hnd.done = true;
          }
        }while(!hnd.done);
        if(start != -1){
          out.println("<val><![CDATA["+hnd.value+"]]></val>");
        }
      }
    }
%>
</root>