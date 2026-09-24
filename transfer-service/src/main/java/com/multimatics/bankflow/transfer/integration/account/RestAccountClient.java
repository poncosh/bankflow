package com.multimatics.bankflow.transfer.integration.account;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.net.SocketTimeoutException;
@Component
public class RestAccountClient implements AccountClient {
    private static final String ACCOUNT_LOOKUP_URI =
            "http://account-service/internal/v1/accounts/{accountNumber}";

    private final RestClient restClient;

    public RestAccountClient(RestClient accountRestClient) {
        this.restClient = accountRestClient;
    }

    @Override
    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "accountFallback")
    public AccountLookupResponse findByAccountNumber(String accountNumber) {
        return invokeRemoteLookup(accountNumber);
    }

    private AccountLookupResponse invokeRemoteLookup(String accountNumber) {
        try {
            AccountLookupResponse response = restClient.get()
                    .uri(ACCOUNT_LOOKUP_URI, accountNumber)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            (request, result) -> {
                                throw new RemoteAccountNotFoundException(accountNumber);
                            })
                    .onStatus(HttpStatusCode::is5xxServerError,
                            (request, result) -> {
                                throw new AccountServiceUnavailableException(
                                        "Account Service returned a server error", null);
                            })
                    .body(AccountLookupResponse.class);
            if (response == null || response.accountNumber() == null) {
                throw new InvalidAccountResponseException(
                        "Account Service returned an invalid response", null);
            }
            return response;
        } catch (RemoteAccountNotFoundException |
                 AccountServiceUnavailableException |
                 InvalidAccountResponseException ex) {
            throw ex;
        } catch (ResourceAccessException ex) {
            Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
            if (root instanceof SocketTimeoutException) {
                throw new AccountServiceTimeoutException(
                        "Account Service response timed out", ex);
            }
            throw new AccountServiceUnavailableException(
                    "Account Service is unavailable", ex);
        } catch (RestClientException ex) {
            throw new InvalidAccountResponseException(
                    "Account Service response cannot be processed", ex);
        }
    }

    private AccountLookupResponse accountFallback(
            String accountNumber, Throwable cause) {
        if (cause instanceof RemoteAccountNotFoundException notFound) {
            throw notFound;
        }
        if (cause instanceof InvalidAccountResponseException invalidResponse) {
            throw invalidResponse;
        }
        throw new AccountServiceUnavailableException(
                "Account validation is temporarily unavailable", cause);
    }
}
