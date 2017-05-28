package webserver;

import java.io.*;
import java.net.Socket;

import controller.UserController;
import model.HttpRequest;
import model.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    UserController userController = new UserController(new UserService(), new ResponseHandler());

    ResponseHandler responseHandler = new ResponseHandler();

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

            HttpRequest httpRequest = HttpRequest.from(br);

            log.debug("httpRequest : {} ", httpRequest.toString());

            DataOutputStream dos = new DataOutputStream(out);

            if(isCss(httpRequest)) {
                responseHandler.responseCss(dos, httpRequest.getPath());
            } else {
                requestMapping(dos, httpRequest);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 요청 URL에 따라 Controller 매핑
     * @param dos
     * @param httpRequest
     * @throws IOException
     */
    private void requestMapping(DataOutputStream dos, HttpRequest httpRequest) throws IOException {

        String requestPath = httpRequest.getPath();

        if(requestPath.equals("/user/from")) {

            userController.create(dos, httpRequest);

        } else if(requestPath.equals("/user/login")) {

            userController.login(dos, httpRequest);

        } else if(requestPath.equals("/user/list.html")) {

            userController.list(dos, httpRequest);

        } else {

            byte[] body = responseHandler.getResponseBody(requestPath);

            responseHandler.response(dos, new ResponseHeader()
                    .setHttpStatus(200)
                    .setContentType("text/html;charset=utf-8")
                    .setContentLength(body.length), body);

        }
    }

    private boolean isCss(HttpRequest httpRequest) {
        return httpRequest.getAccept().indexOf("text/css") != -1;
    }


}
