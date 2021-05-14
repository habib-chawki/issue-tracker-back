package com.habibInc.issueTracker.utils.validation;

import com.habibInc.issueTracker.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return !userRepository.existsByEmail(email);
    }

}
