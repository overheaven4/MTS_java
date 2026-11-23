package org.example.hw10;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentAndParserTest {

    @Test
    void parsesValidLineAndComputesDuration() {
        Incident incident = IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z");

        assertEquals("INC-1", incident.id());
        assertEquals("billing", incident.service());
        assertEquals(Instant.parse("2024-03-30T22:30:00Z"), incident.startedAt());
        assertEquals(Instant.parse("2024-03-31T01:00:00Z"), incident.resolvedAt());
        assertEquals(Duration.ofMinutes(150), incident.duration());
    }

    @Test
    void rejectsInvalidTimestamp() {
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("INC-1;billing;2024-03-30 22:30:00;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("INC-1;billing;not-a-timestamp;2024-03-31T01:00:00Z"));
    }

    @Test
    void rejectsEmptyOrBlankId() {
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse(";billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("   ;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
    }

    @Test
    void rejectsResolvedBeforeStarted() {
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("INC-1;billing;2024-03-31T01:00:00Z;2024-03-30T22:30:00Z"));
    }

    @Test
    void rejectsWrongFieldCount() {
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z"));
        assertThrows(IllegalArgumentException.class, () ->
                IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z;"));
    }
}