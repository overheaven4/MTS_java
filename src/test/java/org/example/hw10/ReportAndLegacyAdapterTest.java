package org.example.hw10;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportAndLegacyAdapterTest {

    @Test
    void formatsMaintenanceWindowReportExactly() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");
        MaintenanceWindow window = new MaintenanceWindow(Instant.parse("2024-03-31T01:00:00Z"), Instant.parse("2024-03-31T02:00:00Z"));

        String formatted = ReportFormatter.formatWindow(window, berlin, Locale.GERMANY);

        assertEquals("31.03.2024 03:00 +02:00 (Europe/Berlin) \u2013 31.03.2024 04:00 +02:00 (Europe/Berlin)", formatted);
    }

    @Test
    void preservesInstantInDateRoundTrip() {
        Instant original = Instant.parse("2024-03-31T01:23:45.678Z");
        Date legacyDate = LegacyDateAdapter.toDate(original);
        Instant restored = LegacyDateAdapter.toInstant(legacyDate);

        assertEquals(original, restored);
        assertEquals(legacyDate, LegacyDateAdapter.toDate(restored));
    }

    @Test
    void preservesInstantAndZoneInGregorianCalendarRoundTrip() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");
        ZonedDateTime original = ZonedDateTime.of(2024, 3, 31, 3, 30, 0, 0, berlin);

        GregorianCalendar calendar = LegacyDateAdapter.toGregorianCalendar(original);
        ZonedDateTime restored = LegacyDateAdapter.toZonedDateTime(calendar);

        assertEquals(original.toInstant(), restored.toInstant());
        assertEquals(berlin, restored.getZone());
    }
}