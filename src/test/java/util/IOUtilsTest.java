package util;

import java.io.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void bufferedReaderToString() throws IOException {
        String data = "GET /index.html HTTP/1.1";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        String result = IOUtils.bufferedReaderToString(br);
        assertThat(result, is(data));
    }

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }
}
