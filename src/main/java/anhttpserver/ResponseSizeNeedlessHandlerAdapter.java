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

/**
 * Implementation of {@link anhttpserver.SimpleHttpHandler}, which
 * calculates response size based on {@link java.io.InputStream#available()} method,
 * which may be not always correct.
 * 
 * So use this adapter only if you sure that your response's
 * available method returns total amount of data which could be read from result.
 *
 * @author Sergey Prilukin
 */
public abstract class ResponseSizeNeedlessHandlerAdapter extends SimpleHttpHandlerAdapter {

    /**
     * Implementations should override this method instead of {@link #getResponse(anhttpserver.HttpRequestContext)}.
     * Default implementation will set response size by calling available on this result and return it.
     *
     * @param httpRequestContext instance of {@link anhttpserver.HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return {@link InputStream} with resonse
     * @throws java.io.IOException if exception occurs
     */
    public abstract InputStream getResponseInternal(HttpRequestContext httpRequestContext) throws IOException;

    /**
     * {@inheritDoc}
     *
     * in this implementation just call {@link #getResponseInternal(anhttpserver.HttpRequestContext)},
     * sets result of calling available method on response as a response size and return that response.
     */
    public final InputStream getResponse(HttpRequestContext httpRequestContext) throws IOException {
        InputStream response = getResponseInternal(httpRequestContext);

        //Don't set response size if it was already set.
        if (getResponseSize(httpRequestContext) == 0) {
            setResponseSize(response != null ? response.available() : 0, httpRequestContext);
        }

        return response;
    }
}
