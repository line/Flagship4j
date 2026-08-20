/*
 * Copyright 2026 LY Corporation
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.flagship4j.javaflagr.exceptions.IllegalEnumValueException;

class EffectiveVariantTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @ValueSource(strings = { "on", "true", "1" })
    void fromValueResolvesEveryOnAlias(String value) {
        assertSame(EffectiveVariant.ON, EffectiveVariant.fromValue(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "off", "false", "0" })
    void fromValueResolvesEveryOffAlias(String value) {
        assertSame(EffectiveVariant.OFF, EffectiveVariant.fromValue(value));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    void fromValueRejectsBlankValue(String value) {
        final IllegalEnumValueException ex =
                assertThrows(IllegalEnumValueException.class, () -> EffectiveVariant.fromValue(value));

        assertEquals("Value cannot be null or empty.", ex.getMessage());
        assertNull(ex.getEnumValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ON", "Off", "yes", "2" })
    void fromValueRejectsUnknownValue(String value) {
        final IllegalEnumValueException ex =
                assertThrows(IllegalEnumValueException.class, () -> EffectiveVariant.fromValue(value));

        assertEquals(String.format("Cannot create enum from %s.", value), ex.getMessage());
        assertEquals(value, ex.getEnumValue());
    }

    @Test
    void toValueReturnsThePrimaryAlias() {
        assertEquals("on", EffectiveVariant.ON.toValue());
        assertEquals("off", EffectiveVariant.OFF.toValue());
    }

    @ParameterizedTest
    @ValueSource(strings = { "on", "ON", "On", "true", "TRUE", "1", "off", "OFF", "false", "0" })
    void isValidAcceptsAliasesRegardlessOfCase(String value) {
        assertTrue(EffectiveVariant.isValid(value));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "yes", "2", "onn" })
    void isValidRejectsUnknownValue(String value) {
        assertFalse(EffectiveVariant.isValid(value));
    }

    @Test
    void serializesToItsPrimaryAlias() throws Exception {
        assertEquals("\"on\"", objectMapper.writeValueAsString(EffectiveVariant.ON));
        assertEquals("\"off\"", objectMapper.writeValueAsString(EffectiveVariant.OFF));
    }

    @Test
    void deserializesFromAnyAlias() throws Exception {
        assertSame(EffectiveVariant.ON, objectMapper.readValue("\"true\"", EffectiveVariant.class));
        assertSame(EffectiveVariant.OFF, objectMapper.readValue("\"0\"", EffectiveVariant.class));
    }

    @Test
    void exposesBothConstants() {
        assertEquals(2, EffectiveVariant.values().length);
        assertSame(EffectiveVariant.ON, EffectiveVariant.valueOf("ON"));
    }
}
