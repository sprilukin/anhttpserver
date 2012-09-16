/*
 * Copyright (c) 2012 Sergey Prilukin
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

/**
 * Implementation of {@link HttpHandler}, which
 * subclasses needs to return result as a string.
 *
 * @author Sergey Prilukin
 */
public abstract class StringHandlerAdapter extends ByteArrayHandlerAdapter {

    /**
     * Implementations should override this method instead of {@link #getResponse(anhttpserver.HttpRequestContext)}
     *
     * @param httpRequestContext instance of {@link anhttpserver.HttpRequestContext} -
     *  facade for {@link com.sun.net.httpserver.HttpExchange}
     * @return string with resonse
     * @throws java.io.IOException if exception occurs
     */
    public abstract String getResponseAsString(HttpRequestContext httpRequestContext) throws IOException;

    /**
     * {@inheritDoc}
     *
     * in this implementation just call {@link #getResponseAsString(anhttpserver.HttpRequestContext)},
     * and return bytes of result string.
     */
    public final byte[] getResponseAsByteArray(HttpRequestContext httpRequestContext) throws IOException {
        String responseAsString = getResponseAsString(httpRequestContext);
        return responseAsString != null ? responseAsString.getBytes() : null;
    }
}
