package controller;

import model.HttpRequest;
import model.HttpResponse;
import model.User;
import service.UserService;
import webserver.ResponseHandler;

/**
 * Created by yunheekim on 2017. 5. 29..
 */
public class CreateUserController extends AbstractController {

    UserService userService;
    ResponseHandler responseHandler;

    public CreateUserController(UserService userService, ResponseHandler responseHandler) {
        this.userService = userService;
        this.responseHandler = responseHandler;
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        userService.create(new User(httpRequest.getParams()));

        responseHandler.sendRedirect(httpResponse, "/");
    }
}
