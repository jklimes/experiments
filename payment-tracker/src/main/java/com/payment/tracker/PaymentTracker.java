package com.payment.tracker;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.payment.tracker.ConsoleUtils.*;
import static com.payment.tracker.ConcurrencyUtils.*;

public class PaymentTracker {
    private static final Map<String, Integer> currencies = new ConcurrentHashMap<>();
    private static volatile String product = null;
    private static volatile String paymentFilePath = null;

    public static void main(String[] args) {
        initPaymentFilePath(args);
        loadPaymentsFromFile();
        startProcessingInput();
        startProducingProduct();
        startPrintingProduct();
        registerShutdownHook();
    }

    private static void initPaymentFilePath(String[] args) {
        if (args.length == 1) {
            if (!new File(args[0]).exists()) {
                try {
                    paymentFilePath = args[0];
                    new File(paymentFilePath).createNewFile();
                } catch (IOException e) {
                    initDefaultPaymentFilePath();
                }
            } else {
                paymentFilePath = args[0];
            }
        } else {
            initDefaultPaymentFilePath();
        }
    }

    private static void initDefaultPaymentFilePath() {
        String home = System.getProperty("user.home");
        paymentFilePath = home + System.getProperty("file.separator") + "payment.txt";
        try {
            new File(paymentFilePath).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadPaymentsFromFile() {
        File paymentFile = new File(paymentFilePath);
        try (FileReader fr = new FileReader(paymentFile); BufferedReader bfr = new BufferedReader(fr)) {
            bfr.lines().forEach(s -> {
                Currency curr = Currency.fromString(s);
                currencies.put(curr.code, curr.value);
            });
        } catch (IOException e) {
            System.out.println("File " + paymentFile.getAbsolutePath() + " does not exist.");
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
        return Currency.fromString(inputLine);
    }

    private static String readInput() {
        InputStream in = System.in;
        int i;
        try {
            StringBuilder sb = new StringBuilder();
            while ((i = in.read()) != '\n') {
                sb.append((char) i);
                sleep(50);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startProducingProduct() {
        new Thread(() -> {
            while (true) {
                synchronized (PaymentTracker.class) {
                    product = Currency.toString(currencies);
                    sleep(100);
                }
            }
        }).start();
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

    private static void startPrintingProduct() {
        new Thread(() -> {
            while (true) {
                printCurrentStateIfReady();
                sleep(1000);
            }
        }).start();
    }

    private static void printCurrentStateIfReady() {
        if (product != null) {
            saveCursorPosition();
            clearScreen(ClearScreenMode.FROM_CURSOR_TO_END);
            System.out.print("\n" + product);
            restoreCursorPosition();
            product = null;
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveCurrenciesToFile();
            clearScreen(ClearScreenMode.FROM_CURSOR_TO_END);
        }));
    }

    private static void saveCurrenciesToFile() {
        String s = Currency.toString(currencies);
        try (FileWriter fw = new FileWriter(new File(paymentFilePath), false)) {
            fw.write(s);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
