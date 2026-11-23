package org.example.hw10;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MaintenanceScheduler {

    private final Clock clock;

    public MaintenanceScheduler(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public MaintenanceWindow nextWindow(
            LocalDate date, LocalTime localStart, Duration duration,
            ZoneId zone, OverlapPolicy policy) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(localStart, "localStart must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(policy, "policy must not be null");
        validatePositiveDuration(duration);

        LocalDateTime localDateTime = LocalDateTime.of(date, localStart);
        if (zone.getRules().getValidOffsets(localDateTime).isEmpty()) {
            throw new DateTimeException(
                    "Local time " + localDateTime + " does not exist in zone " + zone + " due to DST gap");
        }

        ZonedDateTime base = ZonedDateTime.of(localDateTime, zone);
        ZonedDateTime startZoned = switch (policy) {
            case EARLIER -> base.withEarlierOffsetAtOverlap();
            case LATER -> base.withLaterOffsetAtOverlap();
        };
        Instant start = startZoned.toInstant();
        return new MaintenanceWindow(start, start.plus(duration));
    }

    public List<MaintenanceWindow> upcomingWindows(
            int days, LocalTime localStart, Duration duration,
            ZoneId zone, OverlapPolicy policy) {
        Objects.requireNonNull(localStart, "localStart must not be null");
        Objects.requireNonNull(duration, "duration must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(policy, "policy must not be null");
        validatePositiveDuration(duration);
        if (days < 0) {
            throw new IllegalArgumentException("days must not be negative");
        }
        if (days == 0) {
            return List.of();
        }

        LocalDate startDate = LocalDate.now(clock.withZone(zone));
        List<MaintenanceWindow> windows = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            windows.add(nextWindow(startDate.plusDays(i), localStart, duration, zone, policy));
        }
        return List.copyOf(windows);
    }

    private static void validatePositiveDuration(Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("duration must be positive");
        }
    }
}