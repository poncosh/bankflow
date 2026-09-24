package com.multimatics.bankflow.transfer.domain;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record Transfer(UUID id,String sourceAccount,String destinationAccount,BigDecimal amount,String currency,String clientReference,TransferStatus status,Instant createdAt) { }
