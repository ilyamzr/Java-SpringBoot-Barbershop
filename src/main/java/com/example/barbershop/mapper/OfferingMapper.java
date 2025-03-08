package com.example.barbershop.mapper;

import com.example.barbershop.dto.OfferingDto;
import com.example.barbershop.model.Offering;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OfferingMapper {

    public OfferingDto toDto(Offering offering) {
        OfferingDto dto = toDtoShallow(offering);
        if (offering.getBarbers() != null) {
            dto.setBarbers(offering.getBarbers().stream()
                    .map(BarberMapper::toDtoShallow)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }

    public OfferingDto toDtoShallow(Offering offering) {
        OfferingDto dto = new OfferingDto();
        dto.setServiceId(offering.getServiceId());
        dto.setName(offering.getName());
        dto.setPrice(offering.getPrice());
        dto.setDuration(offering.getDuration());
        dto.setDescription(offering.getDescription());
        return dto;
    }

    public Offering toEntity(OfferingDto dto) {
        Offering offering = new Offering();
        offering.setServiceId(dto.getServiceId());
        offering.setName(dto.getName());
        offering.setPrice(dto.getPrice());
        offering.setDuration(dto.getDuration());
        offering.setDescription(dto.getDescription());
        return offering;
    }
}