package webserver;

import model.HttpResponse;
import model.User;
import org.junit.Before;
import org.junit.Test;
import service.UserService;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yunheekim on 2017. 5. 29..
 */
public class ResponseHandlerTest {

    final static String LINE_SEPARATOR = "\r\n";

    ResponseHandler responseHandler = new ResponseHandler();

    UserService userService;

    @Before
    public void setup() throws IOException {

        userService = new UserService();
    }

    @Test
    public void forwardCss() throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();

        byte[] body = responseHandler.getResponseBody("/css/bootstrap.min.css");

        String header = "HTTP/1.1 200 OK" + LINE_SEPARATOR +
                "Content-Length: " + body.length + LINE_SEPARATOR +
                "Content-Type: text/css;charset=utf-8" + LINE_SEPARATOR +
                LINE_SEPARATOR;

        responseHandler.forwardCss(new HttpResponse(outputStream), "/css/bootstrap.min.css");

        assertThat(header + new String(body), is(outputStream.toString()));

    }

    @Test
    public void forward() throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();

        byte[] body = responseHandler.getResponseBody("/");

        String header = "HTTP/1.1 200 OK" + LINE_SEPARATOR +
                "Content-Length: " + body.length + LINE_SEPARATOR +
                "Content-Type: text/html;charset=utf-8" + LINE_SEPARATOR +
                LINE_SEPARATOR;

        responseHandler.forward(new HttpResponse(outputStream), "/");

        assertThat(header + new String(body), is(outputStream.toString()));


        outputStream = new ByteArrayOutputStream();

        userService.create(new User("yhkim", "1234", "김윤희", "yunkim28@gmail.com"));

        Map<String, String> mapper = new HashMap<>();
        mapper.put("{{list}}", userService.list());

        body = responseHandler.getResponseBody("/user/list.html", mapper);

        header = "HTTP/1.1 200 OK" + LINE_SEPARATOR +
                "Content-Length: " + body.length + LINE_SEPARATOR +
                "Content-Type: text/html;charset=utf-8" + LINE_SEPARATOR +
                LINE_SEPARATOR;

        responseHandler.forward(new HttpResponse(outputStream), "/user/list.html", mapper);

        assertThat(header + new String(body), is(outputStream.toString()));
    }

    @Test
    public void sendRedirect() throws IOException {
        OutputStream outputStream = new ByteArrayOutputStream();

        byte[] body = responseHandler.getResponseBody("/");

        String header = "HTTP/1.1 302 Found" + LINE_SEPARATOR +
                "Location: /" + LINE_SEPARATOR +
                LINE_SEPARATOR;

        responseHandler.sendRedirect(new HttpResponse(outputStream), "/");

        assertThat(header, is(outputStream.toString()));
    }
}