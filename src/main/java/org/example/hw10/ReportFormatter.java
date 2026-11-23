package org.example.hw10;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class ReportFormatter {

    private static final String PATTERN = "dd.MM.uuuu HH:mm XXX (VV)";
    private static final String SEPARATOR = " \u2013 ";

    private ReportFormatter() {
    }

    public static String formatWindow(MaintenanceWindow window, ZoneId zone, Locale locale) {
        Objects.requireNonNull(window, "window must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN, locale);
        ZonedDateTime start = window.start().atZone(zone);
        ZonedDateTime end = window.end().atZone(zone);
        return formatter.format(start) + SEPARATOR + formatter.format(end);
    }
}