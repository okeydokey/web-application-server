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
public class ListUserController extends AbstractController {

    UserService userService;
    ResponseHandler responseHandler;

    public ListUserController(UserService userService, ResponseHandler responseHandler) {
        this.userService = userService;
        this.responseHandler = responseHandler;
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
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
