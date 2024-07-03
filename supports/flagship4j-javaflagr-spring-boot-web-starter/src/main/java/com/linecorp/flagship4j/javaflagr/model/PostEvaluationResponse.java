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
package com.linecorp.flagship4j.javaflagr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEvaluationResponse implements Serializable {

    @JsonProperty("flagID")
    private Long flagID;

    @JsonProperty("flagKey")
    private String flagKey;

    @JsonProperty("flagSnapshotID")
    private Long flagSnapshotID;

    @JsonProperty("segmentID")
    private Long segmentID;

    @JsonProperty("variantID")
    private Long variantID;

    @JsonProperty("variantKey")
    private String variantKey;

    @JsonProperty("timestamp")
    private Timestamp timestamp;

    @JsonProperty("variantAttachment")
    private Map<Object, Object> variantAttachment = Collections.emptyMap();

    @JsonProperty("evalContext")
    private EvaluationContext evalContext;

    @JsonProperty("evalDebugLog")
    private EvaluationDebugLog evalDebugLog;

}
