package com.multimatics.bankflow.notification.reliability;
public class TransientNotificationException extends RuntimeException {
    public TransientNotificationException(String message) { super(message); }
}