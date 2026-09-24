package com.multimatics.bankflow.account.api;
import com.multimatics.bankflow.account.exception.*; import jakarta.servlet.http.HttpServletRequest; import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(AccountNotFoundException.class) ResponseEntity<ApiError> notFound(AccountNotFoundException e,HttpServletRequest r){return response(HttpStatus.NOT_FOUND,"ACCOUNT_NOT_FOUND",e.getMessage(),r,List.of());}
 @ExceptionHandler(DuplicateAccountException.class) ResponseEntity<ApiError> duplicate(DuplicateAccountException e,HttpServletRequest r){return response(HttpStatus.CONFLICT,"DUPLICATE_ACCOUNT",e.getMessage(),r,List.of());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> invalid(MethodArgumentNotValidException e,HttpServletRequest r){List<FieldViolation> f=e.getBindingResult().getFieldErrors().stream().map(x->new FieldViolation(x.getField(),x.getDefaultMessage())).toList();return response(HttpStatus.BAD_REQUEST,"VALIDATION_FAILED","Request validation failed",r,f);}
 private ResponseEntity<ApiError> response(HttpStatus s,String c,String m,HttpServletRequest r,List<FieldViolation> f){return ResponseEntity.status(s).body(new ApiError(Instant.now(),s.value(),c,m,r.getRequestURI(),f));}
}
