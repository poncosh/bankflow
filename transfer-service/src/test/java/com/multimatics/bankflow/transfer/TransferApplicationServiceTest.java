package com.multimatics.bankflow.transfer;

import com.multimatics.bankflow.transfer.api.CreateTransferRequest;
import com.multimatics.bankflow.transfer.application.TransferApplicationService;
import com.multimatics.bankflow.transfer.domain.TransferStatus;
import com.multimatics.bankflow.transfer.exception.TransferRejectedException;
import com.multimatics.bankflow.transfer.integration.account.AccountClient;
import com.multimatics.bankflow.transfer.integration.account.AccountLookupResponse;
import com.multimatics.bankflow.transfer.messaging.TransferCompletedEvent;
import com.multimatics.bankflow.transfer.messaging.TransferEventPublisher;
import com.multimatics.bankflow.transfer.repository.TransferRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransferApplicationServiceTest {

    @Test
    void savesCompletedTransferBeforePublishingEvent() {
        AccountClient accountClient = mock(AccountClient.class);
        TransferRepository transferRepository = mock(TransferRepository.class);
        TransferEventPublisher eventPublisher = mock(TransferEventPublisher.class);
        when(accountClient.findByAccountNumber("100000000001"))
                .thenReturn(activeAccount("100000000001", "100000.00"));
        when(accountClient.findByAccountNumber("100000000002"))
                .thenReturn(activeAccount("100000000002", "25000.00"));
        when(transferRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferApplicationService service = new TransferApplicationService(
                transferRepository, accountClient, eventPublisher);
        CreateTransferRequest request = new CreateTransferRequest(
                "100000000001",
                "100000000002",
                new BigDecimal("50000.00"),
                "IDR",
                "LAB-001");

        var response = service.create(request);

        assertNotNull(response.id());
        assertNotNull(response.createdAt());
        assertEquals(TransferStatus.COMPLETED, response.status());
        assertEquals("********0001", response.sourceAccount());
        assertEquals("********0002", response.destinationAccount());
        assertEquals(new BigDecimal("50000.00"), response.amount());
        assertEquals("IDR", response.currency());
        assertEquals("LAB-001", response.clientReference());
        verify(accountClient).findByAccountNumber("100000000001");
        verify(accountClient).findByAccountNumber("100000000002");

        ArgumentCaptor<TransferCompletedEvent> eventCaptor =
                ArgumentCaptor.forClass(TransferCompletedEvent.class);
        InOrder inOrder = org.mockito.Mockito.inOrder(transferRepository, eventPublisher);
        inOrder.verify(transferRepository).save(any());
        inOrder.verify(eventPublisher).publishTransferCompleted(eventCaptor.capture());

        TransferCompletedEvent event = eventCaptor.getValue();
        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
        assertEquals("TransferCompleted", event.eventType());
        assertEquals(1, event.eventVersion());
        assertEquals(response.id(), event.transferId());
        assertEquals("100000000001", event.sourceAccount());
        assertEquals("100000000002", event.destinationAccount());
        assertEquals(new BigDecimal("50000.00"), event.amount());
        assertEquals("IDR", event.currency());
        assertEquals("COMPLETED", event.status());
    }

    @Test
    void doesNotSaveOrPublishWhenAccountValidationFails() {
        AccountClient accountClient = mock(AccountClient.class);
        TransferRepository transferRepository = mock(TransferRepository.class);
        TransferEventPublisher eventPublisher = mock(TransferEventPublisher.class);
        when(accountClient.findByAccountNumber("100000000001"))
                .thenReturn(activeAccount("100000000001", "1000.00"));
        when(accountClient.findByAccountNumber("100000000002"))
                .thenReturn(activeAccount("100000000002", "25000.00"));
        var service = new TransferApplicationService(
                transferRepository, accountClient, eventPublisher);
        var request = new CreateTransferRequest(
                "100000000001",
                "100000000002",
                new BigDecimal("50000.00"),
                "IDR",
                "LAB-002");

        TransferRejectedException exception = assertThrows(
                TransferRejectedException.class, () -> service.create(request));

        assertEquals("INSUFFICIENT_BALANCE", exception.code());
        verifyNoInteractions(transferRepository, eventPublisher);
    }

    private AccountLookupResponse activeAccount(String accountNumber, String balance) {
        return new AccountLookupResponse(
                accountNumber,
                "SAVINGS",
                new BigDecimal(balance),
                "IDR",
                "ACTIVE");
    }
}
