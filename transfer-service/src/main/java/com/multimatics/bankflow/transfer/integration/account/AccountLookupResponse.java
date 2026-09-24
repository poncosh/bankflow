package com.multimatics.bankflow.transfer.integration.account;

import java.math.BigDecimal;
public record AccountLookupResponse(
        String accountNumber,
        String type,
        BigDecimal balance,
        String currency,
        String status
) { }