package org.example.hw10;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentAnalyticsTest {

    @Test
    void handlesEmptyList() {
        List<Incident> empty = List.of();

        assertEquals(Duration.ZERO, IncidentAnalytics.totalDowntime(empty));
        assertEquals(Duration.ZERO, IncidentAnalytics.averageDowntime(empty));
        assertEquals(Optional.empty(), IncidentAnalytics.longestIncident(empty));
        assertTrue(IncidentAnalytics.groupByStartDate(empty, ZoneId.of("Europe/Berlin")).isEmpty());
    }

    @Test
    void computesTotalAverageWithFloorRoundingAndLongestWithTieBreakers() {
        Instant base = Instant.parse("2024-03-30T10:00:00Z");
        Incident inc1 = new Incident("INC-2", "auth", base, base.plusNanos(5));
        Incident inc2 = new Incident("INC-1", "auth", base, base.plusNanos(5));
        Incident inc3 = new Incident("INC-3", "billing", base.plusSeconds(10), base.plusSeconds(10).plusNanos(5));
        Incident inc4 = new Incident("INC-4", "gateway", base.minusSeconds(5), base.minusSeconds(5).plusNanos(2));

        List<Incident> incidents = new ArrayList<>(List.of(inc1, inc2, inc3, inc4));
        List<Incident> snapshot = List.copyOf(incidents);

        assertEquals(Duration.ofNanos(17), IncidentAnalytics.totalDowntime(incidents));
        assertEquals(Duration.ofNanos(4), IncidentAnalytics.averageDowntime(incidents));
        assertEquals(Optional.of(inc2), IncidentAnalytics.longestIncident(incidents));
        assertEquals(snapshot, incidents);
    }

    @Test
    void averagesVeryLongDurationsWithoutLongOverflow() {
        Duration twoHundredYears = Duration.ofDays(365L * 200);
        Instant start = Instant.parse("2000-01-01T00:00:00Z");
        Incident inc1 = new Incident("INC-1", "core", start, start.plus(twoHundredYears));
        Incident inc2 = new Incident("INC-2", "core", start, start.plus(twoHundredYears).plusNanos(1));

        assertEquals(twoHundredYears, IncidentAnalytics.averageDowntime(List.of(inc1, inc2)));
    }

    @Test
    void groupsByStartDateInTargetZoneAndSortsGroupsAndItems() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");
        Incident inc1 = IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z");
        Incident inc2 = IncidentParser.parse("INC-2;gateway;2024-03-30T23:30:00Z;2024-03-31T00:15:00Z");
        Incident inc3 = IncidentParser.parse("INC-0;gateway;2024-03-30T23:30:00Z;2024-03-31T00:10:00Z");

        Map<LocalDate, List<Incident>> grouped =
                IncidentAnalytics.groupByStartDate(List.of(inc2, inc1, inc3), berlin);

        assertEquals(
                List.of(LocalDate.of(2024, 3, 30), LocalDate.of(2024, 3, 31)),
                List.copyOf(grouped.keySet()));
        assertEquals(List.of(inc1), grouped.get(LocalDate.of(2024, 3, 30)));
        assertEquals(List.of(inc3, inc2), grouped.get(LocalDate.of(2024, 3, 31)));
    }
}