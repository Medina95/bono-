package org.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class calculadora {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running=true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean isFirstLine = true;
            String firstLine ="";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if(isFirstLine){
                    firstLine =inputLine;
                    isFirstLine = false;
                }

                if (!in.ready()) {
                    break;
                }
            }

            URI requestURL = getReqURl(firstLine);


            if (requestURL.getPath().startsWith("/compreFlex")) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + calcular(getcommand(requestURL));

            }else {
                outputLine = htmlclient();
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();

        }
        serverSocket.close();
    }

    private static URI getReqURl(String firstLine) {
        String url = firstLine.split(" ")[1];
        try {
            return  new URI(url);
        } catch ( URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String htmlclient(){
        String htmlcode="HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>esta es la calculadora </h1>\n" +
                "</html>";
        return htmlcode;

    }
    public static void bubbleSort(double[] array) {
        int n = array.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    double temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    private static String getcommand (URI fistline){//max(1,2)
      String url= fistline. getQuery().split("=")[1];
     return url;
    }

    public static String calcular(String comando) {
        String methodName = comando.substring(0, comando.indexOf('('));
        //System.out.println(methodName); max
        String[] params = comando.substring(comando.indexOf('(') + 1, comando.indexOf(')')).split(",");
        double[] arguments = Arrays.stream(params).mapToDouble(Double::parseDouble).toArray();
        //System.out.println(params); [1,2]
        try {
            if(methodName.startsWith("bbl")){
                bubbleSort(arguments);
                return "{\"resultado\": " + Arrays.toString(arguments) + "}";
            }else {
                return MathReflexion(methodName, arguments);
            }
        } catch (Exception e) {
            return "{\"resultado\": \"Error: " + e.getMessage() + "\"}";
        }
    }




//base para hacer reflexion visto en clase
    public static  String MathReflexion (String methodName,double[] arguments) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Math.class;
        Class[] paramTypes1 = {double.class};
        Class[] paramTypes2 = {double.class,double.class};

        // para 1 argumento
        if (arguments.length == 1) {
            Method method = c.getMethod(methodName, paramTypes1);
            Object result = method.invoke(null, arguments[0]);
            return "{\"resultado\": " + result.toString() + "}";
            //para 2
        } else if (arguments.length == 2) {
            Method method = c.getMethod(methodName, paramTypes2);
            Object result = method.invoke(null, arguments[0], arguments[1]);
            return "{\"resultado\": " + result.toString() + "}";
        } else {
            return "{\"resultado\": \"Error\"}";
        }
    }
}