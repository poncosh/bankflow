package com.multimatics.bankflow.account.api;
import com.multimatics.bankflow.account.domain.AccountType; import jakarta.validation.constraints.*; import java.math.BigDecimal;
public record CreateAccountRequest(@NotBlank @Pattern(regexp="\\d{12}") String accountNumber,
 @NotNull AccountType type,@NotNull @DecimalMin("0.00") BigDecimal openingBalance,
 @NotBlank @Pattern(regexp="[A-Z]{3}") String currency) { }
