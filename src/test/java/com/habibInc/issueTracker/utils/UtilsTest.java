package com.habibInc.issueTracker.utils;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UtilsTest {
    @Test
    public void itShouldValidateId() {
        // given a valid id
        String validId = "100";

        // when validateId() is called
        Long parsedId = Utils.validateId(validId);

        assertThat(parsedId).isEqualTo(100L);
    }

    @Test
    public void whenIdIsInvalid_itShouldThrowInvalidIdError() {
        // given an invalid id
        String invalidId = "invalid";

        // when validateId() is invoked
        assertThatExceptionOfType(InvalidIdException.class)
                .isThrownBy(() -> Utils.validateId(invalidId));
    }
}
