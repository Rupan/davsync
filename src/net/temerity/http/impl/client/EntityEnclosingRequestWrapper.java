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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.temerity.http.annotation.NotThreadSafe;
import net.temerity.http.entity.HttpEntityWrapper;

import net.temerity.http.Header;
import net.temerity.http.HttpEntity;
import net.temerity.http.HttpEntityEnclosingRequest;
import net.temerity.http.ProtocolException;
import net.temerity.http.protocol.HTTP;

/**
 * A wrapper class for {@link HttpEntityEnclosingRequest}s that can
 * be used to change properties of the current request without
 * modifying the original object.
 * </p>
 * This class is also capable of resetting the request headers to
 * the state of the original request.
 *
 *
 * @since 4.0
 */
@NotThreadSafe // e.g. [gs]etEntity()
public class EntityEnclosingRequestWrapper extends RequestWrapper
    implements HttpEntityEnclosingRequest {

    private HttpEntity entity;
    private boolean consumed;

    public EntityEnclosingRequestWrapper(final HttpEntityEnclosingRequest request)
        throws ProtocolException {
        super(request);
        setEntity(request.getEntity());
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public void setEntity(final HttpEntity entity) {
        this.entity = entity != null ? new EntityWrapper(entity) : null;
        this.consumed = false;
    }

    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }

    @Override
    public boolean isRepeatable() {
        return this.entity == null || this.entity.isRepeatable() || !this.consumed;
    }

    class EntityWrapper extends HttpEntityWrapper {

        EntityWrapper(final HttpEntity entity) {
            super(entity);
        }

        @Deprecated
        @Override
        public void consumeContent() throws IOException {
            consumed = true;
            super.consumeContent();
        }

        @Override
        public InputStream getContent() throws IOException {
            consumed = true;
            return super.getContent();
        }

        @Override
        public void writeTo(final OutputStream outstream) throws IOException {
            consumed = true;
            super.writeTo(outstream);
        }

    }

}
