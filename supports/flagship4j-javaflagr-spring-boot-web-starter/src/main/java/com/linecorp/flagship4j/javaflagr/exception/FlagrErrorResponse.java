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

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.linecorp.flagship4j.javaflagr.model.enums.HttpResponseStatus;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlagrErrorResponse {

    private int status;

    private String message;

    private Instant timestamp = Instant.now();

    public FlagrErrorResponse(FlagrResponseStatusException ex) {
        status = ex.getStatus();
        message = ex.getMessage();
    }

    public FlagrErrorResponse(FlagrException e) {
        status = HttpResponseStatus.INTERNAL_SERVER_ERROR.getStatusCode();
        message = e.getMessage();
    }
}
