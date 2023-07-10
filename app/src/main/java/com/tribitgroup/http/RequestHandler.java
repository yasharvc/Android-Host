package com.tribitgroup.http;

import java.net.Socket;

public abstract class RequestHandler extends Thread {
    protected HttpRequest request;
    protected Socket socket;
    public abstract boolean isMatch();

    public void setRequest(HttpRequest request){
        this.request = request;
    }

    public void setSocket(Socket socket){
        this.socket = socket;
    }
}
