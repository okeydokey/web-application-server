package util;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    /**
     * BufferedReader를 String으로 변환
     * @param bs
     *              String으로 변환할 BufferedReader
     * @return
     * @throws IOException
     */
    public static String bufferedReaderToString(BufferedReader bs) throws IOException {
        String request = "";
        String line;

        while((line = bs.readLine()) != null && !line.equals("")) {
            request += line;
        }

        return request;
    }
}
