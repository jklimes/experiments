package com.payment.tracker;

class ConsoleUtils {
    static void clearLine() {
        System.out.print("\u001b[2K");
    }

    static void clearScreen(ClearScreenMode mode) {
        switch (mode) {
            case COMPLETE:
                print("\u001b[2J");
            case FROM_CURSOR_TO_BEGINNING:
                print("\u001b[1J");
            case FROM_CURSOR_TO_END:
                print("\u001b[0J");
        }
    }

    static void cursorUp(int numOfLines) {
        print("\u001b[" + numOfLines + "A");
    }

    static void cursorDown(int numOfLines) {
        print("\u001b[" + numOfLines + "B");
    }

    static void cursorRight(int numOfChars) {
        print("\u001b[" + numOfChars + "C");
    }

    static void cursorLeft(int numOfChars) {
        print("\u001b[" + numOfChars + "D");
    }

    static void cursorNextLine(int numOfLines) {
        print("\u001b[" + numOfLines + "E");
    }

    static void cursorPreviousLine(int numOfLines) {
        print("\u001b[" + numOfLines + "F");
    }

    static void saveCursorPosition() {
        print("\u001b[s");
    }

    static void restoreCursorPosition() {
        print("\u001b[u");
    }

    private static void print(String s) {
        System.out.print(s);
    }

}
