package com.spring.skeleton.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Converter {
    public static Instant toInstant(String value) {
        if (!value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(value, formatter);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
