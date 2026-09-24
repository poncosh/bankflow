package com.multimatics.bankflow.transfer;

import com.multimatics.bankflow.transfer.integration.account.AccountClient;
import com.multimatics.bankflow.transfer.integration.account.AccountServiceUnavailableException;
import com.multimatics.bankflow.transfer.integration.account.RemoteAccountNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.service-registry.auto-registration.enabled=false"
        })
class RestAccountClientCircuitBreakerTest {

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    @MockitoBean(name = "accountRestClient")
    private RestClient restClient;

    @BeforeEach
    void resetCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker("accountService").reset();
    }

    @Test
    void appliesNamedCircuitBreakerAndUsesFallbackWhenLookupFails() {
        var transportFailure = new IOException("Account Service is offline");
        when(restClient.get()).thenThrow(
                new ResourceAccessException("Connection failed", transportFailure));

        AccountServiceUnavailableException exception = assertThrows(
                AccountServiceUnavailableException.class,
                () -> accountClient.findByAccountNumber("100000000001"));

        assertTrue(AopUtils.isAopProxy(accountClient));
        assertEquals(
                "Account validation is temporarily unavailable",
                exception.getMessage());
        assertInstanceOf(AccountServiceUnavailableException.class, exception.getCause());
        assertEquals(
                3,
                retryRegistry.retry("accountService")
                        .getRetryConfig()
                        .getMaxAttempts());
        verify(restClient, times(3)).get();
        assertTrue(
                circuitBreakerRegistry.circuitBreaker("accountService")
                        .getMetrics()
                        .getNumberOfFailedCalls() >= 1);
    }

    @Test
    void doesNotRetryWhenAccountDoesNotExist() {
        String accountNumber = "999999999999";
        when(restClient.get()).thenThrow(
                new RemoteAccountNotFoundException(accountNumber));

        RemoteAccountNotFoundException exception = assertThrows(
                RemoteAccountNotFoundException.class,
                () -> accountClient.findByAccountNumber(accountNumber));

        assertEquals("Account not found: " + accountNumber, exception.getMessage());
        verify(restClient).get();
        assertEquals(
                0,
                circuitBreakerRegistry.circuitBreaker("accountService")
                        .getMetrics()
                        .getNumberOfFailedCalls());
    }
}
