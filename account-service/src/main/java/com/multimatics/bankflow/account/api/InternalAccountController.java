package com.multimatics.bankflow.account.api;

import com.multimatics.bankflow.account.application.AccountApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/accounts")
public class InternalAccountController {
    private final AccountApplicationService service;
    public InternalAccountController(AccountApplicationService service) { this.service = service; }

    @GetMapping("/{accountNumber}")
    InternalAccountResponse find(
            @PathVariable String accountNumber,
            @RequestHeader(name="X-BankFlow-Lab-Delay-Ms", defaultValue="0") long delayMs)
            throws InterruptedException {
        if (delayMs > 0) Thread.sleep(Math.min(delayMs, 10_000));
        return service.findInternal(accountNumber);
    }
}
