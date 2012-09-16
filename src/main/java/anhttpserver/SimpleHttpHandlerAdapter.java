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
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter which implements most methods of {@link HttpHandler}
 * So descendants should only implement method {@link HttpHandler#getResponse(HttpRequestContext)}
 *
 * @author Sergey Prilukin
 */
public abstract class SimpleHttpHandlerAdapter implements HttpHandler {

    /**
     * Returns thread-specific request key to put something into
     * HTTP request attributes since they're not thread safe
     *
     * @param key not thread-specific attribute key
     * @return
     */
    private String getThreadSpecificAttributeKey(String key) {
        String threadSpecificSuffix = String.valueOf(Thread.currentThread().getName().hashCode());
        return (new StringBuilder(key)).append(threadSpecificSuffix).toString();
    }
    /**
     * Retreive map with response headers from contextAttributes
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return map where hndler can write response headers
     */
    protected Map<String, String> getResponseHeadersFromContext(HttpRequestContext httpRequestContext) {
        String responseHeadersAttributeKey = getThreadSpecificAttributeKey(RESPONSE_HEADERS_ATTRIBUTE_KEY);
        Map<String, String> headers = (Map<String, String>)httpRequestContext.getAttribute(responseHeadersAttributeKey);
            if (headers == null) {
                headers = new HashMap<String, String>();
                httpRequestContext.setAttribute(responseHeadersAttributeKey, headers);
            }

        return headers;
    }

    /**
     * Utility method which allows several response headers in one call.
     *
     * @param headers response headers which will be sent with response
     */
    protected void setResponseHeaders(Map<String, String> headers, HttpRequestContext httpRequestContext) {
        if (headers != null && headers.size() > 0) {
            (getResponseHeadersFromContext(httpRequestContext)).putAll(headers);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getResponseHeaders(HttpRequestContext httpRequestContext) {
        return Collections.unmodifiableMap(getResponseHeadersFromContext(httpRequestContext));
    }

    /**
     * {@inheritDoc}
     */
    public void setResponseHeader(String name, String value, HttpRequestContext httpRequestContext) {
        getResponseHeadersFromContext(httpRequestContext).put(name, value);
    }

    /**
     * {@inheritDoc}
     *
     * If not overridden - returns status 200
     * which means HTTP OK
     */
    public int getResponseCode(HttpRequestContext httpRequestContext) {
        String responseCodeAttributeKey = getThreadSpecificAttributeKey(RESPONSE_CODE_ATTRIBUTE_KEY);
        Object size = httpRequestContext.getAttribute(responseCodeAttributeKey);
        if (size != null) {
            return (Integer)size;
        }

        return HttpURLConnection.HTTP_OK;
    }

    /**
     * Sets response code
     *
     * @param code code of the response {@see HttpURLConnection}
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     */
    protected void setResponseCode(int code, HttpRequestContext httpRequestContext) {
        String responseCodeAttributeKey = getThreadSpecificAttributeKey(RESPONSE_CODE_ATTRIBUTE_KEY);
        httpRequestContext.setAttribute(responseCodeAttributeKey, code);
    }

    /**
     * Sets size of the reposnse
     *
     * @param size size of the response
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     */
    protected void setResponseSize(long size, HttpRequestContext httpRequestContext) {
        String responseCodeAttributeKey = getThreadSpecificAttributeKey(RESPONSE_SIZE_ATTRIBUTE_KEY);
        httpRequestContext.setAttribute(responseCodeAttributeKey, size);
    }

    /**
     * By default uses {@link #RESPONSE_SIZE_ATTRIBUTE_KEY} request attribute
     * to determine size, returns zero if such attribute doesn't exists
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return response size
     */
    public long getResponseSize(HttpRequestContext httpRequestContext) {
        String responseCodeAttributeKey = getThreadSpecificAttributeKey(RESPONSE_SIZE_ATTRIBUTE_KEY);
        Object size = httpRequestContext.getAttribute(responseCodeAttributeKey);
        if (size != null) {
            return (Long)size;
        } else {
            return 0;
        }
    }

    @Override
    public void cleanContext(HttpRequestContext httpRequestContext) {
        httpRequestContext.setAttribute(getThreadSpecificAttributeKey(RESPONSE_HEADERS_ATTRIBUTE_KEY), null);
        httpRequestContext.setAttribute(getThreadSpecificAttributeKey(RESPONSE_CODE_ATTRIBUTE_KEY), null);
        httpRequestContext.setAttribute(getThreadSpecificAttributeKey(RESPONSE_SIZE_ATTRIBUTE_KEY), null);
    }

    /**
     * {@inheritDoc}
     */
    public abstract InputStream getResponse(HttpRequestContext httpRequestContext) throws IOException;
}
