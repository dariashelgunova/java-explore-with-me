package ru.practicum.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {

    Integer id;
    String app;
    String uri;
    String ip;
    String timestamp;
}
