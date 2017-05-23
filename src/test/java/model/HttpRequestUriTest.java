package model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by yunheekim on 2017. 5. 24..
 */
public class HttpRequestUriTest {
    @Test
    public void create() throws Exception {
        HttpRequestUri httpRequestUri = HttpRequestUri.create("/");
        assertThat(httpRequestUri.getPath(), is("/"));
        assertThat(httpRequestUri.getQueryString(), is(""));

        httpRequestUri = HttpRequestUri.create("/index.html");
        assertThat(httpRequestUri.getPath(), is("/index.html"));
        assertThat(httpRequestUri.getQueryString(), is(""));

        httpRequestUri = HttpRequestUri.create("/index.html?user=user&password=password");
        assertThat(httpRequestUri.getPath(), is("/index.html"));
        assertThat(httpRequestUri.getQueryString(), is("user=user&password=password"));
    }

}