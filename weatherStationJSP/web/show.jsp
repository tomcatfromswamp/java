<%@ page import="java.sql.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<% 
    final class Sensor {
	ArrayList<String> tempList = new ArrayList<String>();
	ArrayList<String> humiList = new ArrayList<String>();
	ArrayList<String> timeList = new ArrayList<String>();
	String temp = new String();
	String humi = new String();
	String name = new String();
	String gpio = new String();
    }
    ArrayList<Sensor> sensorsList = new ArrayList<Sensor>();

    Class.forName("org.sqlite.JDBC");
    Connection conn = DriverManager.getConnection("jdbc:sqlite:/var/lib/tomcat7/webapps/ROOT/wstation.db");
    try {
	Statement stat = conn.createStatement();
	ResultSet rs_gpio = stat.executeQuery("select gpio from sensors group by gpio");
	int i = 0;
	while(rs_gpio.next()){
	    sensorsList.add(new Sensor());
	    sensorsList.get(i).gpio=rs_gpio.getString("gpio");
	    i++;
	    }
	i = 0;
	for(Sensor sensor : sensorsList){
	    ResultSet rs = stat.executeQuery("select * from sensors where gpio = '" + sensor.gpio  + "'");
	    int j = 0;
	    while(rs.next()){
		sensorsList.get(i).tempList.add(rs.getString("temp"));
		sensorsList.get(i).humiList.add(rs.getString("humi"));
		sensorsList.get(i).timeList.add(rs.getString("time"));
		j++;
	    }
	i++;
	}
	out.write(""+sensorsList.size());
    } catch(SQLException e) {
	out.write(e.getMessage());
    }
%>
<!DOCTYPE HTML>
<html>
    <head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>Highcharts Example</title>

	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
	<style type="text/css">
${demo.css}
	</style>
	<script type="text/javascript">
$(function () {
    $('#temp').highcharts({
        title: {
            text: 'Temperature',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: [
	<%
	for(int i = 0; i < sensorsList.get(0).timeList.size(); i++){
	    out.write("'" + sensorsList.get(0).timeList.get(i) + "',");
	}
	%>
	]
        },
        yAxis: {
            title: {
                text: 'Temperature (C)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: 'C'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [
	    <%
	    for(int i = 0; i < sensorsList.size(); i++){
            out.write("{\n\t\tname: '" + i + "', \n\t\tdata: [");
		for(int j = 0; j < sensorsList.get(i).tempList.size(); j++){
		    out.write(sensorsList.get(i).tempList.get(j) + ",");
		}
	    out.write("]\n}, ");
	    }
	    %>
        ]
    });
    $('#humi').highcharts({
        title: {
            text: 'Humidity',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: [
	<%
	for(int i = 0; i < sensorsList.get(0).timeList.size(); i++){
	    out.write("'" + sensorsList.get(0).timeList.get(i) + "',");
	}
	%>
	]
        },
        yAxis: {
            title: {
                text: 'Temperature (C)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: 'C'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [
	    <%
	    for(int i = 0; i < sensorsList.size(); i++){
            out.write("{\n\t\tname: '" + i + "', \n\t\tdata: [");
		for(int j = 0; j < sensorsList.get(i).humiList.size(); j++){
		    out.write(sensorsList.get(i).humiList.get(j) + ",");
		}
	    out.write("]\n}, ");
	    }
	    %>
        ]
    });
});
	</script>
    </head>
    <body>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>

<div id="temp" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<div id="humi" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
<div id="cTemp" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
    </body>
</html>
