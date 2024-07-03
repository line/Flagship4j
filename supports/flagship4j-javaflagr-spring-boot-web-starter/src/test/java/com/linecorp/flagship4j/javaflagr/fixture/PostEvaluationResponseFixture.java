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
package com.linecorp.flagship4j.javaflagr.fixture;


import com.linecorp.flagship4j.javaflagr.model.EvaluationContext;
import com.linecorp.flagship4j.javaflagr.model.EvaluationDebugLog;
import com.linecorp.flagship4j.javaflagr.model.PostEvaluationResponse;
import com.linecorp.flagship4j.javaflagr.utils.RandomUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostEvaluationResponseFixture {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static PostEvaluationResponse mockSuccessPostEvaluationResponse() throws ParseException {
        return mockSuccessPostEvaluationResponse("exist_feature_flag", "on");
    }

    public static PostEvaluationResponse mockSuccessPostEvaluationResponse(String flagKey, String variantKey)
            throws ParseException {
        PostEvaluationResponse result = RandomUtils.generateObject(PostEvaluationResponse.class);

        EvaluationContext returnEvalContext = RandomUtils.generateObject(EvaluationContext.class);
        returnEvalContext.setFlagKey(flagKey);
        returnEvalContext.setEntityID("randomly_generated_1486111485");
        result.setEvalContext(returnEvalContext);

        result.setFlagID(1L);
        result.setFlagKey(flagKey);
        result.setFlagSnapshotID(206L);
        result.setSegmentID(16L);
        result.setVariantID(1L);
        result.setVariantKey(variantKey);

        Date parsedDate = dateFormat.parse("2022-05-31T08:31:37Z");
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        result.setTimestamp(timestamp);

        return result;
    }

    public static PostEvaluationResponse mockFlagKeyNotFoundPostEvaluationResponse() throws ParseException {
        PostEvaluationResponse result = RandomUtils.generateObject(PostEvaluationResponse.class);

        result.setFlagKey("not_exist_flag");

        EvaluationContext returnEvalContext = RandomUtils.generateObject(EvaluationContext.class);
        returnEvalContext.setFlagKey("not_exist_flag");
        result.setEvalContext(returnEvalContext);

        EvaluationDebugLog returnDebugLog = RandomUtils.generateObject(EvaluationDebugLog.class);
        returnDebugLog.setMsg("flagID 0 not found or deleted");
        result.setEvalDebugLog(returnDebugLog);

        Date parsedDate = dateFormat.parse("2022-06-02T08:13:57Z");
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        result.setTimestamp(timestamp);

        return result;
    }

    public static PostEvaluationResponse mockVariantKeyNotFoundPostEvaluationResponse() throws ParseException {
        PostEvaluationResponse result = RandomUtils.generateObject(PostEvaluationResponse.class);

        result.setFlagID(1L);
        result.setFlagKey("exist_feature_flag");
        result.setFlagSnapshotID(208L);
        result.setSegmentID(16L);

        EvaluationContext returnEvalContext = RandomUtils.generateObject(EvaluationContext.class);
        returnEvalContext.setFlagKey("exist_feature_flag");
        returnEvalContext.setEntityID("randomly_generated_1139424147");
        result.setEvalContext(returnEvalContext);

        Date parsedDate = dateFormat.parse("2022-06-02T08:16:48Z");
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        result.setTimestamp(timestamp);

        return result;
    }

    public static PostEvaluationResponse mockVariantKeyNotMatchPostEvaluationResponse() throws ParseException {
        PostEvaluationResponse result = RandomUtils.generateObject(PostEvaluationResponse.class);

        result.setFlagID(1L);
        result.setFlagKey("exist_feature_flag");
        result.setFlagSnapshotID(209L);
        result.setSegmentID(16L);
        result.setVariantID(10L);
        result.setVariantKey("On");

        EvaluationContext returnEvalContext = RandomUtils.generateObject(EvaluationContext.class);
        returnEvalContext.setFlagKey("exist_feature_flag");
        returnEvalContext.setEntityID("randomly_generated_544474078");
        result.setEvalContext(returnEvalContext);

        Date parsedDate = dateFormat.parse("2022-06-02T08:17:49Z");
        Timestamp timestamp = new Timestamp(parsedDate.getTime());
        result.setTimestamp(timestamp);

        return result;
    }
}
