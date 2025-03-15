package com.example.barbershop.service;

import com.example.barbershop.dto.BarberDto;
import com.example.barbershop.mapper.BarberMapper;
import com.example.barbershop.model.Barber;
import com.example.barbershop.model.Location;
import com.example.barbershop.model.Offering;
import com.example.barbershop.repository.BarberRepository;
import com.example.barbershop.repository.LocationRepository;
import com.example.barbershop.repository.OfferingRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BarberService {
    private static final String BARBER_NOT_FOUND = "Barber not found";
    private static final String OFFERING_NOT_FOUND = "Offering not found";

    private final BarberRepository barberRepository;
    private final OfferingRepository offeringRepository;
    private final LocationRepository locationRepository;

    public List<BarberDto> findAll() {
        return barberRepository.findAll().stream()
                .map(BarberMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<BarberDto> findById(Long id) {
        return barberRepository.findById(id)
                .map(BarberMapper::toDto);
    }

    public BarberDto save(BarberDto barberDto) {
        Barber barber = BarberMapper.toEntity(barberDto);
        Barber saved = barberRepository.save(barber);
        return BarberMapper.toDto(saved);
    }

    @Transactional
    public BarberDto updateBarber(Long id, BarberDto barberDto) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(BARBER_NOT_FOUND));
        barber.setName(barberDto.getName());
        barber.setAvailableDays(barberDto.getAvailableDays());
        barber.setStartTime(barberDto.getStartTime());
        barber.setEndTime(barberDto.getEndTime());
        Barber updated = barberRepository.save(barber);
        return BarberMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        barberRepository.deleteById(id);
    }

    @Transactional
    public BarberDto addOfferingToBarber(Long barberId, Long offeringId) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException(BARBER_NOT_FOUND));
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new RuntimeException(OFFERING_NOT_FOUND));

        barber.getOfferings().add(offering);
        offering.getBarbers().add(barber);
        barberRepository.save(barber);
        offeringRepository.save(offering);
        return BarberMapper.toDto(barber);
    }

    @Transactional
    public BarberDto removeOfferingFromBarber(Long barberId, Long offeringId) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException(BARBER_NOT_FOUND));
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new RuntimeException(OFFERING_NOT_FOUND));

        barber.getOfferings().remove(offering);
        offering.getBarbers().remove(barber);
        barberRepository.save(barber);
        offeringRepository.save(offering);
        return BarberMapper.toDto(barber);
    }

    @Transactional
    public BarberDto assignLocationToBarber(Long barberId, Long locationId) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException(BARBER_NOT_FOUND));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        barber.setLocation(location);
        location.getBarbers().add(barber);
        barberRepository.save(barber);
        locationRepository.save(location);
        return BarberMapper.toDto(barber);
    }

    @Transactional
    public BarberDto removeLocationFromBarber(Long barberId) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException(BARBER_NOT_FOUND));
        Location location = barber.getLocation();

        if (location != null) {
            location.getBarbers().remove(barber);
            barber.setLocation(null);
            locationRepository.save(location);
        }
        barberRepository.save(barber);
        return BarberMapper.toDto(barber);
    }

}