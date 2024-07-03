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
package com.linecorp.flagship4j.javaflagr.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.linecorp.flagship4j.javaflagr.exception.IllegalEnumValueException;
import org.apache.commons.lang3.StringUtils;

public enum EffectiveVariant {

    ON("on"), OFF("off");

    private final String value;

    EffectiveVariant(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EffectiveVariant fromValue(String value) {
        if (StringUtils.isBlank(value)) {
            throw IllegalEnumValueException.causedByBlankValue();
        }

        for (EffectiveVariant v : values()) {
            if (StringUtils.equals(v.value, value)) {
                return v;
            }
        }

        throw IllegalEnumValueException.causedByInvalidValue(value);
    }

    @JsonValue
    public String toValue() {
        return value;
    }

    public static Boolean isValid(String value) {
        for (EffectiveVariant v : values()) {
            if (StringUtils.equals(v.value, value)) {
                return true;
            }
        }
        return false;
    }
}
