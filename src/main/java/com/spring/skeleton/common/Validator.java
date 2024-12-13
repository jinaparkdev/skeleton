package com.spring.skeleton.common;

import java.time.Instant;

public abstract class Validator {
    public <T> Validator notNullOrEmpty(T value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }

        if (value instanceof String && ((String) value).isEmpty()) {
            throw new IllegalArgumentException(label + " must not be empty");
        }

        return this;
    }

    public Validator ensurePhoneNumber(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Phone number must not be null or empty");
        }

        if (!value.matches("^[0-9]*$")) {
            throw new IllegalArgumentException("Phone number must be numeric");
        }

        return this;
    }

    public Validator ensureEmail(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        if (!value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email must be valid");
        }

        return this;
    }

    public Validator ensureDate(String value) {

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Date must not be null or empty");
        }

        if (!value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format");
        }

        return this;
    }

    public Instant ensureAndGetDate(String value) {

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Date must not be null or empty");
        }

        if (!value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format");
        }

        return Converter.toInstant(value);
    }

    public Validator ensureBoolean(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Boolean must not be null or empty");
        }

        if (!value.matches("^(true|false)$")) {
            throw new IllegalArgumentException("Boolean must be true or false");
        }

        return this;
    }

    public <T> T confirm(T value) {
        return value;
    }
}
