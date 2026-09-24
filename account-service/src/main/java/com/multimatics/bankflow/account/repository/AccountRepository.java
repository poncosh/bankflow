package com.multimatics.bankflow.account.repository;
import com.multimatics.bankflow.account.domain.Account; import java.util.*;
public interface AccountRepository { Account save(Account account); Optional<Account> findById(UUID id); Optional<Account> findByAccountNumber(String number); List<Account> findAll(); }
