package model;

import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunheekim on 2017. 5. 23..
 */

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private String queryString;

    private Map<String, String> headerMap;

    private Map<String, String> cookie;
    private Map<String, String> params;

    private HttpRequest() {

    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        Tuple3<String, String, Map<String, String>> request = parseRequest(br);

        String header = request.v1();
        String body = request.v2();

        httpRequest.headerMap = request.v3();
        httpRequest.cookie = HttpRequestUtils.parseCookies(request.v3().get("Cookie"));

        String[] requestSplit = header.split(" ");

        String method = requestSplit[0];
        String httpRequestUri = requestSplit[1];

        httpRequest.method = method;

        Tuple2<String, String> pathAndQueryString = getPathAndQueryString(method, httpRequestUri, body);

        httpRequest.path = pathAndQueryString.v1();
        httpRequest.queryString = pathAndQueryString.v2();
        httpRequest.params = HttpRequestUtils.parseQueryString(httpRequest.getQueryString());

        return httpRequest;

    }

    private static Tuple2<String, String> getPathAndQueryString(String method, String httpRequestUri, String body) throws UnsupportedEncodingException {

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

        return new Tuple2<>(path, queryString);
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

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String> getCookie() { return cookie; }

    public Map<String, String> getParams() { return params; }

    public String getMethod() {
        return method;
    }

    public String getHeader(String key) {
        return headerMap.get(key);
    }

    public String getParameter(String key) {
        return params.get(key);
    }
}
