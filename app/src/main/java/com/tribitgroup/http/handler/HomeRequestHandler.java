package com.tribitgroup.http.handler;

import com.tribitgroup.http.RequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class HomeRequestHandler extends RequestHandler {

    String h1;

    public HomeRequestHandler(){
        Date currentTime = Calendar.getInstance().getTime();
        h1 = "Test" + currentTime;
    }

    @Override
    public void run() {
        PrintWriter os;

        os = new PrintWriter(request.getOutputStream(), true);
        String pars = "<ul>";

        for (Map.Entry<String, String> val : request.getParams().entrySet()) {
            pars += "<li>" + val.getKey() + " = " + val.getValue() + "</li>";
        }

        pars += "</ul>";

        String response =
                "<html><head></head>" +
                        "<body>" +
                        "<h1>" + h1 + "</h1><p><h2>Request : </h2>" + request.getRawRequest().replace("\r\n", "<br/>") + "</p>" +
                        "<h5>User agent:-" + request.getUserAgent() + "-</h5>" +
                        "<h5>" + request.getAction() + "</h5>" +
                        "<h5>" + request.getUrl() + "</h5>" +
                        "<h5>Content length: " + request.getContentLength() + "</h5>" +
                        "<h5>Content: -" + request.getContent() + "-</h5>" +
                        "<h5>" + request.getUrlWithoutParams() + "</h5>" +
                        "<h5>" + request.getUrlParams() + "</h5>" +
                        "<h5>" + pars  + "</h5>"+
                        "</body>" +
                        "</html>";

        os.print("HTTP/1.0 200" + "\r\n");
        os.print("Content type: text/html" + "\r\n");
        os.print("Content length: " + response.length() + "\r\n");
        os.print("\r\n");
        os.print(response + "\r\n");
        os.flush();
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return;
    }

    @Override
    public boolean isMatch() {
        return request.getUrlWithoutParams().trim().equals("/");
    }
}