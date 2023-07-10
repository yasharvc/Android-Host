package com.tribitgroup.http;

import com.tribitgroup.http.handler.HomeRequestHandler;
import com.tribitgroup.http.handler.NullRequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServerThread extends Thread {
    static final int HttpServerPORT = 8888;
    ServerSocket httpServerSocket;
    HttpRequest httpRequest;
    Map<String, String> requestHandlers = new HashMap<>();

    public HttpServerThread(){
        requestHandlers.put("/", HomeRequestHandler.class.toString().split(" ")[1]);
    }

    @Override
    public void run() {
        Socket socket = null;

        try {
            httpServerSocket = new ServerSocket(HttpServerPORT);
            handleHttpRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispose(){
        if (httpServerSocket != null) {
            try {
                httpServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleHttpRequest() throws IOException {
        Socket socket = null;
        BufferedReader is;
        String request;
        String requestAsString;
        try {
            socket = httpServerSocket.accept();
            requestAsString = getRequestAsString(socket);
            httpRequest = new HttpRequest(requestAsString);
            httpRequest.setReceiveBufferSize(socket.getReceiveBufferSize());
            httpRequest.setRawRequest(requestAsString);
            httpRequest.setOutputStream(socket.getOutputStream());

            RequestHandler httpResponseThread = getHandler(socket);
            httpResponseThread.start();
        } catch (IOException e) {
            if(socket != null)
                socket.close();
            e.printStackTrace();
        } finally {
            handleHttpRequest();
        }
    }

    private RequestHandler getHandler(Socket socket) {
        String url = httpRequest.getUrlWithoutParams();
        RequestHandler requestHandler = new NullRequestHandler();
        if(requestHandlers.containsKey(url.toLowerCase().trim())){
            try {
                String className = requestHandlers.get(url.toLowerCase().trim());
                requestHandler = (RequestHandler) Class.forName(className).newInstance();
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                //throw new RuntimeException(e);
            }
        }else{

        }
        requestHandler.setSocket(socket);
        requestHandler.setRequest(httpRequest);
        return requestHandler;
    }

    private String getRequestAsString(Socket socket) throws IOException {
        String request = "";
        InputStream inputStream = socket.getInputStream();
        BufferedReader is = new BufferedReader(new InputStreamReader(inputStream));
        request = getWithRead(is);

        return request.trim();
    }

    private String getWithRead(BufferedReader is) throws IOException {
        int bufferSize = 1024;
        char[] chars = new char[bufferSize];
        int readCount = is.read(chars);
        String request = new String(chars);

        while (readCount >= bufferSize){
            readCount = is.read(chars);
            request += new String(chars);
        }

        return request.trim();
    }

    private String getWithReadLine(BufferedReader is) throws IOException {
        String request = "";
        String line = is.readLine();
        while (line.length() > 0) {
            request += line + "\r\n";
            line = is.readLine();
        }
        return request;
    }
}
