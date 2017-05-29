package webserver;

import java.io.*;
import java.net.Socket;

import controller.UserController;
import model.HttpRequest;
import model.HttpResponse;
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

            HttpResponse httpResponse = new HttpResponse(dos);
            if(isCss(httpRequest)) {
                responseHandler.forwardCss(httpResponse, httpRequest.getPath());
            } else {
                requestMapping(httpRequest, httpResponse);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 요청 URL에 따라 Controller 매핑
     * @param httpRequest
     * @param httpResponse
     * @throws IOException
     */
    protected void requestMapping(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        String requestPath = httpRequest.getPath();

        if(requestPath.equals("/user/create")) {

            userController.create(httpRequest, httpResponse);

        } else if(requestPath.equals("/user/login")) {

            userController.login(httpRequest, httpResponse);

        } else if(requestPath.equals("/user/list.html")) {

            userController.list(httpRequest, httpResponse);

        } else {

            responseHandler.forward(httpResponse, requestPath);

        }
    }

    private boolean isCss(HttpRequest httpRequest) {
        return httpRequest.getHeader("Accept").indexOf("text/css") != -1;
    }


}
