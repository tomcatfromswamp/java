<%@ page import="java.sql.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
  Created by IntelliJ IDEA.
  User: tfs
  Date: 19.01.2016
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String temp = request.getParameter("temp");
  String humi = request.getParameter("humi");
  String name = request.getParameter("name");
  String gpio = request.getParameter("gpio");
  java.util.Date cdate = new java.util.Date();
  SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss"); 
  String date = sdf_date.format(cdate);
  String time = sdf_time.format(cdate);
  out.write(date+"<br>");
  out.write(time+"<br>");
  String outMessage = null;
  String errorSQL = null;
  if(temp!=null | humi!=null | name!=null)
  {
    Class.forName("org.sqlite.JDBC");
    Connection conn = DriverManager.getConnection("jdbc:sqlite:/var/lib/tomcat7/webapps/ROOT/wstation.db");
    try {
	Statement stat = conn.createStatement();
	ResultSet rs = stat.executeQuery("insert into sensors (temp, humi, date, time, name, gpio) values ('" + temp + "', '" + humi + "', '" + date + "', '" + time + "', '" + name + "', '" + gpio + "')");
    } catch(SQLException e) {
	out.write(e.getMessage());
    }
  } else {
    outMessage  = "Dont have any data!";
  }

%>
<html>
<head>
  <title>Weather server JSP</title>
</head>
<body>
<%=outMessage%>
<%=errorSQL%>
</body>
</html>