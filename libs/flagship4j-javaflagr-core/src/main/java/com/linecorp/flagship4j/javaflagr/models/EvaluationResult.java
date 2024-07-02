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
package com.linecorp.flagship4j.javaflagr.models;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationResult {

    @JsonAlias("flagID")
    private Long flagId;

    private String flagKey;

    @JsonAlias("flagSnapshotID")
    private Long flagSnapshotId;

    @JsonAlias("segmentID")
    private Long segmentId;

    @JsonAlias("variantID")
    private Long variantId;

    private String variantKey;

    private Map<String, Object> variantAttachment;

    @JsonAlias("evalContext")
    private EvaluationContext evaluationContext;

    private Instant timestamp;

    @JsonAlias("evalDebugLog")
    private EvaluationDebugLog evaluationDebugLog;

}
