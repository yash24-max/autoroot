package com.autoroot.common.mapper;

import com.autoroot.common.dto.LogEntryDto;
import com.autoroot.common.entity.LogEntry;
import org.mapstruct.*;

/**
 * Mapper for LogEntry entity and DTO.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LogEntryMapper {

    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "incident.id", target = "incidentId")
    LogEntryDto toDto(LogEntry logEntry);

    @Mapping(source = "serviceId", target = "service.id")
    @Mapping(source = "incidentId", target = "incident.id")
    LogEntry toEntity(LogEntryDto logEntryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "serviceId", target = "service.id")
    @Mapping(source = "incidentId", target = "incident.id")
    void updateEntityFromDto(LogEntryDto logEntryDto, @MappingTarget LogEntry logEntry);
}
