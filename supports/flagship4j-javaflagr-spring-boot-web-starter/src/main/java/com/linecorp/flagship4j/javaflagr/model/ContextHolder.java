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
package com.linecorp.flagship4j.javaflagr.model;

import com.linecorp.flagship4j.javaflagr.bean.BeanLocator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContextHolder {

    public static Optional<Context> get() {
        try {
            Optional<Context> result = BeanLocator.get(Context.class);
            loadLazyBean(result);
            return result;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getServiceId() {
        return get().map(Context::getServiceId);
    }

    private static void loadLazyBean(Optional<Context> bean) {
        if (bean.isPresent()) {
            bean.get().getServiceId();
        }
    }

}
