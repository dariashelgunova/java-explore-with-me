package ru.practicum.model.enums;

import ru.practicum.exception.InvalidEventSortException;

import java.util.Arrays;
import java.util.Optional;

public enum SortEnum {
    EVENT_DATE,
    VIEWS;

    public static SortEnum findByValueOrThrowException(String value) {
        Optional<SortEnum> sort = Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(value))
                .findFirst();
        return sort.orElseThrow(() -> new InvalidEventSortException("Unknown sort: UNSUPPORTED_STATUS"));
    }
}
