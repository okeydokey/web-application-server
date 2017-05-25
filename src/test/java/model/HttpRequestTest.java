package model;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by yunheekim on 2017. 5. 24..
 */
public class HttpRequestTest {
    @Test
    public void create() throws Exception {

        BufferedReader br = new BufferedReader(new StringReader("GET / HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Accept: */*"));

        HttpRequest httpRequest = HttpRequest.create(br);
        assertThat(httpRequest.getPath(), is("/"));
        assertThat(httpRequest.getQueryString(), is(""));

        br = new BufferedReader(new StringReader("GET /index.html HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Cookie: logined=true;user=yhkim" + System.getProperty("line.separator") +
                "Accept: */*"));

        Map<String, String> cookie = new HashMap<>();
        cookie.put("logined", "true");
        cookie.put("user", "yhkim");

        httpRequest = HttpRequest.create(br);
        assertThat(httpRequest.getPath(), is("/index.html"));
        assertThat(httpRequest.getQueryString(), is(""));
        assertThat(httpRequest.getCookie(), is(cookie));

        br = new BufferedReader(new StringReader("GET /user/create?userId=javajigi&password=password HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Accept: */*"));

        httpRequest = HttpRequest.create(br);
        assertThat(httpRequest.getPath(), is("/user/create"));
        assertThat(httpRequest.getQueryString(), is("userId=javajigi&password=password"));

        br = new BufferedReader(new StringReader("POST /user/create HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Content-Length: 46" + System.getProperty("line.separator") +
                "Accept: */*" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "userId=javajigi&password=password&name=JaeSung"));

        httpRequest = HttpRequest.create(br);
        assertThat(httpRequest.getPath(), is("/user/create"));
        assertThat(httpRequest.getQueryString(), is("userId=javajigi&password=password&name=JaeSung"));
    }

}