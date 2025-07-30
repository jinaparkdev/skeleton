package com.spring.skeleton.common;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class OptNotBlankTest {

    private OptNotBlank.Validator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new OptNotBlank.Validator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void testStringValidation() {
        assertTrue(validator.isValid(null, context)); // null 값은 유효
        assertTrue(validator.isValid("string", context)); // 유효한 문자열
        assertFalse(validator.isValid("      ", context)); // 공백 문자열
    }
}
