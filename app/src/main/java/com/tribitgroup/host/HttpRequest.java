package com.tribitgroup.host;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpAction action;
    private String url;
    private String userAgent;
    private int receiveBufferSize;
    private int contentLength=0;
    private String rawRequest;
    private Map<String,String> params = new HashMap<>();
    private OutputStream outputStream;
    private String content ="";

    public String getContent() {
        return content;
    }
    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public String getRawRequest() {
        return rawRequest;
    }

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public HttpAction getAction(){
        return action;
    }

    public String getUrlWithoutParams(){
        if(getUrl().indexOf('?') >= 0)
            return getUrl().split("\\?")[0];
        return getUrl();
    }

    public String getUrlParams(){
        if(getUrl().indexOf('?') >= 0)
            return getUrl().split("\\?")[1];
        return "";
    }

    public String getUrl(){
        return url;
    }

    public HttpRequest() {
        this(HttpAction.GET, "");
    }

    public HttpRequest(String rawRequest){
        this.rawRequest = rawRequest;
        action = getActionFromRequest(rawRequest);
        url = getUrlFromRequest(rawRequest);
        params = getParamsFromUrl();
        userAgent = getUserAgentFromRequest(rawRequest);
        contentLength = getContentLengthFromRequest(rawRequest);
        if(contentLength > 0)
            content = rawRequest.substring(rawRequest.length() - contentLength);
    }

    private Integer getContentLengthFromRequest(String rawRequest) {
        if(rawRequest.toLowerCase().indexOf("content-length") < 0)
            return -1;
        try {
            String res = getRequestValue("content-length");
            content = res;
            return Integer.parseInt(res.trim().replace(":",""));
        }catch (Exception ex){
            return -200;
        }
    }

    public HttpRequest(HttpAction action, String url) {
        this.action = action;
        this.url = url;
    }

    public String getUserAgent() {
        return userAgent;
    }

    private Map<String, String> getParamsFromUrl() {
        if(getUrl().indexOf('?') < 0)
            return new HashMap<>();
        String query = getUrl().split("\\?")[1];
        String[] params = query.split("&");
        Map<String, String> map = new HashMap();

        for (String param : params) {
            String name = decodeUrl(param.split("=")[0]);
            String value = decodeUrl(param.split("=")[1]);
            map.put(name, value);
        }
        return map;
    }

    private String getUserAgentFromRequest(String rawRequest) {
        try {
            String res = getRequestValue("user-agent");
            return res;
        }catch (Exception ex){
            return "";
        }
    }

    private String getUrlFromRequest(String rawRequest) {
        try {
            return rawRequest.split(" ")[1].trim();
        }catch (Exception ex){
            return "/";
        }
    }

    private HttpAction getActionFromRequest(String rawRequest) {
        try {
            String actionStr = rawRequest.split(" ")[0].toUpperCase().trim();
            switch (actionStr){
                case "GET":
                    return HttpAction.GET;
                case "POST":
                    return HttpAction.POST;
                case "PUT":
                    return HttpAction.PUT;
                case "DELETE":
                    return HttpAction.DELETE;
            }
            return HttpAction.GET;
        }catch (Exception ex){
            return HttpAction.GET;
        }
    }
    private String decodeUrl(String rawData){
        try {
            return URLDecoder.decode(rawData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private String getRequestValue(String key){
        key = key.toLowerCase() + ":";
        int index = getRawRequest().toLowerCase().indexOf(key);
        String res =
                rawRequest.substring(rawRequest.toLowerCase().indexOf(key) + key.length(),
                rawRequest.indexOf("\n",rawRequest.toLowerCase().indexOf(key)));
        return res;
    }
}