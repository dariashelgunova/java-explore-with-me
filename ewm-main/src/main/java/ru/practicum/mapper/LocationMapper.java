package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.Location;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {
    Location fromDto(LocationDto dto);
    LocationDto toDto(Location location);
}
