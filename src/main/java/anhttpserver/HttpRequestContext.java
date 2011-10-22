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
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates {@link HttpExchange}
 * to allow access only for it's request related methods
 *
 * @author Sergey Prilukin
 */
public final class HttpRequestContext {
    private HttpExchange httpExchange;
    private byte[] requestBody;

    /**
     * Create wrapper from passed {@code httpExcahnge} param
     * @param httpExcahnge instance if {@link HttpExchange} for current request
     */
    public HttpRequestContext(HttpExchange httpExcahnge) {
        this.httpExchange = httpExcahnge;
        try {
            this.requestBody = IOUtils.toByteArray(httpExcahnge.getRequestBody());
        } catch (IOException e) {
            /* ignore */
        }
    }

    /**
     * Return request attribute for specified key
     *
     * @param key key
     * @return attribute value for specified key
     */
    public Object getAttribute(String key) {
        return httpExchange.getAttribute(key);
    }

    /**
     * Return all request headers.
     *
     * @return all request headers
     */
    public Map<String, List<String>> getRequestHeaders() {
        return httpExchange.getRequestHeaders();
    }

    /**
     * Return HTTP method of the request,
     * for example "GET", "POST", etc.
     *
     * @return string which represents HTTP request method
     */
    public String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    /**
     * Return byte array of request body.
     *
     * @return byte array of request body
     */
    public byte[] getRequestBody() {
        return requestBody;
    }

    /**
     * Return {@link URI} of request
     * @return {@link URI} of request
     */
    public URI getRequestURI() {
        return httpExchange.getRequestURI();
    }
}
