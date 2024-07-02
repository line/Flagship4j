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
package com.linecorp.flagship4j.javaflagr;

import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.EvaluationDebugLog;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import com.linecorp.flagship4j.javaflagr.model.SegmentDebugLog;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestFlagrDataGenerator {

    public final String entityID = "a1234";
    public final String entityType = "report";
    public final Long flagID = 1L;
    public final String flagKey = "test";
    public final String flagTagsOperator = "LW00001";
    public final Map<Object, Object> entityContext = Collections.singletonMap("hello", "world");
    public final List<String> flagTags = Arrays.asList("testTag");
    public final Long flagSnapshotID = 1L;
    public final Long segmentID = 1L;
    public final Long variantID = 1L;
    public final String variantKey = "on";
    public final Timestamp timestamp = Timestamp.from(Instant.now());
    public final Map<Object, Object> variantAttachment = Collections.singletonMap("color", "red");
    public final String msg = "matched all constraints";

    public EvaluationContext givenEvaluationContext() {
        return EvaluationContext.builder()
                .enableDebug(true)
                .entityID(entityID)
                .entityContext(entityContext)
                .entityType(entityType)
                .flagKey(flagKey)
                .flagID(flagID)
                .flagTags(flagTags)
                .flagTagsOperator(flagTagsOperator)
                .build();
    }


    public PostEvaluationResponse givenPostEvaluationResponse() {
        return PostEvaluationResponse.builder()
                .segmentID(segmentID)
                .evalContext(givenEvaluationContext())
                .evalDebugLog(givenEvaluationDebugLog())
                .flagID(flagID)
                .flagKey(flagKey)
                .flagSnapshotID(flagSnapshotID)
                .timestamp(timestamp)
                .variantAttachment(variantAttachment)
                .variantID(variantID)
                .variantKey(variantKey)
                .build();
    }

    public EvaluationDebugLog givenEvaluationDebugLog() {
        return EvaluationDebugLog.builder()
                .msg(msg)
                .segmentDebugLogs(Arrays.asList(givenSegmentDebugLog()))
                .build();
    }

    public SegmentDebugLog givenSegmentDebugLog() {
        return SegmentDebugLog.builder()
                .segmentID(segmentID)
                .msg(msg)
                .build();
    }
}
