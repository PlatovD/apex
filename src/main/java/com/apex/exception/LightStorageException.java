package com.apex.exception;

public class LightStorageException extends RuntimeException {
    public LightStorageException(String message) {
        super(message);
    }

    public LightStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}