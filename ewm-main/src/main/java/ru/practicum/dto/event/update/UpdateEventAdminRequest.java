package ru.practicum.dto.event.update;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest extends UpdateEventRequest {
    StateAction stateAction;
    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
