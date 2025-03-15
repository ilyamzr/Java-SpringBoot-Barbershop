package com.example.barbershop.service;

import com.example.barbershop.dto.LocationDto;
import com.example.barbershop.mapper.LocationMapper;
import com.example.barbershop.model.Location;
import com.example.barbershop.repository.LocationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private static final String LOCATION_NOT_FOUND = "Location not found";

    private final LocationRepository locationRepository;

    public List<LocationDto> findAll() {
        return locationRepository.findAll().stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {
        Location location = LocationMapper.toEntity(locationDto);
        Location savedLocation = locationRepository.save(location);
        return LocationMapper.toDto(savedLocation);
    }

    @Transactional
    public LocationDto updateLocation(Long locationId, LocationDto locationDto) {
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException(LOCATION_NOT_FOUND));

        existingLocation.setName(locationDto.getName());
        existingLocation.setAddress(locationDto.getAddress());

        Location updatedLocation = locationRepository.save(existingLocation);
        return LocationMapper.toDto(updatedLocation);
    }

    @Transactional
    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new RuntimeException(LOCATION_NOT_FOUND);
        }
        locationRepository.deleteById(locationId);
    }
}
