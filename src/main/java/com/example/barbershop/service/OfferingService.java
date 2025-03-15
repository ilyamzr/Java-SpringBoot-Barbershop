package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.OfferingDto;
import com.example.barbershop.mapper.OfferingMapper;
import com.example.barbershop.model.Offering;
import com.example.barbershop.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferingService {
    private static final String OFFERING_NOT_FOUND = "Offering not found";
    private static final String ALL_OFFERINGS_CACHE_KEY = "all_offerings";
    private static final String OFFERING_CACHE_KEY_PREFIX = "offering_";

    private final OfferingRepository offeringRepository;
    private final Cache cache;

    public List<OfferingDto> findAll() {
        Optional<Object> cachedOfferings = cache.get(ALL_OFFERINGS_CACHE_KEY);
        if (cachedOfferings.isPresent()) {
            return (List<OfferingDto>) cachedOfferings.get();
        }

        List<OfferingDto> offerings = offeringRepository.findAll().stream()
                .map(OfferingMapper::toDto)
                .collect(Collectors.toList());

        // Сохраняем в кэш
        cache.put(ALL_OFFERINGS_CACHE_KEY, offerings);

        return offerings;
    }

    public Optional<OfferingDto> findById(Long id) {
        String cacheKey = OFFERING_CACHE_KEY_PREFIX + id;

        Optional<Object> cachedOffering = cache.get(cacheKey);
        if (cachedOffering.isPresent()) {
            return Optional.of((OfferingDto) cachedOffering.get());
        }

        Optional<OfferingDto> offeringDto = offeringRepository.findById(id)
                .map(OfferingMapper::toDto);

        offeringDto.ifPresent(dto -> cache.put(cacheKey, dto));

        return offeringDto;
    }

    public OfferingDto save(OfferingDto offeringDto) {
        Offering offering = OfferingMapper.toEntity(offeringDto);
        Offering saved = offeringRepository.save(offering);
        OfferingDto savedDto = OfferingMapper.toDto(saved);

        String cacheKey = OFFERING_CACHE_KEY_PREFIX + saved.getOfferingId();
        cache.put(cacheKey, savedDto);

        cache.remove(ALL_OFFERINGS_CACHE_KEY);

        return savedDto;
    }

    public OfferingDto updateOffering(Long id, OfferingDto offeringDto) {
        Offering offering = offeringRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(OFFERING_NOT_FOUND));
        offering.setName(offeringDto.getName());
        offering.setPrice(offeringDto.getPrice());
        offering.setDuration(offeringDto.getDuration());
        Offering updated = offeringRepository.save(offering);
        OfferingDto updatedDto = OfferingMapper.toDto(updated);

        String cacheKey = OFFERING_CACHE_KEY_PREFIX + id;
        cache.put(cacheKey, updatedDto);

        cache.remove(ALL_OFFERINGS_CACHE_KEY);

        return updatedDto;
    }

    public void deleteById(Long id) {
        offeringRepository.deleteById(id);

        String cacheKey = OFFERING_CACHE_KEY_PREFIX + id;
        cache.remove(cacheKey);

        cache.remove(ALL_OFFERINGS_CACHE_KEY);
    }
}