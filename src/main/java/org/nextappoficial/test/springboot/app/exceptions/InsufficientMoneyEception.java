package org.nextappoficial.test.springboot.app.exceptions;

public class InsufficientMoneyEception extends RuntimeException {

    public InsufficientMoneyEception(String message) {
        super(message);
    }
}
