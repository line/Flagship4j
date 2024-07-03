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
package com.linecorp.flagship4j.javaflagr.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import com.linecorp.flagship4j.javaflagr.exceptions.IllegalEnumValueException;

public enum EffectiveVariant {

    ON("on", "true", "1"),

    OFF("off", "false", "0");

    private static final Map<String, EffectiveVariant> BY_VALUE = new HashMap<>();

    static {
        for (EffectiveVariant e : values()) {
            for (String value : e.values) {
                BY_VALUE.put(value, e);
            }
        }
    }

    @JsonCreator
    public static EffectiveVariant fromValue(String value) {
        if (StringUtils.isBlank(value)) {
            throw IllegalEnumValueException.causedByBlankValue();
        }

        if (!BY_VALUE.containsKey(value)) {
            throw IllegalEnumValueException.causedByInvalidValue(value);
        }

        return BY_VALUE.get(value);
    }

    private final List<String> values;

    EffectiveVariant(String... values) {
        this.values = Arrays.asList(values);
    }

    @JsonValue
    public String toValue() {
        return values.get(0);
    }

    public static Boolean isValid(String value) {
        return BY_VALUE.containsKey(value.toLowerCase());
    }

}
