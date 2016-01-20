<%@ page import="java.sql.*" %><%--
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
  String outMessage = null;
  if(temp!=null | humi!=null | name!=null)
  {
    String driver = "org.mysql.jdbc.Driver";
    String url = "jdbc:mysql://localhost/weather_station";
    String username = "root";
    String passwd = "123456";
    String myQuery = "insert into sensors (temp, humi) values ('" + temp + "', '" + humi + "')";
    try {
      outMessage = "Добавляем данные в БД...";
      Connection myConnection = null;
      PreparedStatement myPreparedStatement = null;
      ResultSet myResultSet = null;
      Class.forName(driver).newInstance();
      myConnection = DriverManager.getConnection(url, username, passwd);
      myPreparedStatement = myConnection.prepareStatement(myQuery);
      myResultSet = myPreparedStatement.executeQuery();
    } catch (SQLException ex) {
      ex.printStackTrace();
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
  </body>
</html>
