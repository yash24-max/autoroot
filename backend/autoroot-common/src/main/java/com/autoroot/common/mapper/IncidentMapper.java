package com.autoroot.common.mapper;

import com.autoroot.common.dto.IncidentDto;
import com.autoroot.common.entity.Incident;
import org.mapstruct.*;

/**
 * Mapper for Incident entity and DTO.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IncidentMapper {

    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    IncidentDto toDto(Incident incident);

    @Mapping(source = "serviceId", target = "service.id")
    Incident toEntity(IncidentDto incidentDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "serviceId", target = "service.id")
    void updateEntityFromDto(IncidentDto incidentDto, @MappingTarget Incident incident);
}