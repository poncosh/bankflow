package com.multimatics.bankflow.account.application;
import com.multimatics.bankflow.account.api.*; import com.multimatics.bankflow.account.domain.*; import com.multimatics.bankflow.account.exception.AccountNotFoundException;
import com.multimatics.bankflow.account.repository.AccountRepository; import org.springframework.stereotype.Service; import java.time.Instant; import java.util.*;
@Service
public class AccountApplicationService {
 private final AccountRepository repository; public AccountApplicationService(AccountRepository repository){this.repository=repository;}
 public AccountResponse create(CreateAccountRequest r){Account a=new Account(UUID.randomUUID(),r.accountNumber(),r.type(),r.openingBalance(),r.currency(),AccountStatus.ACTIVE,Instant.now()); return toResponse(repository.save(a));}
 public AccountResponse get(UUID id){return toResponse(find(id));}
 public List<AccountResponse> findAll(){return repository.findAll().stream().map(this::toResponse).toList();}
 public BalanceResponse balance(UUID id){Account a=find(id);return new BalanceResponse(a.accountNumber(),a.balance(),a.currency());}
 public StatusResponse status(UUID id){Account a=find(id);return new StatusResponse(a.accountNumber(),a.status());}
 public InternalAccountResponse findInternal(String accountNumber){Account a=repository.findByAccountNumber(accountNumber).orElseThrow(()->new AccountNotFoundException("Account not found"));return new InternalAccountResponse(a.accountNumber(),a.type(),a.balance(),a.currency(),a.status());}
 private Account find(UUID id){return repository.findById(id).orElseThrow(()->new AccountNotFoundException("Account not found"));}
 private AccountResponse toResponse(Account a){return new AccountResponse(a.id(),a.accountNumber(),a.type(),a.balance(),a.currency(),a.status(),a.createdAt());}
}
