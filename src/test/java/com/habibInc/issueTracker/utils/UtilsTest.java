package com.habibInc.issueTracker.utils;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.utils.validation.IdValidator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UtilsTest {
    @Test
    public void itShouldValidateId() {
        // given a valid id
        String validId = "100";

        // when validateId() is called
        Long parsedId = IdValidator.validate(validId);

        assertThat(parsedId).isEqualTo(100L);
    }

    @Test
    public void whenIdIsInvalid_itShouldThrowInvalidIdError() {
        // given an invalid id
        String invalidId = "invalid";

        // when validateId() is invoked
        assertThatExceptionOfType(InvalidIdException.class)
                .isThrownBy(() -> IdValidator.validate(invalidId));
    }
}
