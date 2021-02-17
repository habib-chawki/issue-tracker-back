package com.habibInc.issueTracker.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {
    @Test
    public void itShouldValidateId() {
        // given a valid id
        String validId = "100";

        // when validateId() is called
        boolean isValid = Utils.validateId(validId);

        assertThat(isValid).isTrue();
    }
}
