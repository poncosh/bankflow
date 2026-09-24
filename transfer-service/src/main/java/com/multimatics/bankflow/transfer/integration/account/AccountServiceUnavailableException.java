package com.multimatics.bankflow.transfer.integration.account;
public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}