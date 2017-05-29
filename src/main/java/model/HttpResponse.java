package model;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yunheekim on 2017. 5. 25..
 */
public class HttpResponse {

    private OutputStream os;

    private HttpStatus httpStatus;

    private List<String> list = Arrays.asList(new String[]{"Content-Type", "Content-Length", "Location", "Set-Cookie"});

    private Map<String, String> data = new HashMap<>();

    public HttpResponse(OutputStream os) {
        this.os = os;
    }

    public void addHeader(String key, String value) {
        int index = list.stream().map(String::toUpperCase).collect(Collectors.toList()).indexOf(key.toUpperCase());

        if(index != -1) {
            key = list.get(index);
        }

        data.put(key, value);
    }

    public HttpResponse setHttpStatus(int code) {
        httpStatus = HttpStatus.getHttpStatus(code);
        return this;
    }

    public HttpResponse setContentType(String value) {
        data.put("Content-Type", value);
        return this;
    }

    public HttpResponse setContentLength(int value) {
        data.put("Content-Length", String.valueOf(value));
        return this;
    }

    public HttpResponse setLocation(String value) {
        data.put("Location", value);
        return this;
    }

    // TODO Cookie 여러개 추가 가능하도록 수정
    public HttpResponse setSetCookie(String value) {
        data.put("Set-Cookie", value);
        return this;
    }

    public byte[] getResponseHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ");
        sb.append(httpStatus.getCode());
        sb.append(" ");
        sb.append(httpStatus.getText());
        sb.append("\r\n");

        for(String key : data.keySet()) {
            sb.append(key);
            sb.append(": ");
            sb.append(data.get(key));
            sb.append("\r\n");
        }

        sb.append("\r\n");

        return sb.toString().getBytes();
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public enum HttpStatus {
        OK(200, "OK"),
        Found(302, "Found");

        private int code;
        private String text;

        HttpStatus(int code, String text) {
            this.code = code;
            this.text = text;
        }

        public int getCode() {
            return code;
        }

        public String getText() {
            return text;
        }

        public static HttpStatus getHttpStatus(int code) {
            for (HttpStatus httpStatus : HttpStatus.values()) {
                if(httpStatus.getCode() == code) {
                    return httpStatus;
                }
            }

            return null;
        }
    }
}
