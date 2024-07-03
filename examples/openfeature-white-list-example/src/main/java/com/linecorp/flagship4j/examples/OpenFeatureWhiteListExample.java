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
package com.linecorp.flagship4j.examples;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.linecorp.flagship4j.javaflagr.DefaultOpenFlagr;
import com.linecorp.flagship4j.javaflagr.OpenFlagr;
import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.openfeature.OpenFlagrProvider;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;
import dev.openfeature.sdk.Value;

public class OpenFeatureWhiteListExample {

    private static final String FLAG_KEY = "white-list-flagr";

    private static final String CONFIG_WHITE_LIST = "whiteList";

    private static final String FLAGR_ENDPOINT = "http://localhost:18000";

    public static void main(String[] args) {
        final Client toggleClient = getToggleClient();
        final List<String> userList = IntStream.rangeClosed(1, 10)
                                               .mapToObj(i -> "user" + i)
                                               .collect(Collectors.toList());
        String isNext;
        do {
            final Value value = toggleClient.getObjectValue(FLAG_KEY, new Value());
            final List<String> whiteList = getWhiteList(value);
            final Map<String, ValidationStatus> result = getResult(userList, whiteList);
            printResultTable(userList, result);
            System.out.println("Enter Y to request again (press other to exit)");
            isNext = new Scanner(System.in).next();
        } while ("Y".equalsIgnoreCase(isNext));
        System.exit(0);
    }

    private static void printResultTable(List<String> userList, Map<String, ValidationStatus> result) {
        System.out.println("+-----------------+---------------+");
        System.out.println("|      User       |     Status    |");
        System.out.println("+-----------------+---------------+");

        userList.forEach(user -> System.out.printf("| %-15s | %-13s |\n", user, result.get(user)));

        System.out.println("+-----------------+---------------+");
    }

    private static Map<String, ValidationStatus> getResult(List<String> userList, List<String> whiteList) {
        return userList.stream()
                       .collect(Collectors.toMap(user -> user, user -> validate(user, whiteList)));
    }

    private static ValidationStatus validate(String user, List<String> whiteList) {
        return whiteList.contains(user) ? ValidationStatus.ALLOW : ValidationStatus.DISALLOW;
    }

    private static List<String> getWhiteList(Value value) {
        if (value.isNull()) {
            return Collections.emptyList();
        }
        return value.asStructure()
                    .getValue(CONFIG_WHITE_LIST)
                    .asList()
                    .stream()
                    .map(Value::asString)
                    .collect(Collectors.toList());
    }

    private static Client getToggleClient() {
        OpenFlagrConfig openFlagrConfig = OpenFlagrConfig.builder()
                                                         .endpoint(FLAGR_ENDPOINT)
                                                         .callTimeoutSeconds(10)
                                                         .connectionTimeoutSeconds(10)
                                                         .readTimeoutSeconds(10)
                                                         .writeTimeoutSeconds(10)
                                                         .build();
        OpenFlagr openFlagr = new DefaultOpenFlagr(openFlagrConfig);

        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProvider(new OpenFlagrProvider(openFlagr));

        return api.getClient();
    }

    private enum ValidationStatus {
        ALLOW,
        DISALLOW
    }
}
