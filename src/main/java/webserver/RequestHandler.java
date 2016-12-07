package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            BufferedReader bs = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            byte[] body = getResponseBody(bs);

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private byte[] getResponseBody(BufferedReader bs) throws IOException {
        String request = getRequestString(bs);

        String requestUri = request.split(" ")[1];
        String[] requestSplit = requestUri.split("[?]");
        String requestParam = requestSplit.length > 0 ? requestSplit[1] : "";
        requestUri = requestSplit[0];

        User user = new User(HttpRequestUtils.parseQueryString(requestParam));
        System.out.println(user);

        requestUri = requestUri.equals("/") ? "/index.html" : requestUri;
        return Files.readAllBytes(new File("./webapp" + requestUri).toPath());
    }

    private String getRequestString(BufferedReader bs) throws IOException {
        String request = "";
        String line;

        while((line = bs.readLine()) != null && !line.equals("")) {
            request += line;
        }
        System.out.println(request);
        return request;
    }
}
