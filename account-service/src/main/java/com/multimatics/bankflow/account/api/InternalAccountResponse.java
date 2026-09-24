package com.multimatics.bankflow.account.api;

import com.multimatics.bankflow.account.domain.AccountStatus;
import com.multimatics.bankflow.account.domain.AccountType;
import java.math.BigDecimal;

public record InternalAccountResponse(
        String accountNumber, AccountType type, BigDecimal balance,
        String currency, AccountStatus status) { }
