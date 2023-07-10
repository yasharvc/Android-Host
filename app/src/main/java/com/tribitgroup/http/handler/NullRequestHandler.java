package com.tribitgroup.http.handler;

import com.tribitgroup.http.RequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class NullRequestHandler extends RequestHandler {

    String h1;

    public NullRequestHandler(){
        Date currentTime = Calendar.getInstance().getTime();
        h1 = "Null Response";
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