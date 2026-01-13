package com.apex.exception;

public class NoActiveCameraException extends RuntimeException {
    public NoActiveCameraException(String message) {
        super(message);
    }
}
