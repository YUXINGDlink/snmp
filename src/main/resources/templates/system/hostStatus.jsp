<%@ page contentType="text/xml"%>
<%@ page import="com.av.*,com.dwipal.*,org.snmp4j.*,org.snmp4j.event.*,org.snmp4j.transport.*,org.snmp4j.smi.*,org.snmp4j.mp.*,java.net.InetAddress,java.util.HashMap,java.util.ArrayList,java.io.BufferedReader, java.io.InputStreamReader"%>
<root>
<%
  final int maxTime = 20000;
  if(session.getAttribute("cSet")==null){
    session.setAttribute("cSet", "public");
  }
  if(session.getAttribute("cGet")==null){
    session.setAttribute("cGet", "public");
  }
  if(application.getAttribute("hashWS")==null){
    HashMap hash = new HashMap();
    HashMap hmStat = new HashMap();
    
    hash.put(request.getServerName(), request.getServerName());
    hmStat.put(request.getServerName(), "act");
    
    application.setAttribute("hashWS", hash);
    application.setAttribute("hashWSstat", hmStat);
    
    application.setAttribute("lastCheck", System.currentTimeMillis()+"");
  }
  HashMap hm = (HashMap)application.getAttribute("hashWS");
  final HashMap hmStat = (HashMap)application.getAttribute("hashWSstat");
  ArrayList al = new ArrayList(hm.values());
  final HttpServletRequest req = request;
  final ServletContext app = application;
  req.setAttribute("ctrStat", "");
  final long start = Long.parseLong(app.getAttribute("lastCheck").toString());
  final long now = System.currentTimeMillis();
  final long diff = Math.abs(now - start);
  for(int i=0; i<al.size(); i++){
    final String ip = al.get(i).toString();
    final JspWriter innerOut = out;

    if(diff > maxTime){
      app.setAttribute("lastCheck", now+"");
    }
    try{
      final int port = session.getAttribute("port") == null?161:Integer.parseInt(session.getAttribute("port").toString());
      Thread t=new Thread(new Runnable() {
        public void run() {
          try{
            if(diff > maxTime){
              Process processPing = Runtime.getRuntime().exec("ping "+ ip + " -n 1 -l 1 -w 1000");
              BufferedReader br= new BufferedReader( new InputStreamReader(processPing.getInputStream()));
              String lnPing = null;
              while ((lnPing = br.readLine()) != null){
                if(lnPing.indexOf("Reply from") == 0){
                  lnPing = "act";
                  SnmpLib snmpLib = new SnmpLib();
                  snmpLib.setHost(ip);
                  snmpLib.setPort(port);
                  snmpLib.setCommunity("public", "public");

                  String oid = ".1.3.6.1.2.1.1.1";              
                  MySnmpResponseHandler hnd = new MySnmpResponseHandler();
                  snmpLib.setSnmpResponseHandler(hnd);    
                  snmpLib.snmpWalk(oid);
                  long start = System.currentTimeMillis();
                  do{
                    long end = System.currentTimeMillis();  
                    if((end-start)>1000){
                      start = -1;
                      hnd.done = true;
                    }
                  }while(!hnd.done);
                  if(start == -1){
                    lnPing = "nav";
                  }else{
                    lnPing = "act";
                  }
                  break;
                }
              }
              if(lnPing == null){
                lnPing = "dact";
              }
              hmStat.put(ip, lnPing);
            }
            innerOut.println("<host stat=\""+hmStat.get(ip)+"\"><![CDATA["+ip+"]]></host>");
            req.setAttribute("ctrStat", req.getAttribute("ctrStat")+"1");
          }catch(Exception e){
          }
        }
      });
      t.start();
      }catch(Exception e){}
  }
  do{
  }while(request.getAttribute("ctrStat").toString().length()!=al.size());
  application.setAttribute("hashWSstat", hmStat);
%>
</root>