package com.habibInc.issueTracker.utils;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;

public class Utils {
    public static Long validateId(String id) {
        Long parsedId;
        try{
            parsedId = Long.valueOf(id);
        }catch (Exception ex){
            throw new InvalidIdException("Invalid id");
        }
        return parsedId;
    }
}
