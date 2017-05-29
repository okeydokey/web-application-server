package controller;

import model.HttpRequest;
import model.HttpResponse;
import model.User;
import service.UserService;
import webserver.ResponseHandler;

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
     * @param httpRequest
     * @param httpResponse
     */
    public void create(HttpRequest httpRequest, HttpResponse httpResponse) {
        userService.create(new User(httpRequest.getParams()));

        responseHandler.sendRedirect(httpResponse, "/");
    }

    /**
     * User 로그인
     * @param httpRequest
     * @param httpResponse
     */
    public void login(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        boolean loginYn = userService.login(new User(httpRequest.getParams()));

        if(!loginYn) {
            responseHandler.forward(httpResponse, "/user/login_failed.html");
        }

        responseHandler.response(httpResponse
            .setHttpStatus(200)
            .setContentType("text/html;charset=utf-8")
            .setSetCookie("logined=" + loginYn));
    }

    /**
     * User 리스트
     * @param httpRequest
     * @param httpResponse
     * @throws IOException
     */
    public void list(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(!isLogin(httpRequest)) {
            responseHandler.sendRedirect(httpResponse, "/user/login.html");
        } else {
            Map<String, String> mapper = new HashMap<>();
            mapper.put("{{list}}", userService.list());

            responseHandler.forward(httpResponse, httpRequest.getPath(), mapper);
        }
    }



    private boolean isLogin(HttpRequest httpRequest) {
        return Boolean.parseBoolean(httpRequest.getCookie().get("logined"));
    }
}
