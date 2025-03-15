package com.example.barbershop.service;

import com.example.barbershop.dto.LocationDto;
import com.example.barbershop.mapper.LocationMapper;
import com.example.barbershop.model.Location;
import com.example.barbershop.cache.Cache;
import com.example.barbershop.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private static final String LOCATION_NOT_FOUND = "Location not found";
    private static final String LOCATIONS_CACHE_KEY = "location_key";

    private final LocationRepository locationRepository;
    private final Cache cache;

    public List<LocationDto> findAll() {
        Optional<Object> cachedLocations = cache.get(LOCATIONS_CACHE_KEY);
        if (cachedLocations.isPresent()) {
            return (List<LocationDto>) cachedLocations.get();
        }

        List<LocationDto> locations = locationRepository.findAll().stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());

        cache.put(LOCATIONS_CACHE_KEY, locations);

        return locations;
    }

    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {
        Location location = LocationMapper.toEntity(locationDto);
        Location savedLocation = locationRepository.save(location);
        cache.remove(LOCATIONS_CACHE_KEY);
        return LocationMapper.toDto(savedLocation);
    }

    @Transactional
    public LocationDto updateLocation(Long locationId, LocationDto locationDto) {
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException(LOCATION_NOT_FOUND));

        existingLocation.setName(locationDto.getName());
        existingLocation.setAddress(locationDto.getAddress());

        Location updatedLocation = locationRepository.save(existingLocation);
        cache.remove(LOCATIONS_CACHE_KEY);

        return LocationMapper.toDto(updatedLocation);
    }

    @Transactional
    public void deleteLocation(Long locationId) {
        locationRepository.deleteById(locationId);
        cache.remove(LOCATIONS_CACHE_KEY);
    }
}
