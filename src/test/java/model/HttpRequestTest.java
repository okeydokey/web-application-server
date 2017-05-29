package model;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.*;
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

        InputStream header = new ByteArrayInputStream(("GET / HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Accept: */*").getBytes("UTF-8"));

        HttpRequest httpRequest = HttpRequest.from(header);
        assertThat(httpRequest.getMethod(), is(HttpRequest.HttpMethod.GET));
        assertThat(httpRequest.getPath(), is("/"));
        assertThat(httpRequest.getParams(), is(Maps.newHashMap()));
        assertThat(httpRequest.getHeader("Connection"), is("keep-alive"));
        assertThat(httpRequest.getHeader("Host"), is("localhost:8080"));
        assertThat(httpRequest.getHeader("Accept"), is("*/*"));

        header = new ByteArrayInputStream(("GET /index.html HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Cookie: logined=true;user=yhkim" + System.getProperty("line.separator") +
                "Accept: */*").getBytes("UTF-8"));

        Map<String, String> cookie = new HashMap<>();
        cookie.put("logined", "true");
        cookie.put("user", "yhkim");

        httpRequest = HttpRequest.from(header);
        assertThat(httpRequest.getMethod(), is(HttpRequest.HttpMethod.GET));
        assertThat(httpRequest.getPath(), is("/index.html"));
        assertThat(httpRequest.getParams(), is(Maps.newHashMap()));
        assertThat(httpRequest.getCookie(), is(cookie));

        header = new ByteArrayInputStream(("GET /user/from?userId=javajigi&password=password HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Accept: */*").getBytes("UTF-8"));

        Map<String, String> params = new HashMap<>();
        params.put("userId", "javajigi");
        params.put("password", "password");

        httpRequest = HttpRequest.from(header);
        assertThat(httpRequest.getMethod(), is(HttpRequest.HttpMethod.GET));
        assertThat(httpRequest.getPath(), is("/user/from"));
        assertThat(httpRequest.getParams(), is(params));
        assertThat(httpRequest.getParameter("userId"), is("javajigi"));

        header = new ByteArrayInputStream(("POST /user/from HTTP/1.1" + System.getProperty("line.separator") +
                "Host: localhost:8080" + System.getProperty("line.separator") +
                "Connection: keep-alive" + System.getProperty("line.separator") +
                "Content-Length: 46" + System.getProperty("line.separator") +
                "Accept: */*" + System.getProperty("line.separator") +
                System.getProperty("line.separator") +
                "userId=javajigi&password=password&name=JaeSung").getBytes("UTF-8"));

        params = new HashMap<>();
        params.put("userId", "javajigi");
        params.put("password", "password");
        params.put("name", "JaeSung");

        httpRequest = HttpRequest.from(header);
        assertThat(httpRequest.getMethod(), is(HttpRequest.HttpMethod.POST));
        assertThat(httpRequest.getPath(), is("/user/from"));
        assertThat(httpRequest.getParams(), is(params));
        assertThat(httpRequest.getParameter("userId"), is("javajigi"));
    }

}