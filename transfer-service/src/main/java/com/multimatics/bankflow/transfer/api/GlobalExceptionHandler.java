package com.multimatics.bankflow.transfer.api;

import com.multimatics.bankflow.transfer.exception.TransferRejectedException;
import com.multimatics.bankflow.transfer.integration.account.AccountServiceTimeoutException;
import com.multimatics.bankflow.transfer.integration.account.AccountServiceUnavailableException;
import com.multimatics.bankflow.transfer.integration.account.InvalidAccountResponseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.Instant;

@RestControllerAdvice public class GlobalExceptionHandler {
 private ResponseEntity<ApiError> response(
         HttpStatus s,
         String c,
         String m,
         HttpServletRequest r,
         List<FieldViolation> f
 ){
  return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),c,m,r.getRequestURI(),f));
 }

 @ExceptionHandler(TransferRejectedException.class)
 ResponseEntity<ApiError> rejected(TransferRejectedException ex,
                                   HttpServletRequest request) {
  return response(HttpStatus.UNPROCESSABLE_ENTITY, ex.code(),
          ex.getMessage(), request, List.of());
 }
 @ExceptionHandler(AccountServiceTimeoutException.class)
 ResponseEntity<ApiError> timeout(AccountServiceTimeoutException ex,
                                  HttpServletRequest request) {
  return response(HttpStatus.GATEWAY_TIMEOUT, "ACCOUNT_SERVICE_TIMEOUT",
          "Account validation timed out", request, List.of());
 }
 @ExceptionHandler(AccountServiceUnavailableException.class)
 ResponseEntity<ApiError> unavailable(AccountServiceUnavailableException ex,
                                      HttpServletRequest request) {
  return response(HttpStatus.SERVICE_UNAVAILABLE,
          "ACCOUNT_SERVICE_UNAVAILABLE",
          "Account validation is temporarily unavailable", request, List.of());
 }
 @ExceptionHandler(InvalidAccountResponseException.class)
 ResponseEntity<ApiError> invalidDownstream(InvalidAccountResponseException ex,
                                            HttpServletRequest request) {
  return response(HttpStatus.BAD_GATEWAY, "INVALID_DOWNSTREAM_RESPONSE",
          "Account validation returned an invalid response", request, List.of());
 }
}
