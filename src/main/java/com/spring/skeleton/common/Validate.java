package com.spring.skeleton.common;

public class Validate {
    public <T> Validate notNullOrEmpty(T value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " must not be null");
        }

        if (value instanceof String && ((String) value).isEmpty()) {
            throw new IllegalArgumentException(label + " must not be empty");
        }

        return this;
    }

    public Validate ensurePhoneNumber(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Phone number must not be null or empty");
        }

        if (!value.matches("^[0-9]*$")) {
            throw new IllegalArgumentException("Phone number must be numeric");
        }

        return this;
    }

    public Validate ensureEmail(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        if (!value.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email must be valid");
        }

        return this;
    }

    public Validate ensureDate(String value) {

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Date must not be null or empty");
        }

        if (!value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("Date must be in yyyy-MM-dd format");
        }

        return this;
    }

    public <T> T confirm(T value) {
        return value;
    }
}
