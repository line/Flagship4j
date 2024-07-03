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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationContext implements Serializable {

    @JsonProperty("entityID")
    private String entityID;

    @JsonProperty("entityType")
    private String entityType;

    @JsonProperty("entityContext")
    private Map<Object, Object> entityContext = Collections.emptyMap();

    @JsonProperty("enableDebug")
    private Boolean enableDebug;

    @JsonProperty("flagID")
    private Long flagID;

    @JsonProperty("flagKey")
    private String flagKey;

    @JsonProperty("flagTags")
    private List<String> flagTags;

    @JsonProperty("flagTagsOperator")
    private String flagTagsOperator;

}
