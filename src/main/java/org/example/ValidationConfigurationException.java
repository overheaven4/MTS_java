package org.example;

public class ValidationConfigurationException extends RuntimeException {

    public ValidationConfigurationException(String message) {
        super(message);
    }

    public ValidationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}