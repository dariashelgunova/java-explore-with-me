package ru.practicum.view;

import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;

import java.time.LocalDateTime;

public interface EventView {
    Integer getId();

    String getAnnotation();

    Category getCategory();

    int getConfirmedRequests();

    Boolean getAvailable();

    LocalDateTime getCreatedOn();

    String getDescription();

    LocalDateTime getEventDate();

    User getInitiator();

    Location getLocation();

    Boolean getPaid();

    Integer getParticipantLimit();

    LocalDateTime getPublishedOn();

    Boolean getRequestModeration();

    State getState();

    String getTitle();
}
