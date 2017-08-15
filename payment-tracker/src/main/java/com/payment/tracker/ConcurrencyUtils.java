package com.payment.tracker;

import java.util.concurrent.TimeUnit;

class ConcurrencyUtils {
    static void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
