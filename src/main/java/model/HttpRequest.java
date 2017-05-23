package model;

/**
 * Created by yunheekim on 2017. 5. 23..
 */
public class HttpRequest {
    String path;
    String queryString;

    public HttpRequest(String requestURI) {
        int index = requestURI.indexOf("?");
        boolean hasQueryString = index != -1;
        this.path = hasQueryString ? requestURI.substring(0, index) : requestURI;
        this.queryString = hasQueryString ? requestURI.substring(index + 1) : "";
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
