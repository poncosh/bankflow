package com.multimatics.bankflow.account.api;
import com.multimatics.bankflow.account.application.AccountApplicationService; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/v1/accounts")
public class AccountController {
 private final AccountApplicationService service; public AccountController(AccountApplicationService service){this.service=service;}
 @PostMapping ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));}
 @GetMapping("/{id}") AccountResponse get(@PathVariable UUID id){return service.get(id);}
 @GetMapping List<AccountResponse> findAll(){return service.findAll();}
 @GetMapping("/{id}/balance") BalanceResponse balance(@PathVariable UUID id){return service.balance(id);}
 @GetMapping("/{id}/status") StatusResponse status(@PathVariable UUID id){return service.status(id);}
}
