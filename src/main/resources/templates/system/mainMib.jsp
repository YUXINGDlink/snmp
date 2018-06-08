<%
  session.setAttribute("ip", request.getParameter("ip"));
%>
<html>
<head>
  <title>Web Based MIB Browser</title>
</head>
 <FRAMESET cols="300,*">
	<FRAME SRC="mibTree.jsp" name="menu" SCROLLING="auto" NORESIZE>
	<FRAME SRC="aboutMib.jsp" name="basefrm">
 </FRAMESET>
</html>