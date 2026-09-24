package com.multimatics.bankflow.transfer.api;

import com.multimatics.bankflow.transfer.application.TransferApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api/v1/transfers") public class TransferController {
 private final TransferApplicationService service;
 public TransferController(TransferApplicationService service){
  this.service=service;
 }
 @PostMapping ResponseEntity<TransferResponse> create(@Valid @RequestBody CreateTransferRequest r)
 {
  return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));
 }
 @GetMapping("/{id}") TransferResponse get(@PathVariable UUID id)
 {
  return service.get(id);
 }
 @GetMapping List<TransferResponse> findAll(){
  return service.findAll();
 }
}
