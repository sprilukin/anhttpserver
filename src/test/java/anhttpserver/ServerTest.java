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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Very basic tests for {@link DefaultHttpServer}
 *
 * @author Sergey Prilukin
 */
public class ServerTest {

    private HttpServer server;

    @Before
    public void init() {
        server = new DefaultHttpServer();
        server.setHost("0.0.0.0");
        server.setPort(9999);
        server.setMaxThreads(3);
        server.start();
    }

    @After
    public void finish() {
        server.stop();
    }

    private String getResult(URLConnection connection) throws Exception {
        InputStream is = connection.getInputStream();
        String result = IOUtils.toString(is);
        is.close();
        return result;
    }

    private String getResult(String urlString) throws Exception {
        return getResult(getConnection(urlString));
    }

    private URLConnection getConnection(String urlString) throws Exception {
        return (new URL(urlString)).openConnection();
    }

    @Test
    public void basicServerTest() throws Exception {

        server.addHandler("/", new ByteArrayHandlerAdapter() {
            @Override
            public byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
                String response = "Hello world!";
                return response.getBytes();
            }
        });

        assertEquals("Hello world!", getResult("http://localhost:9999"));
    }

    @Test
    public void autoResponseSizeHandlerAdapterTest() throws Exception {

        server.addHandler("/", new AutoResponseSizeHandlerAdapter() {
            @Override
            public InputStream getResponseInternal(HttpRequestContext httpRequestContext) throws IOException {
                String response = "Hello world!";
                return new ByteArrayInputStream(response.getBytes());
            }
        });

        assertEquals("Hello world!", getResult("http://localhost:9999"));
    }

    @Test
    public void stringHandlerAdapterTest() throws Exception {

        server.addHandler("/", new StringHandlerAdapter() {
            @Override
            public String getResponseAsString(HttpRequestContext httpRequestContext) throws IOException {
                return "Hello world!";
            }
        });

        assertEquals("Hello world!", getResult("http://localhost:9999"));
    }

    @Test(expected = java.net.SocketException.class)
    public void testNullForStringHandlerAdapterTest() throws Exception {

        server.addHandler("/", new StringHandlerAdapter() {
            @Override
            public String getResponseAsString(HttpRequestContext httpRequestContext) throws IOException {
                return null;
            }
        });

        assertEquals(null, getResult("http://localhost:9999"));
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void testHandlerNotFound() throws Exception {
        server.addHandler("/index1", new ByteArrayHandlerAdapter() {
            @Override
            public byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
                return httpRequestContext.getRequestURI().getPath().getBytes();
            }
        });

        getResult("http://localhost:9999/index");
    }

    @Test
    public void multiThreadContextIsolationTest() throws Exception {
        final String res1 = "1111111111";
        final String res2 = "1";
        final String TEST_HEADER = "TEST-HEADER";

        final AtomicBoolean testPassed = new AtomicBoolean(true);

        class ThreadTestHttpHandlerAdapter extends ByteArrayHandlerAdapter {
            private String result;
            private int timeToSleep;

            ThreadTestHttpHandlerAdapter(String result, int timeToSleep) {
                this.result = result;
                this.timeToSleep = timeToSleep;
            }

            @Override
            public byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
                setResponseHeader(TEST_HEADER, result, httpRequestContext);

                try {
                    Thread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                return result.getBytes();
            }
        }

        class ThreadTester implements Runnable {
            private String url;
            private String result;
            private int repeatCount;

            ThreadTester(String url, String result, int repeatCount) {
                this.url = url;
                this.result = result;
                this.repeatCount = repeatCount;
            }

            public void run() {
                try {
                    for (int i = 0; i < repeatCount; i++) {
                        if (!testPassed.get()) {
                            return;
                        }

                        URLConnection connection = getConnection(url);
                        String headerValue = connection.getHeaderField(TEST_HEADER);
                        if (!headerValue.equals(result)) {
                            testPassed.set(false);
                            return;
                        }


                        if (!getResult(connection).equals(result)) {
                            testPassed.set(false);
                            return;
                        }
                    }
                } catch (Exception e) {
                    testPassed.set(false);
                }
            }
        }

        server.addHandler("/thread1", new ThreadTestHttpHandlerAdapter(res1, 0));
        server.addHandler("/thread2", new ThreadTestHttpHandlerAdapter(res2, 1));


        int count = 50;
        Thread[] threads = new Thread[count];

        String url1 = "http://localhost:9999/thread1";
        String url2 = "http://localhost:9999/thread2";

        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(new ThreadTester(i % 2 == 0 ? url1 : url2, i % 2 == 0 ? res1 : res2, 20));
            threads[i].start();
        }

        for (int i = 0; i < count; i++) {
            threads[i].join();
        }

        assertTrue(testPassed.get());
    }

    @Test
    public void testCascadingPath() throws Exception {
        server.addHandler("/", new ByteArrayHandlerAdapter() {
            @Override
            public byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
                return httpRequestContext.getRequestURI().getPath().getBytes();
            }
        });

        server.addHandler("/path1", new ByteArrayHandlerAdapter() {
            @Override
            public byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
                return "path1".getBytes();
            }
        });

        server.addHandler("/path1/path2", new ByteArrayHandlerAdapter() {
            @Override
            public byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
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
