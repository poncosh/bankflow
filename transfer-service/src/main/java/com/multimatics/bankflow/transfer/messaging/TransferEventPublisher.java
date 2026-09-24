package com.multimatics.bankflow.transfer.messaging;
public interface TransferEventPublisher {
    void publishTransferCompleted(TransferCompletedEvent event);
}