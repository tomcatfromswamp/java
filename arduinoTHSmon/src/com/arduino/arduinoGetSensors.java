package com.arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class arduinoGetSensors {

    private static SerialPort serialPort;
    private static String url;
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;
    private static Boolean dbUse;
    private static String dbType;
    private static String dbHost;
    private static String dbPort;
    private static String dbName;
    private static String dbUser;
    private static String dbPassword;
    private static Boolean debug;
    private static String name;
    private static String port;
    private static int interval;
    private static String currentDate;
    private static String currentTime;
    private static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();
        System.out.println("Загружаем конфигурацию");
        props.load(new FileInputStream(new File("arduinoTHSmon.conf")));
        dbUse = Boolean.parseBoolean(props.getProperty("dbuse"));
        dbType = props.getProperty("dbtype");
        dbHost = props.getProperty("dbhost");
        dbName = props.getProperty("dbname");
        dbUser = props.getProperty("dbuser");
        dbPassword = props.getProperty("dbpasswd");
        debug = Boolean.parseBoolean(props.getProperty("debug"));
        name = props.getProperty("name");
        port = props.getProperty("port");
        interval = Integer.parseInt(props.getProperty("interval"));
        if(dbUse){
            System.out.println("Работа в режиме сохранения данных в БД.");
        } else {
            System.out.println("Работаем в автономном режиме.");
        }
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        serialPort.writeString("3");
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask,1000,(interval*1000));
            System.out.println("Опрашиваем Arduino каждые " + interval + " секунд.");
            System.out.println("=================================");
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }

    private static class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {

            Calendar calendar = new GregorianCalendar();

            if(event.isRXCHAR()  && event.getEventValue() > 6){
                try {
                    String buffer = serialPort.readString();
                    String[] sensors = buffer.split("\\,");
                    double temp =  Double.parseDouble(sensors[0].substring(0,4));
                    double humi =  Double.parseDouble(sensors[1].substring(0,4));
                    currentDate = date.format(calendar.getTime());
                    currentTime = time.format(calendar.getTime());
                    System.out.println(currentDate);
                    System.out.println(currentTime);
                    System.out.println("Температура:  " + temp);
                    System.out.println("Влажность:  " + humi);
                    System.out.println("----------------------");
                    if(dbUse) {
                        try {
                            switch(dbType) {
                                case "postgresql": {
                                    dbPort = "5432";
                                    break;
                                }
                                case "mysql": {
                                    dbPort = "3306";
                                }
                            }
                            url = "jdbc:" + dbType + "://" + dbHost + ":" + dbPort + "/" + dbName;
                            Class.forName("org.postgresql.Driver");
                            String query = "insert into sensors (temp, humi, name, date, time) values ('" + temp + "','" + humi + "','" + name + "','" + currentDate + "','" + currentTime + "')";
                            con = DriverManager.getConnection(url, dbUser, dbPassword);
                            stmt = con.createStatement();
                            rs = stmt.executeQuery(query);
                            con.close();
                        } catch (SQLException sqlEx) {
                            if(debug) {
                                sqlEx.printStackTrace();
                            }
                        } catch (ClassNotFoundException e) {
                           if(debug){
                               e.printStackTrace();
                           }
                        }
                    }
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}