package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

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
        String request = IOUtils.bufferedReaderToString(bs);

        if(!request.isEmpty()) {
            HttpRequest httpRequest = new HttpRequest(request.split(" ")[1]);

            String requestPath = httpRequest.getPath();

            requestPath = requestPath.equals("/") ? "/index.html" : requestPath;
            return Files.readAllBytes(new File("./webapp" + requestPath).toPath());
        } else {
            // empty request
            log.debug("empty request!");
            return new byte[0];
        }

    }
}
