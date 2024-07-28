package org.spauk.currencyconverter.exception;

public class InvalidCurrencyCodeException extends RuntimeException {

    public InvalidCurrencyCodeException(String message) {
        super(message);
    }
}
