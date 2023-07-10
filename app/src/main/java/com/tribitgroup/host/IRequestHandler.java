package com.tribitgroup.host;

public interface IRequestHandler {
    boolean isMatch(String url);
    String processRequest(HttpRequest request);
}
