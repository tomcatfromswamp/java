package com.arduino;

        import jssc.SerialPort;
        import jssc.SerialPortEvent;
        import jssc.SerialPortEventListener;
        import jssc.SerialPortException;
        import org.w3c.dom.NodeList;
        import org.xml.sax.InputSource;
        import org.xml.sax.SAXException;

        import javax.xml.crypto.dsig.XMLObject;
        import javax.xml.parsers.DocumentBuilder;
        import javax.xml.parsers.DocumentBuilderFactory;
        import javax.xml.parsers.ParserConfigurationException;
        import java.io.*;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.Properties;
        import java.util.Timer;
        import java.util.TimerTask;

public class Main {

    private static SerialPort serialPort;
    private static Boolean debug;
    private static String name;
    private static String port;
    private static int interval;
    private static Boolean save;
    private static String cfgPath;
    private static String compareData = "";
    private static Boolean isBegin = false;
    private static String servlet;

    public static void main(String[] args) throws IOException {
        if (args[0].equals("-c")) {
            cfgPath = args[1];
        } else {
            cfgPath = "./arduinoTHSmon.conf";
        }
        File file = new File(cfgPath);
        boolean exists = file.exists();
        if (exists) {
            Properties props = new Properties();
            System.out.println("Загружаем конфигурацию из: " + cfgPath);
            props.load(new FileInputStream(new File(cfgPath)));
            save = Boolean.parseBoolean(props.getProperty("save"));
            debug = Boolean.parseBoolean(props.getProperty("debug"));
            name = props.getProperty("name");
            port = props.getProperty("port");
            servlet = props.getProperty("servlet");
            interval = Integer.parseInt(props.getProperty("interval"));
            System.out.println("Имя: " + name);
            if (save) {
                System.out.println("Работа в режиме сохранения данных в БД.");
            } else {
                System.out.println("Работаем в автономном режиме.");
            }
            serialPort = new SerialPort(port);
            try {
                serialPort.openPort();
                serialPort.setParams(9600,8,1,0);
                serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
                serialPort.addEventListener(new PortReader());
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                                serialPort.writeString("0");
                                Thread.sleep(1000);
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer.schedule(timerTask, 1000, (interval * 1000));
                System.out.println("Опрашиваем Arduino каждые " + interval + " секунд.");
                System.out.println("=================================");
            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
        } else {
            System.out.println("Config file isn`t exist!");
        }
    }

    public static class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                String compareDataXML = "";
                try {
                    String buffer = serialPort.readString(event.getEventValue());
                    //System.out.println("Буфер: " + buffer);
                    if (buffer.contains("<?xml")) {
                        //System.out.println("<--begin!");
                        //System.out.println(buffer);
                        compareData = buffer;
                        isBegin = true;
                    } else
                    if (buffer.contains("</data>")) {
                        //System.out.println("end-->");
                        //System.out.println(buffer);
                        compareDataXML = compareData + buffer;
                        compareData = "";
                        isBegin = false;
                    } else {
                        //System.out.println("--body--");
                        //System.out.println(buffer);
                        compareData = compareData + buffer;
                        isBegin = false;
                    }
                    if (compareDataXML.length() > 0) {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        org.w3c.dom.Document doc = db.parse(new InputSource(new StringReader(compareDataXML)));
                        doc.getDocumentElement().normalize();
                        NodeList nodes = doc.getElementsByTagName("sensor");
                        System.out.println(nodes.getLength());
                        for(int i=0;i<nodes.getLength();i++){
                            int id = Integer.parseInt(nodes.item(i).getAttributes().getNamedItem("id").getNodeValue());
                            double temp = Double.parseDouble(nodes.item(i).getAttributes().getNamedItem("temp").getNodeValue());
                            String adr = nodes.item(i).getAttributes().getNamedItem("adr").getNodeValue();
                            if (save) {
                                sendToServer(temp, 0.0, name, id, adr);
                            } else {
                                System.out.println("Id : " + id);
                                System.out.println("Addr : " + adr);
                                System.out.println("Temp : " + temp);
                                System.out.println("-------------------");
                            }
                        }
                    }

                }  catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }

            public void sendToServer(double temp, double humi, String name, int id, String adr) throws IOException {
            final String USER_AGENT = "Mozilla/5.0";
            String url = servlet + "?temp=" + temp + "&humi=" + humi + "&name=" + name + "&id=" + id + "&adr=" + adr;
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