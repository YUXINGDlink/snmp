<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="com.dwipal.*,javax.swing.JTree,java.util.HashMap,javax.swing.tree.*" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
    <title>untitled</title>
  </head>


  <style>
   BODY {
     background-color: #CDCDCD;}
   TD {
     font-size: 10pt;
     font-family: verdana,helvetica;
     text-decoration: none;
     white-space: nowrap;}
   A {
     text-decoration: none;
     color: black;}
  </style>

  <script src="js/ua.js"></script>
  <script src="js/ftiens4.js"></script>
  
  <script>
    USETEXTLINKS = 1
    HIGHLIGHT = 1
    ICONPATH = "img/";
    var foldersTree = gFld("MIB Tree - RFC1213", "aboutMib.jsp");
  <%
    HashMap al = new HashMap();    
    DwSnmpMibTreeGUI tree1 = null;
    if(application.getAttribute("snmpMibTree") == null){
      tree1 = new DwSnmpMibTreeGUI();
      tree1.createComponents();
      application.setAttribute("snmpMibTree", tree1);
    }else{
      tree1 = (DwSnmpMibTreeGUI)application.getAttribute("snmpMibTree");
    }    
    try{
      Thread.sleep(500);
    }catch(Exception e){
    }
    JTree t = tree1.getTree();

    for(int i=0; i<t.getRowCount(); i++){
      t.expandRow(i);
      Object []path = t.getPathForRow(i).getPath();

      t.setSelectionRow(i);
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) t.getLastSelectedPathComponent();

      boolean declare = false;
      if(al.get(path[path.length-1])==null){
        al.put(path[path.length-1], ""+i);
        declare = true;
      }
      if(i==0){
        out.println("var foldersTree"+i+" = insFld(foldersTree, gFld(\""+path[path.length-1]+"\", \"infoMib.jsp?row="+i+"\"));");
      }else if(node.getChildCount()!=0){
        out.println("var foldersTree"+i+" = insFld(foldersTree"+al.get(path[path.length-2])+", gFld(\""+path[path.length-1]+"\", \"infoMib.jsp?row="+i+"\"));");
      }else if(path.length>1){
        out.println("insDoc(foldersTree"+al.get(path[path.length-2])+", gLnk(\"R\", \""+path[path.length-1]+"\", \"infoMib.jsp?row="+i+"\"));");
      }
    }
  %>
  </script>
  <body>
    <input type="button" value="Logout" onclick="window.parent.location='./../process?logout=true'"/>
    <input type="button" value="Change Host" onclick="window.parent.location='hosts.jsp'"/>
    <div style="position:absolute; top:0; left:0;display:none"><table border="0"><tr><td><font size="-2"><a style="font-size:7pt;text-decoration:none;color:silver;" href="http://www.treemenu.net/" target="_blank">JavaScript Tree</a></font></td></tr></table></div>
    <script>initializeDocument();</script>
  </body>
</html>
