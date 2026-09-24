package com.multimatics.bankflow.transfer.exception;
public class TransferRejectedException extends RuntimeException {
    private final String code;
    public TransferRejectedException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String code() { return code; }
}