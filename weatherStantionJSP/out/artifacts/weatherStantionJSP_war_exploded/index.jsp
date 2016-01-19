<%--
  Created by IntelliJ IDEA.
  User: tfs
  Date: 19.01.2016
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Weather server JSP</title>
  </head>
  <body>
  <%
    String temp = request.getParameter("temp");
    String humi = request.getParameter("humi");
    String name = request.getParameter("name");
  %>
  </body>
</html>
