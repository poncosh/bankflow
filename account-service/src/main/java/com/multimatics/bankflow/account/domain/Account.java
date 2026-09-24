package com.multimatics.bankflow.account.domain;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record Account(UUID id,String accountNumber,AccountType type,BigDecimal balance,String currency,AccountStatus status,Instant createdAt) { }
