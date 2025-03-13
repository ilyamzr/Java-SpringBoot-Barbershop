package com.example.barbershop.service;

import com.example.barbershop.dto.OfferingDto;
import com.example.barbershop.mapper.OfferingMapper;
import com.example.barbershop.model.Offering;
import com.example.barbershop.repository.OfferingRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferingService {
    private static final String OFFERING_NOT_FOUND = "Offering not found";

    private final OfferingRepository offeringRepository;

    public List<OfferingDto> findAll() {
        return offeringRepository.findAll().stream()
                .map(OfferingMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<OfferingDto> findById(Long id) {
        return offeringRepository.findById(id)
                .map(OfferingMapper::toDto);
    }

    public OfferingDto save(OfferingDto offeringDto) {
        Offering offering = OfferingMapper.toEntity(offeringDto);
        Offering saved = offeringRepository.save(offering);
        return OfferingMapper.toDto(saved);
    }

    public OfferingDto updateOffering(Long id, OfferingDto offeringDto) {
        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(OFFERING_NOT_FOUND));
        offering.setName(offeringDto.getName());
        offering.setPrice(offeringDto.getPrice());
        offering.setDuration(offeringDto.getDuration());
        Offering updated = offeringRepository.save(offering);
        return OfferingMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        offeringRepository.deleteById(id);
    }
}