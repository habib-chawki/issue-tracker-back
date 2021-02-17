package com.habibInc.issueTracker.utils;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;

public class Utils {
    public final static String errorMessage = "Invalid id";

    public static Long validateId(String id) throws InvalidIdException {
        Long parsedId;
        try{
            parsedId = Long.valueOf(id);
        }catch (Exception ex){
            throw new InvalidIdException(errorMessage);
        }
        return parsedId;
    }
}
