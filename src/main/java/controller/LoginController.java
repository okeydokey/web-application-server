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
 * Created by yunheekim on 2017. 5. 29..
 */
public class LoginController extends AbstractController {
    UserService userService;
    ResponseHandler responseHandler;

    public LoginController(UserService userService, ResponseHandler responseHandler) {
        this.userService = userService;
        this.responseHandler = responseHandler;
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        boolean loginYn = userService.login(new User(httpRequest.getParams()));

        if(!loginYn) {
            responseHandler.forward(httpResponse, "/user/login_failed.html");
        }

        responseHandler.response(httpResponse
                .setHttpStatus(200)
                .setContentType("text/html;charset=utf-8")
                .setSetCookie("logined=" + loginYn));
    }
}
