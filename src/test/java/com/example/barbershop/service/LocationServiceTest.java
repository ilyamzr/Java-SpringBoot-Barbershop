package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.LocationDto;
import com.example.barbershop.model.Location;
import com.example.barbershop.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private LocationService locationService;

    private Location location;
    private LocationDto locationDto;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setLocationId(1L);
        location.setName("Test Location");

        locationDto = new LocationDto();
        locationDto.setLocationId(1L);
        locationDto.setName("Test Location");
    }

    @Test
    void findAll_cached() {
        when(cache.get("all_locations")).thenReturn(Optional.of(List.of(locationDto)));
        List<LocationDto> result = locationService.findAll();
        assertEquals(1, result.size());
        verify(locationRepository, never()).findAll();
    }

    @Test
    void findAll_notCached() {
        when(cache.get("all_locations")).thenReturn(Optional.empty());
        when(locationRepository.findAll()).thenReturn(List.of(location));
        List<LocationDto> result = locationService.findAll();
        assertEquals(1, result.size());
        verify(cache).put("all_locations", result);
    }

    @Test
    void findById_cached() {
        when(cache.get("location_1")).thenReturn(Optional.of(locationDto));
        Optional<LocationDto> result = locationService.findById(1L);
        assertTrue(result.isPresent());
        verify(locationRepository, never()).findById(anyLong());
    }

    @Test
    void findById_notCached_found() {
        when(cache.get("location_1")).thenReturn(Optional.empty());
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        Optional<LocationDto> result = locationService.findById(1L);
        assertTrue(result.isPresent());
        verify(cache).put("location_1", result.get());
    }

    @Test
    void findById_notFound() {
        when(cache.get("location_1")).thenReturn(Optional.empty());
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<LocationDto> result = locationService.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void createLocation() {
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        LocationDto result = locationService.createLocation(locationDto);
        assertEquals(locationDto.getName(), result.getName());
        verify(cache).put("location_1", result);
        verify(cache).remove("all_locations");
    }

    @Test
    void updateLocation_success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        LocationDto result = locationService.updateLocation(1L, locationDto);
        assertEquals(locationDto.getName(), result.getName());
        verify(cache).put("location_1", result);
        verify(cache).remove("all_locations");
    }

    @Test
    void updateLocation_notFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> locationService.updateLocation(1L, locationDto));
    }

    @Test
    void deleteLocation() {
        locationService.deleteLocation(1L);
        verify(locationRepository).deleteById(1L);
        verify(cache).remove("location_1");
        verify(cache).remove("all_locations");
    }
}