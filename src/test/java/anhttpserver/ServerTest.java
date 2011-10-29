/*
 * Copyright (c) 2011 Sergey Prilukin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package anhttpserver;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Very basic tests for {@link DefaultSimpleHttpServer}
 *
 * @author Sergey Prilukin
 */
public class ServerTest {

    private SimpleHttpServer server;

    @Before
    public void init() {
        server = new DefaultSimpleHttpServer();
        server.setHost("0.0.0.0");
        server.setPort(9999);
        server.setMaxThreads(3);
        server.start();
    }

    @After
    public void finish() {
        server.stop();
    }

    private String getResult(String urlString) throws Exception {
        InputStream is = (new URL(urlString)).openStream();
        String result = IOUtils.toString(is);
        is.close();
        return result;
    }

    @Test
    public void basicServerTest() throws Exception {

        server.addHandler("/", new SimpleHttpHandlerAdapter() {
            @Override
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                String response = "Hello world!";
                return response.getBytes();
            }
        });

        assertEquals("Hello world!", getResult("http://localhost:9999"));
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void testHandlerNotFound() throws Exception {
        server.addHandler("/index1", new SimpleHttpHandlerAdapter() {
            @Override
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                return httpRequestContext.getRequestURI().getPath().getBytes();
            }
        });

        getResult("http://localhost:9999/index");
    }

    @Test
    public void testCascadingPath() throws Exception {
        server.addHandler("/", new SimpleHttpHandlerAdapter() {
            @Override
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                return httpRequestContext.getRequestURI().getPath().getBytes();
            }
        });

        server.addHandler("/path1", new SimpleHttpHandlerAdapter() {
            @Override
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                return "path1".getBytes();
            }
        });

        server.addHandler("/path1/path2", new SimpleHttpHandlerAdapter() {
            @Override
            public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
                return "path2".getBytes();
            }
        });

        assertEquals("/", getResult("http://localhost:9999/"));
        assertEquals("/", getResult("http://localhost:9999"));
        assertEquals("/index", getResult("http://localhost:9999/index"));
        assertEquals("/index/test", getResult("http://localhost:9999/index/test"));
        assertEquals("path1", getResult("http://localhost:9999/path1"));
        assertEquals("path1", getResult("http://localhost:9999/path1/path3"));
        assertEquals("path2", getResult("http://localhost:9999/path1/path2"));
        assertEquals("path2", getResult("http://localhost:9999/path1/path2/path3"));
    }
}
