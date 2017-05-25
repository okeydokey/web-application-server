package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import model.HttpRequest;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    UserService userService = new UserService();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }



    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            HttpRequest httpRequest = HttpRequest.create(br);

            log.debug("httpRequest : {} ", httpRequest.toString());

            String requestPath = httpRequest.getPath();

            DataOutputStream dos = new DataOutputStream(out);

            if(httpRequest.getAccept().indexOf("text/css") != -1) {

                byte[] body = getResponseBody(requestPath);

                response200HeaderCss(dos, body.length);
                responseBody(dos, body);

            } else if(requestPath.equals("/user/create")) {

                userService.create(new User(HttpRequestUtils.parseQueryString(httpRequest.getQueryString())));

                response302Header(dos, "/");

            } else if(requestPath.equals("/user/login")) {

                boolean loginYn = userService.login(new User(HttpRequestUtils.parseQueryString(httpRequest.getQueryString())));
                response200HeaderAddCookie(dos, "logined=" + loginYn);

            } else if(requestPath.equals("/user/list.html")) {

                if(!Boolean.parseBoolean(httpRequest.getCookie().get("logined"))) {
                    response302Header(dos, "/user/login.html");
                } else {
                    Map<String, String> mapper = new HashMap<>();
                    mapper.put("{{list}}", userService.list());
                    byte[] body = getResponseBody(requestPath, mapper);

                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }

            } else {

                byte[] body = getResponseBody(requestPath);

                response200Header(dos, body.length);
                responseBody(dos, body);

            }


        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    // TODO response Header 리팩토링 필요
    private void response200HeaderAddCookie(DataOutputStream dos, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getResponseBody(String path, Map<String, String> mapper) throws IOException {
        String requestPath = path.equals("/") ? "/index.html" : path;

        byte[] result = Files.readAllBytes(new File("./webapp" + requestPath).toPath());

        if(hasMapper(mapper)) {
            String responseBody = new String(result, StandardCharsets.UTF_8);

            log.debug("before mapper : {} ", responseBody);
            for(String key : mapper.keySet()) {
                responseBody = responseBody.replace(key, mapper.get(key));
            }

            log.debug("after mapper : {} ", responseBody);
            result = responseBody.getBytes();
        }

        return result;
    }

    private boolean hasMapper(Map<String, String> mapper) {
        return !(mapper == null || mapper.size() == 0);
    }

    private byte[] getResponseBody(String path) throws IOException {
        return getResponseBody(path, null);
    }
}
