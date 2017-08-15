package com.payment.tracker;

import java.util.Map;

class Currency {
    final String code;
    final Integer value;


    Currency(String code, Integer value) {
        this.code = code;
        this.value = value;
    }

    /**
     * Factory method which creates new instance of currency from string
     * @param s string to create new currency from
     * @return new currency or <code>null</code> if the given string is <code>null</code> or cannot be parsed
     */
    static Currency fromString(String s) {
        if (s == null) {
            return null;
        }

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

    static String toString(Map<String, Integer> currencies) {
        return currencies.entrySet().stream().collect(
                StringBuilder::new,
                (o, stringIntegerEntry) -> o.append(stringIntegerEntry.getKey() + " " + stringIntegerEntry.getValue() + "\n"),
                StringBuilder::append).toString();
    }

}
