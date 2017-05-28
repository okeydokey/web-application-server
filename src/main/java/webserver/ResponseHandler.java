package webserver;

import model.HttpRequest;
import model.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
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
     * @param dos
     * @param requestPath
     * @throws IOException
     */
    public void responseCss(DataOutputStream dos, String requestPath) throws IOException {
        byte[] body = getResponseBody(requestPath);

        response(dos, new ResponseHeader()
                .setHttpStatus(200)
                .setContentType("text/css;charset=utf-8")
                .setContentLength(body.length), body);
    }

    public void response(DataOutputStream dos, ResponseHeader responseHeader) {
        response(dos, responseHeader, null);
    }

    public void response(DataOutputStream dos, ResponseHeader responseHeader, byte[] body) {
        responseHeader(dos, responseHeader);

        if(body != null) responseBody(dos, body);
    }

    private void responseHeader(DataOutputStream dos, ResponseHeader responseHeader) {
        try {
            dos.writeBytes(responseHeader.getResponseHeader());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public byte[] getResponseBody(String path) throws IOException {
        return getResponseBody(path, null);
    }

    public byte[] getResponseBody(String path, Map<String, String> mapper) throws IOException {
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

    public void goLoginPage(DataOutputStream dos) {
        response(dos, new ResponseHeader()
            .setHttpStatus(302)
            .setLocation("/user/login.html"));
    }

    private boolean hasMapper(Map<String, String> mapper) {
        return !(mapper == null || mapper.size() == 0);
    }
}
