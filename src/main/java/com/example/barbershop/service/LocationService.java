package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.LocationDto;
import com.example.barbershop.mapper.LocationMapper;
import com.example.barbershop.model.Location;
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
    private static final String ALL_LOCATIONS_CACHE_KEY = "all_locations";
    private static final String LOCATION_CACHE_KEY_PREFIX = "location_";

    private final LocationRepository locationRepository;
    private final Cache cache;

    public List<LocationDto> findAll() {
        Optional<Object> cachedLocations = cache.get(ALL_LOCATIONS_CACHE_KEY);
        if (cachedLocations.isPresent()) {
            return (List<LocationDto>) cachedLocations.get();
        }

        List<LocationDto> locations = locationRepository.findAll().stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());

        cache.put(ALL_LOCATIONS_CACHE_KEY, locations);

        return locations;
    }

    public Optional<LocationDto> findById(Long id) {
        String cacheKey = LOCATION_CACHE_KEY_PREFIX + id;

        Optional<Object> cachedLocation = cache.get(cacheKey);
        if (cachedLocation.isPresent()) {
            return Optional.of((LocationDto) cachedLocation.get());
        }

        Optional<LocationDto> locationDto = locationRepository.findById(id)
                .map(LocationMapper::toDto);

        locationDto.ifPresent(dto -> cache.put(cacheKey, dto));

        return locationDto;
    }

    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {
        Location location = LocationMapper.toEntity(locationDto);
        Location savedLocation = locationRepository.save(location);
        LocationDto savedDto = LocationMapper.toDto(savedLocation);

        String cacheKey = LOCATION_CACHE_KEY_PREFIX + savedLocation.getLocationId();
        cache.put(cacheKey, savedDto);

        cache.remove(ALL_LOCATIONS_CACHE_KEY);

        return savedDto;
    }

    @Transactional
    public LocationDto updateLocation(Long locationId, LocationDto locationDto) {
        Location existingLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException(LOCATION_NOT_FOUND));

        existingLocation.setName(locationDto.getName());
        existingLocation.setAddress(locationDto.getAddress());

        Location updatedLocation = locationRepository.save(existingLocation);
        LocationDto updatedDto = LocationMapper.toDto(updatedLocation);

        String cacheKey = LOCATION_CACHE_KEY_PREFIX + locationId;
        cache.put(cacheKey, updatedDto);

        cache.remove(ALL_LOCATIONS_CACHE_KEY);

        return updatedDto;
    }

    @Transactional
    public void deleteLocation(Long locationId) {
        locationRepository.deleteById(locationId);

        String cacheKey = LOCATION_CACHE_KEY_PREFIX + locationId;
        cache.remove(cacheKey);

        cache.remove(ALL_LOCATIONS_CACHE_KEY);
    }
}