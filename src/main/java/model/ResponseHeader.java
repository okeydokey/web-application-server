package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yunheekim on 2017. 5. 25..
 */
public class ResponseHeader {

    HttpStatus httpStatus;

    List<String> list = Arrays.asList(new String[]{"Content-Type", "Content-Length", "Location", "Set-Cookie"});

    private Map<String, String> data = new HashMap<>();

    public void setHeader(String key, String value) {
        int index = list.stream().map(String::toUpperCase).collect(Collectors.toList()).indexOf(key.toUpperCase());

        if(index != -1) {
            key = list.get(index);
        }

        data.put(key, value);
    }

    public ResponseHeader setHttpStatus(int code) {
        httpStatus = HttpStatus.getHttpStatus(code);
        return this;
    }

    public ResponseHeader setContentType(String value) {
        data.put("Content-Type", value);
        return this;
    }

    public ResponseHeader setContentLength(int value) {
        data.put("Content-Length", String.valueOf(value));
        return this;
    }

    public ResponseHeader setLocation(String value) {
        data.put("Location", value);
        return this;
    }

    // TODO Cookie 여러개 추가 가능하도록 수정
    public ResponseHeader setSetCookie(String value) {
        data.put("Set-Cookie", value);
        return this;
    }

    public String getResponseHeader() {
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

        return sb.toString();
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
