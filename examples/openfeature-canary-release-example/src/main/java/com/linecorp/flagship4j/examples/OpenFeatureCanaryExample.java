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

import java.util.Scanner;
import java.util.regex.Pattern;

import com.linecorp.flagship4j.javaflagr.DefaultOpenFlagr;
import com.linecorp.flagship4j.javaflagr.OpenFlagr;
import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.openfeature.OpenFlagrProvider;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.OpenFeatureAPI;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenFeatureCanaryExample {

    // feature toggle related config
    private static final String FLAG_KEY = "canary-flag";
    public static final String FLAGR_ENDPOINT = "http://localhost:18000";

    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    // Regular Colors
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String MAGENTA = "\033[0;35m";    // MAGENTA

    public static void main(String[] args) throws InterruptedException {
        Client toggleClient = getToggleClient();

        String isNext;
        int requestTimes = 100;

        do {
            iteration(toggleClient, requestTimes);
            System.out.println(
                    "enter Y or numbers of request (Multiples of 10) to start next iteration");
            isNext = new Scanner(System.in).next();
            if (isNumeric(isNext)) {
                requestTimes = Integer.valueOf(isNext);
                isNext = "Y";
            }
        } while ("Y".equalsIgnoreCase(isNext));

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

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private static void iteration(Client toggleClient, int times) throws InterruptedException {
        int col = 0, row = 0;
        int api1 = 0, api2 = 0;

        int maxColumn = 20;
        int maxRow = times / maxColumn;

        long startTime = System.nanoTime();

        while (row < maxRow) {
            // toggle related code start
            String userId = String.valueOf(col) + row;
            MutableContext ctx = new MutableContext(userId);

            Boolean rollout = toggleClient.getBooleanValue(FLAG_KEY, false, ctx);

            if (rollout) {
                callApi2();
                api2++;
            } else {
                callApi1();
                api1++;
            }
            // toggle related code end

            col++;

            if (col == maxColumn) {
                col = 0;
                row++;
                System.out.println(RESET + "");
            }
            Thread.sleep(10);
        }

        printReport(times, api1, api2, System.nanoTime() - startTime);

    }

    private static void callApi1() {
        System.out.print(BLUE + "+");
    }

    private static void callApi2() {
        System.out.print(GREEN + "o");
    }

    private static void printReport(int times, int api1, int api2, long duration) {
        System.out.println("=====\tThis Iteration (total = " + times + ")\t=====");
        System.out.println(String.format(
                "|\t\t" + BLUE + "api1: %d calls(%,.2f%%)" + RESET + "\t\t\t\t|",
                api1, (float) api1 / times * 100));
        System.out.println(String.format(
                "|\t\t" + GREEN + "api2: %d calls(%,.2f%%)" + RESET + "\t\t\t\t|",
                api2, (float) api2 / times * 100));
        System.out.println(String.format(
                "|\t\t" + MAGENTA + "Execution Time: %,.2f Sec" + RESET + "\t\t\t|",
                (double) duration / 1000000000l));
        System.out.println("=====\t\tEnd Of This Iteration\t\t=====");
    }

}
