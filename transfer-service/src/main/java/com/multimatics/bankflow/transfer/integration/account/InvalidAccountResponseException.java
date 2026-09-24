package com.multimatics.bankflow.transfer.integration.account;
public class InvalidAccountResponseException extends RuntimeException {
    public InvalidAccountResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}