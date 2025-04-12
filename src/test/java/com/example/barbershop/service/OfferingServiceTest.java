package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.OfferingDto;
import com.example.barbershop.model.Offering;
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
class OfferingServiceTest {

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private Cache cache;

    @InjectMocks
    private OfferingService offeringService;

    private Offering offering;
    private OfferingDto offeringDto;

    @BeforeEach
    void setUp() {
        offering = new Offering();
        offering.setOfferingId(1L);
        offering.setName("Test Offering");

        offeringDto = new OfferingDto();
        offeringDto.setOfferingId(1L);
        offeringDto.setName("Test Offering");
    }

    @Test
    void findAll_cached() {
        when(cache.get("all_offerings")).thenReturn(Optional.of(List.of(offeringDto)));
        List<OfferingDto> result = offeringService.findAll();
        assertEquals(1, result.size());
        verify(offeringRepository, never()).findAll();
    }

    @Test
    void findAll_notCached() {
        when(cache.get("all_offerings")).thenReturn(Optional.empty());
        when(offeringRepository.findAll()).thenReturn(List.of(offering));
        List<OfferingDto> result = offeringService.findAll();
        assertEquals(1, result.size());
        verify(cache).put("all_offerings", result);
    }

    @Test
    void findById_cached() {
        when(cache.get("offering_1")).thenReturn(Optional.of(offeringDto));
        Optional<OfferingDto> result = offeringService.findById(1L);
        assertTrue(result.isPresent());
        verify(offeringRepository, never()).findById(anyLong());
    }

    @Test
    void findById_notCached_found() {
        when(cache.get("offering_1")).thenReturn(Optional.empty());
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        Optional<OfferingDto> result = offeringService.findById(1L);
        assertTrue(result.isPresent());
        verify(cache).put("offering_1", result.get());
    }

    @Test
    void findById_notFound() {
        when(cache.get("offering_1")).thenReturn(Optional.empty());
        when(offeringRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<OfferingDto> result = offeringService.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void save() {
        when(offeringRepository.save(any(Offering.class))).thenReturn(offering);
        OfferingDto result = offeringService.save(offeringDto);
        assertEquals(offeringDto.getName(), result.getName());
        verify(cache).put("offering_1", result);
        verify(cache).remove("all_offerings");
    }

    @Test
    void updateOffering_success() {
        when(offeringRepository.findById(1L)).thenReturn(Optional.of(offering));
        when(offeringRepository.save(any(Offering.class))).thenReturn(offering);
        OfferingDto result = offeringService.updateOffering(1L, offeringDto);
        assertEquals(offeringDto.getName(), result.getName());
        verify(cache).put("offering_1", result);
        verify(cache).remove("all_offerings");
    }

    @Test
    void updateOffering_notFound() {
        when(offeringRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> offeringService.updateOffering(1L, offeringDto));
    }

    @Test
    void deleteById() {
        offeringService.deleteById(1L);
        verify(offeringRepository).deleteById(1L);
        verify(cache).remove("offering_1");
        verify(cache).remove("all_offerings");
    }
}