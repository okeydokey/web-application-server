package model;

import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by yunheekim on 2017. 5. 23..
 */
public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    String path;
    String queryString;
    String accept;

    Map<String, String> cookie;

    private HttpRequest() {

    }

    public static HttpRequest create(BufferedReader br) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        Tuple4<String, String, String, String> request = parseRequest(br);

        String header = request.v1();
        String body = request.v2();
        httpRequest.cookie = HttpRequestUtils.parseCookies(request.v3());
        httpRequest.accept = request.v4();

        Tuple2<String, String> pathAndQueryString = getPathAndQueryStringByHeaderAndBody(header, body);

        httpRequest.path = pathAndQueryString.v1();
        httpRequest.queryString = pathAndQueryString.v2();


        return httpRequest;

    }

    private static Tuple2<String, String> getPathAndQueryStringByHeaderAndBody(String header, String body) throws UnsupportedEncodingException {
        String[] requestSplit = header.split(" ");

        String method = requestSplit[0];
        String httpRequestUri = requestSplit[1];

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

    private static Tuple4<String, String, String, String> parseRequest(BufferedReader br) throws IOException {
        int contentLength = 0;

        String header = "";
        String line;
        String cookie = "";
        String accept = "";

        while((((line = br.readLine())) != null) && !line.equals("")) {

            HttpRequestUtils.Pair headerPair = HttpRequestUtils.parseHeader(line);

            if(headerPair != null && headerPair.getKey().equals("Content-Length")) {
                contentLength = Integer.parseInt(headerPair.getValue());
            } else if(headerPair != null && headerPair.getKey().equals("Cookie")) {
                cookie = headerPair.getValue();
            } else if(headerPair != null && headerPair.getKey().equals("Accept")) {
                accept = headerPair.getValue();
            }

            header += line;
        }

        String body = IOUtils.readData(br, contentLength);

        log.debug("Request Header: {} , Body: {}, Cookie: {}, Accept: {}", header, body, cookie, accept);

        return new Tuple4<>(header, body, cookie, accept);
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String> getCookie() { return cookie; }

    public String getAccept() {return accept;}
    @Override
    public String toString() {
        return "HttpRequest{" +
                "path='" + path + '\'' +
                ", queryString='" + queryString + '\'' +
                '}';
    }
}
