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
package com.linecorp.flagship4j.examples;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.linecorp.flagship4j.javaflagr.DefaultOpenFlagr;
import com.linecorp.flagship4j.javaflagr.OpenFlagr;
import com.linecorp.flagship4j.javaflagr.configs.OpenFlagrConfig;
import com.linecorp.flagship4j.openfeature.OpenFlagrProvider;

import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.OpenFeatureAPI;

import java.io.IOException;

public class OpenFeatureExample {

    private static final String FLAG_KEY = "hello-world-enabled";

    public static void main(String[] args) throws InterruptedException, IOException {
        OpenFlagrConfig openFlagrConfig = OpenFlagrConfig.builder()
                .endpoint("http://localhost:18000")
                .callTimeoutSeconds(10)
                .connectionTimeoutSeconds(10)
                .readTimeoutSeconds(10)
                .writeTimeoutSeconds(10)
                .build();
        OpenFlagr openFlagr = new DefaultOpenFlagr(openFlagrConfig);

        OpenFeatureAPI api = OpenFeatureAPI.getInstance();
        api.setProvider(new OpenFlagrProvider(openFlagr));

        Client client = api.getClient();

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = defaultTerminalFactory.createTerminal();
        Screen screen = new TerminalScreen(terminal);
        Boolean previousToggleState = null;

        screen.startScreen();

        screen.setCursorPosition(null);

        while (true) {
            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null &&
                    (keyStroke.getKeyType().equals(KeyType.Escape) ||
                            keyStroke.getKeyType().equals(KeyType.EOF))) {
                break;
            }

            Boolean isHelloWorldEnabled = client.getBooleanValue(FLAG_KEY, false);
            String helloWorldEnabledString = isHelloWorldEnabled ? "enabled" : "disabled";

            if (previousToggleState != isHelloWorldEnabled) {
                previousToggleState = isHelloWorldEnabled;
                screen.clear();
            }

            textBox(
                    screen,
                    new TerminalPosition(1, 1),
                    String.format(" Hello World: %s ", helloWorldEnabledString));

            if (isHelloWorldEnabled) {
                textLabel(
                        screen,
                        new TerminalPosition(1, 5),
                        " Hello World ");
            }

            screen.refresh();

            Thread.yield();
        }

        screen.stopScreen();
        screen.close();
    }

    private static void textBox(
            Screen screen,
            TerminalPosition topLeftPosition,
            String message) {
        TextGraphics textGraphics = screen.newTextGraphics();
        TerminalSize boxSize = new TerminalSize(message.length() + 2, 3);
        TerminalPosition topRightPosition = topLeftPosition
                .withRelativeColumn(boxSize.getColumns() - 1);

        // box
        textGraphics.fillRectangle(topLeftPosition, boxSize, ' ');

        // top lines
        textGraphics.drawLine(
                topLeftPosition.withRelativeColumn(1),
                topLeftPosition.withRelativeColumn(boxSize.getColumns() - 2),
                Symbols.DOUBLE_LINE_HORIZONTAL);

        // bottom lines
        textGraphics.drawLine(
                topLeftPosition.withRelativeRow(2).withRelativeColumn(1),
                topLeftPosition.withRelativeRow(2)
                        .withRelativeColumn(boxSize.getColumns() - 2),
                Symbols.DOUBLE_LINE_HORIZONTAL);

        // left lines
        textGraphics.setCharacter(topLeftPosition,
                Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        textGraphics.setCharacter(topLeftPosition.withRelativeRow(1),
                Symbols.DOUBLE_LINE_VERTICAL);
        textGraphics.setCharacter(topLeftPosition.withRelativeRow(2),
                Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);

        // right lines
        textGraphics.setCharacter(topRightPosition,
                Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        textGraphics.setCharacter(topRightPosition.withRelativeRow(1),
                Symbols.DOUBLE_LINE_VERTICAL);
        textGraphics.setCharacter(topRightPosition.withRelativeRow(2),
                Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);

        textGraphics.putString(topLeftPosition.withRelative(1, 1), message);
    }

    private static void textLabel(
            Screen screen,
            TerminalPosition topLeftPosition,
            String message) {
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.putString(topLeftPosition, message);
    }

}
