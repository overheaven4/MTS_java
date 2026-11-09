package org.example;

import java.util.Objects;

public final class ValidationError {

    private final String fieldName;
    private final String annotationName;
    private final String message;

    public ValidationError(String fieldName, String annotationName, String message) {
        if (fieldName == null || annotationName == null || message == null) {
            throw new NullPointerException("ValidationError arguments cannot be null");
        }
        this.fieldName = fieldName;
        this.annotationName = annotationName;
        this.message = message;
    }

    public String fieldName() {
        return fieldName;
    }

    public String annotationName() {
        return annotationName;
    }

    public String message() {
        return message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ValidationError that = (ValidationError) o;
        return Objects.equals(fieldName, that.fieldName)
                && Objects.equals(annotationName, that.annotationName)
                && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, annotationName, message);
    }

    @Override
    public String toString() {
        return "ValidationError{fieldName='" + fieldName + "', annotationName='"
                + annotationName + "', message='" + message + "'}";
    }
}