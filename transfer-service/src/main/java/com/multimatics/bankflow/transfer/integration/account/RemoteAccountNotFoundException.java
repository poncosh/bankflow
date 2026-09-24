package com.multimatics.bankflow.transfer.integration.account;
public class RemoteAccountNotFoundException extends RuntimeException {
    public RemoteAccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}