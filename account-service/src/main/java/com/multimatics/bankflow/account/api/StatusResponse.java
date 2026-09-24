package com.multimatics.bankflow.account.api;
import com.multimatics.bankflow.account.domain.AccountStatus; public record StatusResponse(String accountNumber,AccountStatus status) { }
