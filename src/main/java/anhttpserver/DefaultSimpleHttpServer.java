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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link SimpleHttpServer}
 * Which has predefined host and port.
 *
 * {@link com.sun.net.httpserver.HttpServer} is used
 * at the backend.
 *
 * @author Sergey Prilukin
 */
public final class DefaultSimpleHttpServer implements SimpleHttpServer {

    public static final String HTTP_HEAD = "HEAD";
    public static final String SERVER_HEADER_NAME = "Server";
    public static final String SERVER_NAME = "SimpleHttpServer";
    public static final String SERVER_VERSION = "0.2";
    public static final String FULL_SERVER_NAME = SERVER_NAME + "/" + SERVER_VERSION;
    public static final int DEFAULT_PORT = 8000;
    public static final String DEFAULT_HOST = "localhost";
    public static final String HTTP_PREFIX = "http://";
    public static final String PORT_DELIMITER = ":";

    private HttpServer httpServer;
    private int port = DEFAULT_PORT;
    private Map<String, SimpleHttpHandler> handlers = new HashMap<String, SimpleHttpHandler>();
    private Map<String, String> defaultHeaders = new HashMap<String, String>();

    private final Object handlersMonitor = new Object();
    private final Object headersMonitor = new Object();

    private HttpHandler defaultHandler = new HttpHandler() {

        private void logRequest(HttpExchange httpExchange) {
            //request URI
            System.out.println("=== SIMPLE-HTTP-SERVER REQUEST URI: " + httpExchange.getRequestURI().toString());

            //request headers
            System.out.println("=== SIMPLE-HTTP-SERVER REQUEST HEADERS: ");
            for (Map.Entry<String, List<String>> entry : httpExchange.getRequestHeaders().entrySet()) {
                for (String value: entry.getValue()) {
                    System.out.println(String.format("==== [%s: %s]", entry.getKey(), value));
                }
            }

            //request method
            System.out.println("=== SIMPLE-HTTP-SERVER REQUEST METHOD: " + httpExchange.getRequestMethod());
        }

        private void internalHandleRequest(SimpleHttpHandler handler, HttpExchange httpExchange) throws IOException {
            try {
                HttpRequestContext httpRequestContext = new HttpRequestContext(httpExchange);

                //Call getReponse of passed handler
                byte[] response = handler.getResponse(httpRequestContext);

                //Add default headers
                for (Map.Entry<String, String> entry: defaultHeaders.entrySet()) {
                    httpExchange.getResponseHeaders().add(entry.getKey(), entry.getValue());
                }

                //Add headers from handler
                if (handler.getResponseHeaders() != null && handler.getResponseHeaders().size() > 0) {
                    for (Map.Entry<String, String> entry: handler.getResponseHeaders().entrySet()) {
                        httpExchange.getResponseHeaders().add(entry.getKey(), entry.getValue());
                    }
                }


                httpExchange.sendResponseHeaders(handler.getResponseCode(httpRequestContext), response != null ? response.length : 0);

                if (response != null && response.length > 0
                        && !HTTP_HEAD.equals(httpExchange.getRequestMethod())) {
                    httpExchange.getResponseBody().write(response);
                }
            } finally {
                httpExchange.close();
            }
        }

        public void handle(HttpExchange httpExchange) throws IOException {
            logRequest(httpExchange);

            String path = httpExchange.getRequestURI().getPath();
            synchronized (handlersMonitor) {
                if (handlers.containsKey(path)) {
                    internalHandleRequest(handlers.get(path), httpExchange);
                } else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    httpExchange.close();
                }
            }
        }
    };

    public DefaultSimpleHttpServer() {
        defaultHeaders.put(SERVER_HEADER_NAME, FULL_SERVER_NAME);
    }

    private void createHttpServer() {
        if (httpServer == null) {
            synchronized (this) {
                if (httpServer == null) {
                    try {
                        httpServer = HttpServer.create();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    boolean bound = false;

                    while (!bound) {
                        try {
                            httpServer.bind(new InetSocketAddress(DEFAULT_HOST, port), 0);
                            bound = true;
                            System.out.println(String.format("=== SIMPLE-HTTP-SERVER ACCEPT CONNECTIONS ON PORT: %s", port));
                        } catch (BindException e) {
                            port++;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public String getBaseUrl() {
        return (new StringBuilder()).append(HTTP_PREFIX).append(DEFAULT_HOST).append(PORT_DELIMITER).append(port).toString();
    }

    public void start() {
        createHttpServer();
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void addHandler(String path, SimpleHttpHandler httpHandler) {
        createHttpServer();

        synchronized (handlersMonitor) {
            handlers.put(path, httpHandler);
        }

        httpServer.createContext(path, defaultHandler);
    }

    public void setDefaultResponseHeaders(Map<String, String> defaultHeaders) {
        synchronized (headersMonitor) {
            this.defaultHeaders.putAll(defaultHeaders);
        }
    }

    public void addResponseHeader(String name, String value) {
        synchronized (headersMonitor) {
            this.defaultHeaders.put(name, value);
        }
    }
}
