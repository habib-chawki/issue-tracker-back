package com.habibInc.issueTracker.utils.validation;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;

public class IdValidator {
    public final static String errorMessage = "Invalid id";

    public static Long validate(String id) throws InvalidIdException {
        Long parsedId;
        try{
            parsedId = Long.valueOf(id);
        }catch (Exception ex){
            throw new InvalidIdException(errorMessage);
        }
        return parsedId;
    }
}
