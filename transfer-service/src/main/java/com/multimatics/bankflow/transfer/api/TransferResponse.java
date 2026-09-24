package com.multimatics.bankflow.transfer.api;
import com.multimatics.bankflow.transfer.domain.TransferStatus; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record TransferResponse(UUID id,String sourceAccount,String destinationAccount,BigDecimal amount,String currency,String clientReference,TransferStatus status,Instant createdAt) { }
