package controller;

import model.HttpRequest;
import model.ResponseHeader;
import model.User;
import service.UserService;
import util.HttpRequestUtils;
import webserver.ResponseHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunheekim on 2017. 5. 28..
 */
public class UserController {
    UserService userService;
    ResponseHandler responseHandler;

    public UserController(UserService userService, ResponseHandler responseHandler) {
        this.userService = userService;
        this.responseHandler = responseHandler;
    }

    /**
     * User 생성
     * @param dos
     * @param httpRequest
     */
    public void create(DataOutputStream dos, HttpRequest httpRequest) {
        userService.create(new User(HttpRequestUtils.parseQueryString(httpRequest.getQueryString())));

        responseHandler.response(dos, new ResponseHeader()
                .setHttpStatus(302)
                .setLocation("/"));
    }

    /**
     * User 로그인
     * @param dos
     * @param httpRequest
     */
    public void login(DataOutputStream dos, HttpRequest httpRequest) {
        boolean loginYn = userService.login(new User(HttpRequestUtils.parseQueryString(httpRequest.getQueryString())));

        responseHandler.response(dos, new ResponseHeader()
                .setHttpStatus(200)
                .setContentType("text/html;charset=utf-8")
                .setSetCookie("logined=" + loginYn));
    }

    /**
     * User 리스트
     * @param dos
     * @param httpRequest
     * @throws IOException
     */
    public void list(DataOutputStream dos, HttpRequest httpRequest) throws IOException {
        if(!isLogined(httpRequest)) {
            responseHandler.goLoginPage(dos);
        } else {
            Map<String, String> mapper = new HashMap<>();
            mapper.put("{{list}}", userService.list());
            byte[] body = responseHandler.getResponseBody(httpRequest.getPath(), mapper);

            responseHandler.response(dos, new ResponseHeader()
                    .setHttpStatus(200)
                    .setContentType("text/html;charset=utf-8")
                    .setContentLength(body.length), body);
        }
    }



    private boolean isLogined(HttpRequest httpRequest) {
        return Boolean.parseBoolean(httpRequest.getCookie().get("logined"));
    }
}
