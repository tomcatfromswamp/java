<%@ page import="java.sql.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%
    ArrayList<String> gpioList = new ArrayList<String>();
    ArrayList<String> tempListGlobal = new ArrayList<ArrayList<String>>();
    ArrayList<String> humiListGlobal = new ArrayList<ArrayList<String>>();
    ArrayList<String> timeListGlobal = new ArrayList<ArrayList<String>>();
    ArrayList<String> tempList = new ArrayList<String>();
    ArrayList<String> humiList = new ArrayList<String>();
    ArrayList<String> timeList = new ArrayList<String>();
    Class.forName("org.sqlite.JDBC");
    Connection conn = DriverManager.getConnection("jdbc:sqlite:/var/lib/tomcat7/webapps/ROOT/wstation.db");
    try {
	Statement stat = conn.createStatement();
	ResultSet rs_gpio = stat.executeQuery("select gpio from sensors group by gpio");
	while(rs_gpio.next()){
	    gpioList.add(rs_gpio.getString("gpio"));
	}
	int i = 0;
	for(String gpioVal : gpioList){
	    ResultSet rs = stat.executeQuery("select * from sensors where gpio = '" + gpioVal + "'");
	    while(rs.next()){
		tempList.add(rs.getString("temp"));
		humiList.add(rs.getString("humi"));
		timeList.add(rs.getString("time"));
	    }
	    out.write("i="+i+"<br>");
	    tempListGlobal.add(new ArrayList());
	    tempListGlobal.get(i)=tempList;
	    humiListGlobal.add(humiList);
	    timeListGlobal.add(timeList);
	    out.write("Array size: "+timeListGlobal.get(i).size()+"<br>");
	    for(int j=tempList.size()-1;j>=0;j--){
		tempList.remove(j);
		humiList.remove(j);
		timeList.remove(j);
	    }
	    out.write("after remove: " + tempList.size());
	    i++;
	}
	out.write("Size:" + tempListGlobal.size());
	out.write("Size:" + humiListGlobal.size());
	out.write("Size:" + timeListGlobal.size());
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
    $('#container').highcharts({
        title: {
            text: 'Outdoor temperature',
            x: -20 //center
        },
        subtitle: {
            text: '',
            x: -20
        },
        xAxis: {
            categories: [
	<%
	for(int i = 0; i < timeListGlobal.get(0).size(); i++){
	    out.write("'" + timeListGlobal.get(0).get(i) + "',");
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
        series: [{
            name: 'Outdoor',
            data: [
		    <%
		    for(int i = 0; i < tempListGlobal.get(0).size(); i++){
			out.write(tempListGlobal.get(0).get(i) + ",");
		    }
		    %>
		]
        }]
    });
});
	</script>
    </head>
    <body>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>

<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

    </body>
</html>
