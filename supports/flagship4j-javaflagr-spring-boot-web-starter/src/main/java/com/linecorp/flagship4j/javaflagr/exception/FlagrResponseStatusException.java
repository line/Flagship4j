/*
 * Copyright 2024 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.flagship4j.javaflagr.exception;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.linecorp.flagship4j.javaflagr.model.enums.HttpResponseStatus;

import lombok.Getter;

public class FlagrResponseStatusException extends FlagrException {

    @Getter
    private final int status;

    public FlagrResponseStatusException(HttpResponseStatus status) {
        this(status, (String)null);
    }

    public FlagrResponseStatusException(HttpResponseStatus status, @Nullable String message) {
        super(message);
        Assert.notNull(status, "HttpResponseStatus is required");
        this.status = status.getStatusCode();
    }
}
