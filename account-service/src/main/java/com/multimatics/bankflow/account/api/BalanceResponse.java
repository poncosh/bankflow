package com.multimatics.bankflow.account.api;
import java.math.BigDecimal; public record BalanceResponse(String accountNumber,BigDecimal balance,String currency) { }
