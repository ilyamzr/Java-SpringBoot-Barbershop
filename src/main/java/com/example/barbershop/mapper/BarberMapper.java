package com.example.barbershop.mapper;

import com.example.barbershop.dto.BarberDto;
import com.example.barbershop.model.Barber;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;


@UtilityClass
public class BarberMapper {

    public BarberDto toDto(Barber barber) {
        BarberDto dto = toDtoShallow(barber);
        if (barber.getOfferings() != null) {
            dto.setOfferings(barber.getOfferings().stream()
                    .map(OfferingMapper::toDtoShallow)
                    .collect(Collectors.toSet()));
        }
        if (barber.getLocation() != null) {
            dto.setLocationName(barber.getLocation().getName());
        }
        return dto;
    }

    public BarberDto toDtoShallow(Barber barber) {
        BarberDto dto = new BarberDto();
        dto.setBarberId(barber.getBarberId());
        dto.setName(barber.getName());
        dto.setAvailableDays(barber.getAvailableDays());
        dto.setStartTime(barber.getStartTime());
        dto.setEndTime(barber.getEndTime());
        if (barber.getLocation() != null) {
            dto.setLocationName(barber.getLocation().getName());
        }
        return dto;
    }

    public Barber toEntity(BarberDto dto) {
        Barber barber = new Barber();
        barber.setBarberId(dto.getBarberId());
        barber.setName(dto.getName());
        barber.setAvailableDays(dto.getAvailableDays());
        barber.setStartTime(dto.getStartTime());
        barber.setEndTime(dto.getEndTime());
        return barber;
    }
}