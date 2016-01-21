package com.arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import java.io.*;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class arduinoGetSensors {

    private static SerialPort serialPort;
    private static Boolean debug;
    private static String name;
    private static String port;
    private static int interval;
    private static Boolean save;

    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        System.out.println("Загружаем конфигурацию");
        props.load(new FileInputStream(new File("arduinoTHSmon.conf")));
        save = Boolean.parseBoolean(props.getProperty("save"));
        debug = Boolean.parseBoolean(props.getProperty("debug"));
        name = props.getProperty("name");
        port = props.getProperty("port");
        interval = Integer.parseInt(props.getProperty("interval"));
        System.out.println(name);
        if(save){
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
            if(event.isRXCHAR()  && event.getEventValue() > 6){
                try {
                    String buffer = serialPort.readString();
                    String[] sensors = buffer.split("\\,");
                    double temp =  Double.parseDouble(sensors[0].substring(0,4));
                    double humi =  Double.parseDouble(sensors[1].substring(0,4));
                    System.out.println("Температура:  " + temp);
                    System.out.println("Влажность:  " + humi);
                    System.out.println("----------------------");
                    if(save) {
                        sendToServer(temp,humi,name);
                    }
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendToServer(double temp, double humi, String name) throws IOException {
            final String USER_AGENT = "Mozilla/5.0";
            String url = "http://weather-station.x5x.ru:8080/index.jsp?temp=" + temp + "&humi=" + humi + "&name=" + name;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
        }
    }
}