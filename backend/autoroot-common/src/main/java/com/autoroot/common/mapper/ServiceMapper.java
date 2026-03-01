package com.autoroot.common.mapper;

import com.autoroot.common.dto.ServiceDto;
import com.autoroot.common.entity.Service;
import org.mapstruct.*;

/**
 * Mapper for Service entity and DTO.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceMapper {

    ServiceDto toDto(Service service);

    Service toEntity(ServiceDto serviceDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ServiceDto serviceDto, @MappingTarget Service service);
}