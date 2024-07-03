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

import lombok.Getter;

@Getter
public class IllegalEnumValueException extends IllegalArgumentException {

    protected static final String CAUSED_BY_BLANK_VALUE_MESSAGE = "Value cannot be null or empty.";

    protected static final String CAUSED_BY_INVALID_VALUE_MESSAGE_FORMAT = "Cannot create enum from %s.";

    public static IllegalEnumValueException causedByBlankValue() {
        return new IllegalEnumValueException(CAUSED_BY_BLANK_VALUE_MESSAGE, null);
    }

    public static IllegalEnumValueException causedByInvalidValue(Object value) {
        return new IllegalEnumValueException(
                String.format(CAUSED_BY_INVALID_VALUE_MESSAGE_FORMAT, value.toString()), value);
    }

    private Object enumValue;

    public IllegalEnumValueException(String message, Object enumValue) {
        super(message);
        this.enumValue = enumValue;
    }

}
