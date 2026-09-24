package com.multimatics.bankflow.transfer.api;
import jakarta.validation.constraints.*; import java.math.BigDecimal;
public record CreateTransferRequest(@NotBlank @Pattern(regexp="\\d{12}") String sourceAccount,
 @NotBlank @Pattern(regexp="\\d{12}") String destinationAccount,@NotNull @DecimalMin("1.00") BigDecimal amount,
 @NotBlank @Pattern(regexp="[A-Z]{3}") String currency,@NotBlank String clientReference) { }
