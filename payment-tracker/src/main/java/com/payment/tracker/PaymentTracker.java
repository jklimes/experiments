package com.payment.tracker;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PaymentTracker {
    static class Currency {
        public Currency(String code, Integer value) {
            this.code = code;
            this.value = value;
        }

        String code;
        Integer value;
    }

    private static final Map<String, Integer> currencies = new ConcurrentHashMap<>();
    private static volatile String product = null;
    private static final AtomicBoolean isStillReading = new AtomicBoolean(true);

    public static void main(String[] args) {
        loadPaymentsFromFile(new File("payment.txt"), currencies);
        startProcessingInput();
        startProducingProduct();
        startPrintingProduct();
        registerShutdownHook();
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> saveCurrenciesToFile()));
    }

    private static void saveCurrenciesToFile() {
        String s = currenciesToString();
        try (FileWriter fw = new FileWriter(new File("payment.txt"), false)) {
            fw.write(s);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void startProcessingInput() {
        new Thread(() -> {
            while (true) {
                processInput();
                sleep(10);
            }
        }).start();
    }

    private static void startPrintingProduct() {
        new Thread(() -> {
            while (true) {
                printCurrentStateIfReady();
                sleep(1000);
            }
        }).start();
    }


    private static void processInput() {
        try {
            Currency currency = loadCurrencyFromInput();
            storeCurrency(currency);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Currency loadCurrencyFromInput() throws IOException {
        String inputLine = readInput();
        cursorPreviousLine(1);
        clearLine();
        return parseCurrency(inputLine);
    }

    private static void storeCurrency(Currency currency) {
        if (currency != null && currency.value != 0) {
            if (currencies.containsKey(currency.code)) {
                int updatedValue = currencies.get(currency.code) + currency.value;
                if (updatedValue == 0) {
                    currencies.remove(currency.code);
                } else {
                    currencies.put(currency.code, updatedValue);
                }
            } else {
                currencies.put(currency.code, currency.value);
            }
        }
    }

    private static void printCurrentStateIfReady() {
        if (product != null) {
            saveCursorPosition();
            clearScreen(ClearScreen.FROM_CURSOR_TO_END);
            System.out.print("\n" +product);
            restoreCursorPosition();
            product = null;
        }
    }


    private static void loadPaymentsFromFile(File input, Map<String, Integer> currencies) {
        try (FileReader fr = new FileReader(input); BufferedReader bfr = new BufferedReader(fr)) {
            bfr.lines().forEach(s -> {
                Currency curr = parseCurrency(s);
                currencies.put(curr.code, curr.value);
            });
        } catch (IOException e) {
            System.out.println("File " + input.getAbsolutePath() + " does not exist.");
        }
    }

    private static Currency parseCurrency(String s) {
        String[] tokens = s.split(" ");
        if (tokens.length != 2) {
            return null;
        }
        String currency = tokens[0].toUpperCase();
        Integer value;
        try {
            value = Integer.valueOf(tokens[1].trim());
        } catch (NumberFormatException e) {
            return null;
        }
        return new Currency(currency, value);
    }

    private static String readInput() {
        InputStream in = System.in;
        int i;
        try {
            StringBuilder sb = new StringBuilder();
            isStillReading.set(true);
            while ((i = in.read()) != '\n') {
                sb.append((char) i);
                sleep(50);
            }
            isStillReading.set(false);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void cursorLeft(int numOfChars) {
        System.out.print("\u001b[" + numOfChars + "C");
    }

    private static void cursorUp(int numOfLines) {
        System.out.print("\u001b[" + numOfLines + "A");
    }
    private static void cursorPreviousLine(int numOfLines) {
        System.out.print("\u001b[" + numOfLines + "F");
    }

    private static void clearLine() {
        System.out.print("\u001b[2K");
    }

    private static void saveCursorPosition() {
        System.out.print("\u001b[s");
    }

    private static void restoreCursorPosition() {
        System.out.print("\u001b[u");
    }

    static enum ClearScreen {
        COMPLETE, FROM_CURSOR_TO_BEGINNING, FROM_CURSOR_TO_END;
    }

    private static void clearScreen(ClearScreen mode) {
        switch (mode) {
            case COMPLETE: System.out.print("\u001b[2J");
            case FROM_CURSOR_TO_BEGINNING: System.out.print("\u001b[1J");
            case FROM_CURSOR_TO_END: System.out.print("\u001b[0J");
        }
    }


    private static void startProducingProduct() {
        new Thread(() -> {
            while (true) {
                synchronized (PaymentTracker.class) {
                    product = currenciesToString();
                    sleep(100);
                }
            }
        }).start();
    }

    private static String currenciesToString() {
        return currencies.entrySet().stream().collect(
                StringBuilder::new,
                (o, stringIntegerEntry) -> o.append(stringIntegerEntry.getKey() + " " + stringIntegerEntry.getValue() + "\n"),
                StringBuilder::append).toString();
    }
}
