package com.multimatics.bankflow.transfer.application;

import com.multimatics.bankflow.transfer.api.*;
import com.multimatics.bankflow.transfer.domain.*;
import com.multimatics.bankflow.transfer.exception.TransferNotFoundException;
import com.multimatics.bankflow.transfer.exception.TransferRejectedException;
import com.multimatics.bankflow.transfer.integration.account.AccountLookupResponse;
import com.multimatics.bankflow.transfer.integration.account.RemoteAccountNotFoundException;
import com.multimatics.bankflow.transfer.messaging.TransferCompletedEvent;
import com.multimatics.bankflow.transfer.messaging.TransferEventPublisher;
import com.multimatics.bankflow.transfer.repository.TransferRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import com.multimatics.bankflow.transfer.integration.account.AccountClient;


@Service public class TransferApplicationService {
 private final TransferRepository transferRepository;
 private final AccountClient accountClient;
 private final TransferEventPublisher eventPublisher;
 public TransferApplicationService(TransferRepository transferRepository,
                                   AccountClient accountClient,
                                   TransferEventPublisher eventPublisher) {
  this.transferRepository = transferRepository;
  this.accountClient = accountClient;
  this.eventPublisher = eventPublisher;
 }

 public TransferResponse get(UUID id){
  return toResponse(transferRepository.findById(id).orElseThrow(()->
          new TransferNotFoundException("Transfer not found"))
  );
 }
 public List<TransferResponse> findAll(){
  return transferRepository.findAll().stream().map(this::toResponse).toList();
 }

 private String mask(String n)
 {
  return "*".repeat(Math.max(0,n.length()-4))+n.substring(n.length()-4);
 }

 private TransferResponse toResponse(Transfer t)
 {
  return new TransferResponse(
          t.id(),
          mask(t.sourceAccount()),
          mask(t.destinationAccount()),
          t.amount(),
          t.currency(),
          t.clientReference(),
          t.status(),
          t.createdAt()
  );
 }



 public TransferResponse create(CreateTransferRequest request) {
  if (request.sourceAccount().equals(request.destinationAccount())) {
   throw new TransferRejectedException(
           "SAME_ACCOUNT", "Source and destination must be different");
  }
  AccountLookupResponse source;
  AccountLookupResponse destination;
  try {
   source = accountClient.findByAccountNumber(request.sourceAccount());
  } catch (RemoteAccountNotFoundException ex) {
   throw new TransferRejectedException(
           "SOURCE_ACCOUNT_NOT_FOUND", "Source account was not found");
  }
  try {
   destination = accountClient.findByAccountNumber(request.destinationAccount());
  } catch (RemoteAccountNotFoundException ex) {
   throw new TransferRejectedException(
           "DESTINATION_ACCOUNT_NOT_FOUND", "Destination account was not found");
  }
  requireActive(source, "SOURCE_ACCOUNT_NOT_ACTIVE");
  requireActive(destination, "DESTINATION_ACCOUNT_NOT_ACTIVE");
  if (!source.currency().equals(request.currency()) ||
          !destination.currency().equals(request.currency())) {
   throw new TransferRejectedException(
           "CURRENCY_MISMATCH", "Transfer currency does not match both accounts");
  }
  if (source.balance().compareTo(request.amount()) < 0) {
   throw new TransferRejectedException(
           "INSUFFICIENT_BALANCE", "Source balance is insufficient");
  }
  Transfer transfer = new Transfer(
          UUID.randomUUID(), request.sourceAccount(), request.destinationAccount(),
          request.amount(), request.currency(), request.clientReference(),
          TransferStatus.VALIDATING, Instant.now());
  transfer = new Transfer(
          transfer.id(), transfer.sourceAccount(), transfer.destinationAccount(),
          transfer.amount(), transfer.currency(), transfer.clientReference(),
          TransferStatus.COMPLETED, transfer.createdAt());
  Transfer saved = transferRepository.save(transfer);
  var event = new TransferCompletedEvent(
          UUID.randomUUID(), "TransferCompleted", 1, Instant.now(),
          saved.id(), saved.sourceAccount(), saved.destinationAccount(),
          saved.amount(), saved.currency(), saved.status().name());
  eventPublisher.publishTransferCompleted(event);
  return toResponse(saved);
 }


 private void requireActive(AccountLookupResponse account, String code) {
  if (!"ACTIVE".equals(account.status())) {
   throw new TransferRejectedException(code, "Account is not active");
  }
 }
}
