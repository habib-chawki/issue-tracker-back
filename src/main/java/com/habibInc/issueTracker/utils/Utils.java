package com.habibInc.issueTracker.utils;

public class Utils {
    public static Long validateId(String id) {
        Long parsedId = Long.valueOf(id);

        return parsedId;
    }
}
