/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package net.temerity.http.impl.client;

import java.net.URI;

import net.temerity.http.HttpRequest;
import net.temerity.http.HttpResponse;
import net.temerity.http.ProtocolException;
import net.temerity.http.annotation.Immutable;
import net.temerity.http.client.RedirectStrategy;
import net.temerity.http.client.methods.HttpGet;
import net.temerity.http.client.methods.HttpHead;
import net.temerity.http.client.methods.HttpUriRequest;
import net.temerity.http.protocol.HttpContext;

/**
 * @since 4.1
 */
@Immutable
@Deprecated
class DefaultRedirectStrategyAdaptor implements RedirectStrategy {

    private final net.temerity.http.client.RedirectHandler handler;

    @Deprecated
    public DefaultRedirectStrategyAdaptor(final net.temerity.http.client.RedirectHandler handler) {
        super();
        this.handler = handler;
    }

    public boolean isRedirected(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws ProtocolException {
        return this.handler.isRedirectRequested(response, context);
    }

    public HttpUriRequest getRedirect(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws ProtocolException {
        URI uri = this.handler.getLocationURI(response, context);
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase(HttpHead.METHOD_NAME)) {
            return new HttpHead(uri);
        } else {
            return new HttpGet(uri);
        }
    }

}
