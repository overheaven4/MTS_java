package org.example.hw10;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceSchedulerTest {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Test
    void rejectsNonExistentLocalTimeInDstGap() {
        MaintenanceScheduler scheduler = new MaintenanceScheduler(Clock.systemUTC());

        assertThrows(DateTimeException.class, () -> scheduler.nextWindow(
                LocalDate.of(2024, 3, 31),
                LocalTime.of(2, 30),
                Duration.ofMinutes(30),
                BERLIN,
                OverlapPolicy.EARLIER));
    }

    @Test
    void resolvesDstOverlapAccordingToPolicy() {
        MaintenanceScheduler scheduler = new MaintenanceScheduler(Clock.systemUTC());
        LocalDate overlapDate = LocalDate.of(2024, 10, 27);
        LocalTime localStart = LocalTime.of(2, 30);
        Duration duration = Duration.ofMinutes(30);

        MaintenanceWindow earlier = scheduler.nextWindow(overlapDate, localStart, duration, BERLIN, OverlapPolicy.EARLIER);
        MaintenanceWindow later = scheduler.nextWindow(overlapDate, localStart, duration, BERLIN, OverlapPolicy.LATER);

        ZonedDateTime earlierZdt = earlier.start().atZone(BERLIN);
        ZonedDateTime laterZdt = later.start().atZone(BERLIN);

        assertEquals(ZoneOffset.ofHours(2), earlierZdt.getOffset());
        assertEquals(ZoneOffset.ofHours(1), laterZdt.getOffset());
        assertEquals(Duration.ofHours(1), Duration.between(earlier.start(), later.start()));
    }

    @Test
    void schedulesDailyWindowsAcrossSpringForwardWith23HourInstantDelta() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(fixedClock);

        List<MaintenanceWindow> firstCall = scheduler.upcomingWindows(2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);
        List<MaintenanceWindow> secondCall = scheduler.upcomingWindows(2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);

        assertEquals(2, firstCall.size());
        assertEquals(LocalTime.of(3, 30), firstCall.get(0).start().atZone(BERLIN).toLocalTime());
        assertEquals(LocalTime.of(3, 30), firstCall.get(1).start().atZone(BERLIN).toLocalTime());
        assertEquals(LocalDate.of(2024, 3, 30), firstCall.get(0).start().atZone(BERLIN).toLocalDate());
        assertEquals(LocalDate.of(2024, 3, 31), firstCall.get(1).start().atZone(BERLIN).toLocalDate());
        assertEquals(Duration.ofHours(23), Duration.between(firstCall.get(0).start(), firstCall.get(1).start()));
        assertEquals(firstCall, secondCall);
    }

    @Test
    void validatesDaysAndDurationArguments() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(fixedClock);

        assertTrue(scheduler.upcomingWindows(0, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> scheduler.upcomingWindows(-1, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER));
        assertThrows(IllegalArgumentException.class, () -> scheduler.nextWindow(LocalDate.of(2024, 3, 30), LocalTime.of(3, 30), Duration.ZERO, BERLIN, OverlapPolicy.EARLIER));
    }
}