package controller;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

/**
 * Created by yunheekim on 2017. 5. 29..
 */
public abstract class AbstractController implements Controller{

    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if(httpRequest.getMethod().equals("GET")) {
            doGet(httpRequest, httpResponse);
        } else if(httpRequest.getMethod().equals("POST")) {
            doPost(httpRequest, httpResponse);
        }
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
    }
}
