package model;

/**
 * Created by yunheekim on 2017. 5. 23..
 */
public class HttpRequestUri {
    String path;
    String queryString;

    private HttpRequestUri() {

    }

    public static HttpRequestUri create(String requestURI) {
        HttpRequestUri httpRequestUri = new HttpRequestUri();

        int index = requestURI.indexOf("?");
        boolean hasQueryString = index != -1;

        httpRequestUri.path = hasQueryString ? requestURI.substring(0, index) : requestURI;
        httpRequestUri.queryString = hasQueryString ? requestURI.substring(index + 1) : "";

        return httpRequestUri;

    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
