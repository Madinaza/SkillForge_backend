package com.skillforge.backend.exception;

public class TwoFaRequiredException extends RuntimeException {
    public TwoFaRequiredException(String msg) {
        super(msg);
    }
}