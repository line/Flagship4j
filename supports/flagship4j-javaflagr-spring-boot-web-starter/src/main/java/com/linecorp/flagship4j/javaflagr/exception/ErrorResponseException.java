/*
 * Copyright 2024 LY Corporation
 *
 * LY Corporation licenses this file to you under the Apache License,
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

import com.linecorp.flagship4j.javaflagr.model.ContextHolder;
import com.linecorp.flagship4j.javaflagr.model.enums.HttpResponseStatus;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class ErrorResponseException extends FlagrResponseStatusException {

    protected final String serviceId;

    protected final Instant timestamp;

    protected final List<ErrorDetail> details;

    public ErrorResponseException() {
        this(new ErrorDetail[] {});
    }

    public ErrorResponseException(Throwable cause) {
        this(cause, new ErrorDetail[] {});
    }

    public ErrorResponseException(ErrorDetail... details) {
        this(null, HttpResponseStatus.INTERNAL_SERVER_ERROR, details);
    }

    public ErrorResponseException(Throwable cause, ErrorDetail... details) {
        this(cause, HttpResponseStatus.INTERNAL_SERVER_ERROR, details);
    }

    public ErrorResponseException(HttpResponseStatus status, ErrorDetail... details) {
        this(null, status, details);
    }

    public ErrorResponseException(Throwable cause, HttpResponseStatus status, ErrorDetail... details) {
        super(status, status.getReasonPhrase());
        serviceId = getServiceId();
        timestamp = Instant.now();
        this.details = Arrays.asList(details);
        initCause(cause);
    }

    public ErrorResponseException(Throwable... throwables) {
        this(throwables.length > 0 ? throwables[0] : null, toErrorDetails(throwables));
    }

    public ErrorResponseException(HttpResponseStatus status, Throwable... throwables) {
        this(throwables.length > 0 ? throwables[0] : null, status, toErrorDetails(throwables));
    }

    protected static ErrorDetail[] toErrorDetails(Throwable[] throwables) {
        return Arrays.stream(throwables).map(ErrorDetail::unknown).toArray(ErrorDetail[]::new);
    }

    private String getServiceId() {
        try {
            return ContextHolder.getServiceId().get();
        } catch (Exception e) {
            return null;
        }
    }

    @Getter
    @Builder
    public static class ErrorDetail implements Serializable {

        public static ErrorDetail unknown(Throwable t) {
            return builder()
                    .code("unknown")
                    .message(t.getMessage())
                    .build();
        }

        private String code;

        private String message;

        @Builder.Default
        private List<String> reasons = Collections.emptyList();

    }

}
