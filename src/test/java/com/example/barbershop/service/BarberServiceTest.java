package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.BarberDto;
import com.example.barbershop.model.Barber;
import com.example.barbershop.model.Location;
import com.example.barbershop.model.Offering;
import com.example.barbershop.repository.BarberRepository;
import com.example.barbershop.repository.LocationRepository;
import com.example.barbershop.repository.OfferingRepository;
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
class BarberServiceTest {

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private BarberService barberService;

    private Barber barber;
    private BarberDto barberDto;

    @BeforeEach
    void setUp() {
        barber = new Barber();
        barber.setBarberId(1L);
        barber.setName("Test Barber");

        barberDto = new BarberDto();
        barberDto.setBarberId(1L);
        barberDto.setName("Test Barber");
    }

    @Test
    void findAll_cached() {
        when(cache.get("all_barbers")).thenReturn(Optional.of(List.of(barberDto)));
        List<BarberDto> result = barberService.findAll();
        assertEquals(1, result.size());
        verify(barberRepository, never()).findAll();
    }

    @Test
    void findAll_notCached() {
        when(cache.get("all_barbers")).thenReturn(Optional.empty());
        when(barberRepository.findAll()).thenReturn(List.of(barber));
        List<BarberDto> result = barberService.findAll();
        assertEquals(1, result.size());
        verify(cache).put("all_barbers", result);
    }

    @Test
    void findById_cached() {
        when(cache.get("barber_1")).thenReturn(Optional.of(barberDto));
        Optional<BarberDto> result = barberService.findById(1L);
        assertTrue(result.isPresent());
        verify(barberRepository, never()).findById(anyLong());
    }

    @Test
    void findById_notCached_found() {
        when(cache.get("barber_1")).thenReturn(Optional.empty());
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        Optional<BarberDto> result = barberService.findById(1L);
        assertTrue(result.isPresent());
        verify(cache).put("barber_1", result.get());
    }

    @Test
    void findById_notFound() {
        when(cache.get("barber_1")).thenReturn(Optional.empty());
        when(barberRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<BarberDto> result = barberService.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void save() {
        when(barberRepository.save(any(Barber.class))).thenReturn(barber);
        BarberDto result = barberService.save(barberDto);
        assertEquals(barberDto.getName(), result.getName());
        verify(cache).remove("all_barbers");
        verify(cache).put("barber_1", result);
    }

    @Test
    void saveAll() {
        when(barberRepository.save(any(Barber.class))).thenReturn(barber);
        List<BarberDto> result = barberService.saveAll(List.of(barberDto));
        assertEquals(1, result.size());
        verify(cache).remove("all_barbers");
        verify(cache).put("barber_1", result.get(0));
    }

    @Test
    void updateBarber_success() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(barberRepository.save(any(Barber.class))).thenReturn(barber);
        BarberDto result = barberService.updateBarber(1L, barberDto);
        assertEquals(barberDto.getName(), result.getName());
        verify(cache).put("barber_1", result);
        verify(cache).remove("all_barbers");
    }

    @Test
    void updateBarber_notFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.updateBarber(1L, barberDto));
    }

    @Test
    void deleteById() {
        barberService.deleteById(1L);
        verify(barberRepository).deleteById(1L);
        verify(cache).remove("barber_1");
        verify(cache).remove("all_barbers");
    }

    @Test
    void addOfferingToBarber_success() {
        Offering offering = new Offering();
        offering.setOfferingId(1L);
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(barberRepository.save(barber)).thenReturn(barber);
        BarberDto result = barberService.addOfferingToBarber(1L, 1L);
        assertNotNull(result);
        verify(cache).remove("all_barbers");
    }

    @Test
    void removeOfferingFromBarber_success() {
        Offering offering = new Offering();
        offering.setOfferingId(1L);
        barber.getOfferings().add(offering);
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(barberRepository.save(barber)).thenReturn(barber);
        BarberDto result = barberService.removeOfferingFromBarber(1L, 1L);
        assertNotNull(result);
        verify(cache).remove("all_barbers");
    }

    @Test
    void assignLocationToBarber_success() {
        Location location = new Location();
        location.setLocationId(1L);
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(barberRepository.save(barber)).thenReturn(barber);
        BarberDto result = barberService.assignLocationToBarber(1L, 1L);
        assertNotNull(result);
        verify(cache).remove("all_barbers");
    }

    @Test
    void removeLocationFromBarber_success() {
        Location location = new Location();
        location.setLocationId(1L);
        barber.setLocation(location);
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(barberRepository.save(barber)).thenReturn(barber);
        BarberDto result = barberService.removeLocationFromBarber(1L);
        assertNotNull(result);
        verify(cache).remove("all_barbers");
    }

    @Test
    void getBarbersByLocationName() {
        when(barberRepository.findBarbersByLocationName("test")).thenReturn(List.of(barber));
        List<BarberDto> result = barberService.getBarbersByLocationName("test");
        assertEquals(1, result.size());
    }

    @Test
    void getBarbersByLocationNameNative() {
        when(barberRepository.findBarbersByLocationNameNative("test")).thenReturn(List.of(barber));
        List<BarberDto> result = barberService.getBarbersByLocationNameNative("test");
        assertEquals(1, result.size());
    }

    @Test
    void addOfferingToBarber_barberNotFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.addOfferingToBarber(1L, 1L));
    }

    @Test
    void addOfferingToBarber_offeringNotFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(offeringRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.addOfferingToBarber(1L, 1L));
    }

    @Test
    void removeOfferingFromBarber_barberNotFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.removeOfferingFromBarber(1L, 1L));
    }

    @Test
    void removeOfferingFromBarber_offeringNotFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(offeringRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.removeOfferingFromBarber(1L, 1L));
    }

    @Test
    void assignLocationToBarber_locationNotFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.assignLocationToBarber(1L, 1L));
    }

    @Test
    void removeLocationFromBarber_barberNotFound() {
        when(barberRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> barberService.removeLocationFromBarber(1L));
    }

    @Test
    void removeLocationFromBarber_locationIsNull() {
        barber.setLocation(null);
        when(barberRepository.findById(1L)).thenReturn(Optional.of(barber));
        BarberDto result = barberService.removeLocationFromBarber(1L);
        assertNotNull(result);
        verify(locationRepository, never()).save(any());
        verify(cache).remove("all_barbers");
    }
}