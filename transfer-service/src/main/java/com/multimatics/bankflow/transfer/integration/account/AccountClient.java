package com.multimatics.bankflow.transfer.integration.account;
public interface AccountClient {
    AccountLookupResponse findByAccountNumber(String accountNumber);
}