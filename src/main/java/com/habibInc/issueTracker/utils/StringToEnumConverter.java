package com.habibInc.issueTracker.utils;

import com.habibInc.issueTracker.sprint.SprintStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, SprintStatus> {
    @Override
    public SprintStatus convert(String source) {
        return SprintStatus.valueOf(source.toUpperCase());
    }
}
