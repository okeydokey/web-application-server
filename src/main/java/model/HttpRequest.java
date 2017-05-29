package model;

import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunheekim on 2017. 5. 23..
 */

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private HttpMethod method;
    private String path;

    private Map<String, String> headers;

    private Map<String, String> cookie;
    private Map<String, String> params;

    private HttpRequest() {

    }

    public static HttpRequest from(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        Tuple3<String, String, Map<String, String>> request = parseRequest(br);

        HttpRequest httpRequest = new HttpRequest();

        String header = request.v1();
        String body = request.v2();

        httpRequest.headers = request.v3();
        httpRequest.cookie = HttpRequestUtils.parseCookies(request.v3().get("Cookie"));

        String[] requestSplit = header.split(" ");

        String method = requestSplit[0];
        String httpRequestUri = requestSplit[1];

        httpRequest.method = HttpMethod.getHttpMethod(method);

        Tuple2<String, Map<String, String>> pathAndQueryString = getPathAndParams(method, httpRequestUri, body);

        httpRequest.path = pathAndQueryString.v1();
        httpRequest.params = pathAndQueryString.v2();

        return httpRequest;

    }

    private static Tuple2<String, Map<String, String>> getPathAndParams(String method, String httpRequestUri, String body) throws UnsupportedEncodingException {

        String path = "";
        String queryString = "";

        if (method.equals("GET")) {

            int index = httpRequestUri.indexOf("?");
            boolean hasQueryString = index != -1;

            path = hasQueryString ? httpRequestUri.substring(0, index) : httpRequestUri;
            queryString = hasQueryString ? URLDecoder.decode(httpRequestUri.substring(index + 1), "UTF-8") : "";

        } else if(method.equals("POST")) {

            path = httpRequestUri;
            queryString = body;

        } else {
            //TODO method 별 특성 파악 필요
        }

        return new Tuple2<>(path, HttpRequestUtils.parseQueryString(queryString));
    }

    private static Tuple3<String, String, Map<String, String>> parseRequest(BufferedReader br) throws IOException {
        int contentLength = 0;

        String header = "";
        Map<String, String> headerMap = new HashMap<>();
        String line;

        while((((line = br.readLine())) != null) && !line.equals("")) {

            HttpRequestUtils.Pair headerPair = HttpRequestUtils.parseHeader(line);

            if(headerPair != null ) {
                headerMap.put(headerPair.getKey(), headerPair.getValue());
            }

            header += line;
        }

        contentLength = headerMap.get("Content-Length") != null ? Integer.parseInt(headerMap.get("Content-Length")) : contentLength;

        String body = IOUtils.readData(br, contentLength);

        log.debug("Request Header: {} , Body: {}, HeaderMap: {}", header, body, headerMap);

        return new Tuple3<>(header, body, headerMap);
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getCookie() { return cookie; }

    public Map<String, String> getParams() { return params; }

    public HttpMethod getMethod() {
        return method;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }

    public enum HttpMethod {
        GET("GET"),
        POST("POST");

        String name;

        HttpMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isGet() {
            return this == GET;
        }

        public boolean isPost() {
            return this == POST;
        }

        public static HttpMethod getHttpMethod(String name) {
            for (HttpMethod httpMethod : HttpMethod.values()) {
                if(httpMethod.getName().equals(name)) {
                    return httpMethod;
                }
            }

            return null;
        }
    }
}
