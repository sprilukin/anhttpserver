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

import java.util.Map;

/**
 * <p>
 *     Interface for embedded HTTP Server
 *     based on Sun Java 6 HTTP Server.
 * </p>
 *
 * <p>
 *     Useful for functional testing where we need to emulate a web server
 * </p>
 *
 *
 *
 * @author Sergey Prilukin
 */
public interface SimpleHttpServer {

    /**
     * Start HTTP server
     */
    public void start();

    /**
     * Stop HTTP server
     */
    public void stop();

    /**
     * <p>Set port which HTTP server will listen.
     * Default is {@code 8000}.</p>
     *
     * <p>Will take effect only BEFORE first call to
     * {@link #start()} or {@link #addHandler(String, SimpleHttpHandler)}</p>
     *
     * @param port tcp port number
     */
    public void setPort(int port);


    /**
     * Return current port.
     * {@code 8000} by default.
     *
     * @return current port
     */
    public int getPort();

    /**
     * Return current host.
     *
     * @return current host
     */
    public String getHost();

    /**
     * Set host on which HTTP Server will listen.
     * Default is {@code localhost}
     *
     * <p>Will take effect only BEFORE first call to
     * {@link #start()} or {@link #addHandler(String, SimpleHttpHandler)}</p>
     *
     * @param host host to set
     */
    public void setHost(String host);

    /**
     * Return current value of maximum threads count.
     *
     * @return max threads count
     */
    public int getMaxThreads();

    /**
     * Set max threads count for server
     * Default is {@code 1}
     *
     * <p>Will take effect only BEFORE first call to
     * {@link #start()} or {@link #addHandler(String, SimpleHttpHandler)}</p>
     *
     * @param maxThreads max count of threads
     */
    public void setMaxThreads(int maxThreads);

    /**
     * For given {@code path} set instance of {@link SimpleHttpHandler}
     * which will handle all requests for given path.
     *
     *
     * @param path path for which handler will be set.
     *  for example if your server is on follwing URL:<br />
     *  <code>http://localhost:8000</code><br />
     *  and you want to handle all requests for http://localhost:8000/index
     *  you should call
     *      <code>addHandler("/index", httpHandler)</code>
     *
     * @param httpHandler instance of {@link SimpleHttpHandler} which will handle
     * all requests for given path
     */
    public void addHandler(String path, SimpleHttpHandler httpHandler);

    /**
     * Set response headers which will always be sent for all requests.
     * Example of such header is a {@code Server} header which will be sent
     * to client with every response
     *
     * @param defaultHeaders map where key is name of a response and value is a value
     * of a response
     */
    public void setDefaultResponseHeaders(Map<String, String> defaultHeaders);

    /**
     * Add single HTTP header entry which will be sent with every response
     * Similar to {@link #setDefaultResponseHeaders(java.util.Map)}
     * but adds only one header
     *
     * @param name name of the header
     * @param value value of the header
     */
    public void addResponseHeader(String name, String value);

    /**
     * Return current base url.
     * By default return follwing (if port was not changed): <br />
     * <code>http://localhost:8000</code>
     *
     * @return current base url
     */
    public String getBaseUrl();
}
