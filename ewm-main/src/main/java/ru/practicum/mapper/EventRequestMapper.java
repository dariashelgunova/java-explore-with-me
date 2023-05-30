package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.eventrequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventrequest.participation.ParticipationRequestDto;
import ru.practicum.model.EventRequest;
import ru.practicum.model.enums.Status;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventRequestMapper {

    @Mapping(target = "created", source = "createdOn")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "user.id")
    ParticipationRequestDto toParticipationDto(EventRequest eventRequest);

    List<ParticipationRequestDto> toParticipationDtoList(List<EventRequest> eventRequests);

    default EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<EventRequest> requests) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        for (EventRequest request : requests) {
            if (request.getStatus().equals(Status.CONFIRMED)) {
                confirmedRequests.add(toParticipationDto(request));
            } else {
                rejectedRequests.add(toParticipationDto(request));
            }
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

}
