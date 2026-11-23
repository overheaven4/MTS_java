package org.example.hw10;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record MaintenanceWindow(Instant start, Instant end) {

    public MaintenanceWindow {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end must be strictly after start");
        }
    }

    public Duration duration() {
        return Duration.between(start, end);
    }
}