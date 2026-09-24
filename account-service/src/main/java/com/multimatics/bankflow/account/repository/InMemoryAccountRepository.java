package com.multimatics.bankflow.account.repository;
import com.multimatics.bankflow.account.domain.Account; import com.multimatics.bankflow.account.exception.DuplicateAccountException;
import org.springframework.stereotype.Repository; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Repository
public class InMemoryAccountRepository implements AccountRepository {
 private final Map<UUID,Account> store=new ConcurrentHashMap<>();
 public Account save(Account a){if(findByAccountNumber(a.accountNumber()).isPresent()) throw new DuplicateAccountException("Account number already exists"); store.put(a.id(),a); return a;}
 public Optional<Account> findById(UUID id){return Optional.ofNullable(store.get(id));}
 public Optional<Account> findByAccountNumber(String n){return store.values().stream().filter(a->a.accountNumber().equals(n)).findFirst();}
 public List<Account> findAll(){return List.copyOf(store.values());}
}
