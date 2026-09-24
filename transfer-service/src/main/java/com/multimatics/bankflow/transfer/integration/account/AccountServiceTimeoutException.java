package com.multimatics.bankflow.transfer.integration.account;
public class AccountServiceTimeoutException extends RuntimeException {
    public AccountServiceTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}