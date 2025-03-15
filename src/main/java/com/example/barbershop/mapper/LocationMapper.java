package com.example.barbershop.mapper;

import com.example.barbershop.dto.LocationDto;
import com.example.barbershop.model.Barber;
import com.example.barbershop.model.Location;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {

    public LocationDto toDto(Location location) {
        LocationDto dto = new LocationDto();
        dto.setLocationId(location.getLocationId());
        dto.setName(location.getName());
        dto.setAddress(location.getAddress());
        dto.setBarbers(location.getBarbers().stream()
                .map(Barber::getName)
                .collect(Collectors.toSet()));
        return dto;
    }

    public Location toEntity(LocationDto dto) {
        Location location = new Location();
        location.setLocationId(dto.getLocationId());
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        return location;
    }
}
