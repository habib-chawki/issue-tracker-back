package com.habibInc.issueTracker.sprint;

import org.springframework.core.convert.converter.Converter;

public class SprintStatusConverter implements Converter<String, SprintStatus> {
    @Override
    public SprintStatus convert(String source) {
        return SprintStatus.valueOf(source.toUpperCase());
    }
}
