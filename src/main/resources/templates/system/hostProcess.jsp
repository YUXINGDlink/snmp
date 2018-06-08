<%@ page import="java.util.HashMap" %>
<%
  String ip = request.getParameter("ip");
  HashMap hm = (HashMap)application.getAttribute("hashWS");
  HashMap hmStat = (HashMap)application.getAttribute("hashWSstat");
  if(request.getParameter("event").equals("delete")){
    hm.remove(ip);
    hmStat.remove(ip);
  }else if(request.getParameter("event").equals("add")){
    hm.put(ip, ip);
    hmStat.put(ip, hmStat.get(ip)==null?"dact":hmStat.get(ip));
  }
  application.setAttribute("hashWS", hm);
%>