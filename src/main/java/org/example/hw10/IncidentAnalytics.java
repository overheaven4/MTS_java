package org.example.hw10;

import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

public final class IncidentAnalytics {

    private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);

    private static final Comparator<Incident> LONGEST_COMPARATOR = Comparator
            .comparing(Incident::duration).reversed()
            .thenComparing(Incident::startedAt)
            .thenComparing(Incident::id);

    private static final Comparator<Incident> START_ORDER_COMPARATOR = Comparator
            .comparing(Incident::startedAt)
            .thenComparing(Incident::id);

    private IncidentAnalytics() {
    }

    public static Duration totalDowntime(List<Incident> incidents) {
        validateIncidents(incidents);
        Duration total = Duration.ZERO;
        for (Incident incident : incidents) {
            total = total.plus(incident.duration());
        }
        return total;
    }

    public static Duration averageDowntime(List<Incident> incidents) {
        validateIncidents(incidents);
        if (incidents.isEmpty()) {
            return Duration.ZERO;
        }
        BigInteger totalNanos = BigInteger.ZERO;
        for (Incident incident : incidents) {
            Duration duration = incident.duration();
            BigInteger durationNanos = BigInteger.valueOf(duration.getSeconds())
                    .multiply(NANOS_PER_SECOND)
                    .add(BigInteger.valueOf(duration.getNano()));
            totalNanos = totalNanos.add(durationNanos);
        }
        BigInteger averageNanos = totalNanos.divide(BigInteger.valueOf(incidents.size()));
        BigInteger[] secondsAndNanos = averageNanos.divideAndRemainder(NANOS_PER_SECOND);
        return Duration.ofSeconds(secondsAndNanos[0].longValueExact(), secondsAndNanos[1].longValueExact());
    }

    public static Optional<Incident> longestIncident(List<Incident> incidents) {
        validateIncidents(incidents);
        return incidents.stream().min(LONGEST_COMPARATOR);
    }

    public static Map<LocalDate, List<Incident>> groupByStartDate(
            List<Incident> incidents, ZoneId zone) {
        validateIncidents(incidents);
        Objects.requireNonNull(zone, "zone must not be null");
        Map<LocalDate, List<Incident>> grouped = new TreeMap<>();
        for (Incident incident : incidents) {
            LocalDate startDate = incident.startedAt().atZone(zone).toLocalDate();
            grouped.computeIfAbsent(startDate, key -> new ArrayList<>()).add(incident);
        }
        Map<LocalDate, List<Incident>> result = new TreeMap<>();
        for (Map.Entry<LocalDate, List<Incident>> entry : grouped.entrySet()) {
            List<Incident> dayIncidents = entry.getValue();
            dayIncidents.sort(START_ORDER_COMPARATOR);
            result.put(entry.getKey(), List.copyOf(dayIncidents));
        }
        return Collections.unmodifiableMap(result);
    }

    private static void validateIncidents(List<Incident> incidents) {
        Objects.requireNonNull(incidents, "incidents must not be null");
        for (Incident incident : incidents) {
            if (incident == null) {
                throw new IllegalArgumentException("incidents must not contain null elements");
            }
        }
    }
}