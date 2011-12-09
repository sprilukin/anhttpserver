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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link SimpleHttpHandler}, which
 * subclasses needs to return result as a byte array.
 *
 * @author Sergey Prilukin
 */
public abstract class ByteArrayHandlerAdapter extends SimpleHttpHandlerAdapter {

    /**
     * Implementations should override this method instead of {@link #getResponse(HttpRequestContext)}
     * It will be wrapped into InputStream in default implementation of {@link #getResponse(HttpRequestContext)}
     *
     * @param httpRequestContext instance of {@link HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return byte array with resonse
     * @throws IOException if exception occurs
     */
    public abstract byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException;

    /**
     * {@inheritDoc}
     *
     * in this implementation just call {@link #getResponseAsByteArray(HttpRequestContext)},
     * sets response size to size of result array and return ByteArrayInputStream as wrapper
     * over that byte array.
     */
    public final InputStream getResponse(HttpRequestContext httpRequestContext) throws IOException {
        byte[] responseAsByteArray = getResponseAsByteArray(httpRequestContext);

        //Don't set response size if it was already set.
        if (getResponseSize(httpRequestContext) == 0) {
            setResponseSize(responseAsByteArray != null ? responseAsByteArray.length : 0, httpRequestContext);
        }

        return new ByteArrayInputStream(responseAsByteArray);
    }
}
