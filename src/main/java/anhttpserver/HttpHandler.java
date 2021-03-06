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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>Class which encapsulates handler for HTTP request.</p>
 *
 * <p>
 *     Example of usage:<br/>
 *
 *     <pre>
 *         HttpServer server = new DefaultHttpServer();
 *         server.start();
 *
 *         server.addHandler("/index", new SimpleHttpHandlerAdapter() {
 *             public byte[] getResponse(HttpRequestContext httpRequestContext) throws IOException {
 *                 return "Hello world".getBytes();
 *             }
 *         });
 *     </pre>
 *
 *     <p>In this example we add handler for path "/index",
 *     so anytime client access {@code http://localhost:8000/index}
 *     "Hello world" will be sent as response.</p>
 *
 *     <p>
 *         All methods declared here will be used by implementations of
 *         HttpServer.
 *     </p>
 * </p>
 *
 * @author Sergey Prilukin
 */
public interface HttpHandler {

    /**
     * Key for which server will search to get response size if possible
     */
    public static final String RESPONSE_SIZE_ATTRIBUTE_KEY = "anhttpserver.response.size";

    /**
     * Key for which server will search to get response code if possible
     */
    public static final String RESPONSE_CODE_ATTRIBUTE_KEY = "anhttpserver.response.code";

    /**
     * Key for which server will search to get response headers if possible
     */
    public static final String RESPONSE_HEADERS_ATTRIBUTE_KEY = "anhttpserver.response.headers";

    /**
     * Return unmodifiable collection of response headers
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return collection of response headers.
     */
    public Map<String, String> getResponseHeaders(HttpRequestContext httpRequestContext);

    /**
     * Set a single response header.
     *
     *
     * @param name name of the header
     * @param value value of the header
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     */
    public void setResponseHeader(String name, String value, HttpRequestContext httpRequestContext);

    /**
     * Return response code.
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return response code
     * @see java.net.HttpURLConnection
     */
    public int getResponseCode(HttpRequestContext httpRequestContext);

    /**
     * Return response size.
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return response size
     */
    public long getResponseSize(HttpRequestContext httpRequestContext);

    /**
     * Somewhy in {@link com.sun.net.httpserver.HttpExchange} context attibutes
     * are not cleaned with each request, so need to clean them manually
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     */
    public void cleanContext(HttpRequestContext httpRequestContext);

    /**
     * Return {@link InputStream} with response.
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return {@link InputStream} with response
     * @throws IOException if exception occurs during getting response
     */
    public InputStream getResponse(HttpRequestContext httpRequestContext) throws IOException;
}
