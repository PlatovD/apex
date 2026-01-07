package com.apex.math.exceptions;

public class MathException extends RuntimeException {
    public MathException(String message) {
        super(message);
    }

    public MathException(String message, Throwable cause) {
        super(message, cause);
    }
}
