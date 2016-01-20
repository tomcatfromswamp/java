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
  java.util.Date date = new java.util.Date();
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  String datetime = sdf.format(date);
  String outMessage = null;
  String errorSQL = null;
  if(temp!=null | humi!=null | name!=null)
  {
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost/weather_station";
    String username = "root";
    String passwd = "123456";
    String myQuery = "insert into sensors (temp, humi, date, name) values ('" + temp + "', '" + humi + "', '" + datetime + "', '" + name + "')";
    try {
      outMessage = "Добавляем данные в БД...";
      Connection myConnection = null;
      PreparedStatement myPreparedStatement = null;
      ResultSet myResultSet = null;
      Class.forName(driver).newInstance();
      myConnection = DriverManager.getConnection(url, username, passwd);
      myPreparedStatement = myConnection.prepareStatement(myQuery);
      myPreparedStatement.execute();
    } catch (SQLException ex) {
      ex.printStackTrace();
      errorSQL = ex.getMessage();
    }
  } else {
    outMessage  = "Нет данных для обработки!";
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