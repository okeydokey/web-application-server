package controller;

import model.HttpRequest;
import model.HttpResponse;

/**
 * Created by yunheekim on 2017. 5. 29..
 */
public interface Controller {
    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
