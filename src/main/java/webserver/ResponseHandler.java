package webserver;

import model.HttpRequest;
import model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/**
 * Created by yunheekim on 2017. 5. 28..
 */
public class ResponseHandler {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    /**
     * css 응답
     * @param httpResponse
     * @param requestPath
     * @throws IOException
     */
    public void forwardCss(HttpResponse httpResponse, String requestPath) throws IOException {
        byte[] body = getResponseBody(requestPath);

        forward(httpResponse, body, "text/css;charset=utf-8");
    }

    public void forward(HttpResponse httpResponse, String requestPath) throws IOException {
        byte[] body = getResponseBody(requestPath);

        forward(httpResponse, body, "text/html;charset=utf-8");
    }

    public void forward(HttpResponse httpResponse, String requestPath, Map<String, String> mapper) throws IOException {
        byte[] body = getResponseBody(requestPath, mapper);

        forward(httpResponse, body, "text/html;charset=utf-8");
    }

    private void forward(HttpResponse httpResponse, byte[] body, String contentType) {
        response(httpResponse
                .setHttpStatus(200)
                .setContentType(contentType)
                .setContentLength(body.length), body);
    }

    public void response(HttpResponse httpResponse) {
        response(httpResponse, null);
    }

    public void response(HttpResponse httpResponse, byte[] body) {
        responseHeader(httpResponse);

        if(body != null) responseBody(httpResponse, body);
    }

    private void responseHeader(HttpResponse httpResponse) {
        try {
            httpResponse.getOutputStream().write(httpResponse.getResponseHeader(), 0, (httpResponse.getResponseHeader()).length);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(HttpResponse httpResponse, byte[] body) {
        try {
            httpResponse.getOutputStream().write(body, 0, body.length);
            httpResponse.getOutputStream().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    protected byte[] getResponseBody(String path) throws IOException {
        return getResponseBody(path, null);
    }

    protected byte[] getResponseBody(String path, Map<String, String> mapper) throws IOException {
        String requestPath = path.equals("/") ? "/index.html" : path;

        byte[] result = Files.readAllBytes(new File("./webapp" + requestPath).toPath());

        if(hasMapper(mapper)) {
            result = mappingData(mapper, result);
        }

        return result;
    }

    private byte[] mappingData(Map<String, String> mapper, byte[] result) {
        String responseBody = new String(result, StandardCharsets.UTF_8);

        for(String key : mapper.keySet()) {
            responseBody = responseBody.replace(key, mapper.get(key));
        }

        result = responseBody.getBytes();
        return result;
    }

    public void sendRedirect(HttpResponse httpResponse, String location) {
        response(httpResponse
            .setHttpStatus(302)
            .setLocation(location));
    }

    private boolean hasMapper(Map<String, String> mapper) {
        return !(mapper == null || mapper.size() == 0);
    }
}
