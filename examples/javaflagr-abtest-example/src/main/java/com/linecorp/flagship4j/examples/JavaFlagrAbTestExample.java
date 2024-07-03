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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
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

public class JavaFlagrAbTestExample {

    private static final String FLAG_KEY = "blue-green-exp";

    private static List<String> userIds = new ArrayList<String>() {
        {
            add("bd4535a4-5ee6-40bb-ae06-e5f565b2c0ea");
            add("ae0bd08e-cc87-4ef3-a85b-8acdd4133f07");
        }
    };

    public static void main(String[] args) throws InterruptedException, IOException {
        String userId = userIds.get(0);
        OpenFlagrConfig openFlagrConfig = OpenFlagrConfig.builder()
                .endpoint("http://localhost:18000")
                .callTimeoutMs(10000)
                .connectionTimeoutMs(10000)
                .readTimeoutMs(10000)
                .writeTimeoutMs(10000)
                .build();
        OpenFlagr openFlagr = new DefaultOpenFlagr(openFlagrConfig);

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = defaultTerminalFactory.createTerminal();
        Screen screen = new TerminalScreen(terminal);

        screen.startScreen();

        screen.setCursorPosition(null);

        while (true) {
            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null &&
                    (keyStroke.getKeyType().equals(KeyType.Escape) ||
                            keyStroke.getKeyType().equals(KeyType.EOF))) {
                break;
            }

            if (keyStroke != null && (keyStroke.getCharacter() == Character.valueOf('1'))) {
                userId = userIds.get(0);
                screen.clear();
            }

            if (keyStroke != null && (keyStroke.getCharacter() == Character.valueOf('2'))) {
                userId = userIds.get(1);
                screen.clear();
            }

            if (keyStroke != null && (keyStroke.getCharacter() == Character.valueOf('0'))) {
                userId = UUID.randomUUID().toString();
                screen.clear();
            }

            Optional<String> colorOptional = openFlagr.getVariantKey(FLAG_KEY, userId);
            String message = " No variant key ";
            TextColor textColor = TextColor.ANSI.DEFAULT;

            if (colorOptional.isPresent()) {
                String color = colorOptional.get();

                switch (color) {
                    case "blue":
                        textColor = TextColor.ANSI.BLUE;
                        break;
                    case "green":
                        textColor = TextColor.ANSI.GREEN;
                        break;
                    default:
                        textColor = TextColor.ANSI.DEFAULT;
                        break;
                }

                message = String.format(" Variant Key: %s ", color);
            }

            textBox(
                    screen,
                    new TerminalPosition(1, 1),
                    String.format(" User ID: %s ", userId));

            textLabel(
                    screen,
                    new TerminalPosition(1, 5),
                    textColor,
                    message);

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
            TextColor textColor,
            String message) {
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.setForegroundColor(textColor);
        textGraphics.putString(topLeftPosition, message);
    }

}
