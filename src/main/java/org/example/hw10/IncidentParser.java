package org.example.hw10;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public final class IncidentParser {

    private static final int EXPECTED_FIELDS = 4;

    private IncidentParser() {
    }

    public static Incident parse(String line) {
        Objects.requireNonNull(line, "line must not be null");
        String[] parts = line.split(";", -1);
        if (parts.length != EXPECTED_FIELDS) {
            throw new IllegalArgumentException(
                    "Expected " + EXPECTED_FIELDS + " fields separated by ';', got " + parts.length);
        }
        Instant startedAt = parseInstant(parts[2], "startedAt");
        Instant resolvedAt = parseInstant(parts[3], "resolvedAt");
        return new Incident(parts[0], parts[1], startedAt, resolvedAt);
    }

    private static Instant parseInstant(String value, String fieldName) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " timestamp: " + value, e);
        }
    }
}