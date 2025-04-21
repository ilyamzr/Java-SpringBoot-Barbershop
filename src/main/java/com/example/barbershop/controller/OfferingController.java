package com.example.barbershop.controller;

import com.example.barbershop.dto.OfferingDto;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.service.OfferingService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offerings")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;
    private static final Logger logger = LoggerFactory.getLogger(OfferingController.class);

    private static final String ID_MUST_BE_POSITIVE = "ID must be greater than 0";
    private static final String NAME_REQUIRED = "Offering name is required";
    private static final String NAME_MIN_LENGTH = "Offering name must be at least 2 characters";
    private static final String PRICE_POSITIVE = "Price must be positive";
    private static final String DURATION_POSITIVE = "Duration must be positive";
    private static final String PRICE_MAX = "Price cannot exceed 1000";
    private static final String DURATION_MAX = "Duration cannot exceed 240 minutes";

    @Operation(summary = "Get all offerings", description = "Retrieve a list of all offerings.")
    @GetMapping
    public List<OfferingDto> getAllOfferings() {
        logger.info("Fetching all offerings");
        return offeringService.findAll();
    }

    @Operation(summary = "Get offering by ID", description
            = "Retrieve an offering by its unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<OfferingDto> getOfferingById(@PathVariable Long id) {
        validateId(id);
        logger.info("Fetching offering with id: {}", id);
        return offeringService.findById(id)
                .map(offering -> {
                    logger.info("Found offering: {}", offering);
                    return ResponseEntity.ok(offering);
                })
                .orElseGet(() -> {
                    logger.warn("Offering not found for id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Create a new offering", description
            = "Create a new offering with the provided details.")
    @PostMapping
    public OfferingDto createOffering(@RequestBody OfferingDto offeringDto) {
        validateOfferingDto(offeringDto);
        logger.info("Creating new offering: {}", offeringDto);
        OfferingDto createdOffering = offeringService.save(offeringDto);
        logger.info("Created offering with id: {}", createdOffering.getOfferingId());
        return createdOffering;
    }

    @Operation(summary = "Update an offering", description
            = "Update the details of an existing offering by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<OfferingDto> updateOffering(
            @PathVariable Long id,
            @RequestBody OfferingDto offeringDto) {
        validateId(id);
        validateOfferingDto(offeringDto);
        logger.info("Updating offering with id: {}", id);
        OfferingDto updatedOffering = offeringService.updateOffering(id, offeringDto);
        logger.info("Updated offering with id: {}", id);
        return ResponseEntity.ok(updatedOffering);
    }

    @Operation(summary = "Delete an offering", description = "Delete an offering by its unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffering(@PathVariable Long id) {
        validateId(id);
        logger.info("Deleting offering with id: {}", id);
        offeringService.deleteById(id);
        logger.info("Deleted offering with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException(ID_MUST_BE_POSITIVE);
        }
    }

    private void validateOfferingDto(OfferingDto offeringDto) {
        if (offeringDto.getName() == null || offeringDto.getName().trim().isEmpty()) {
            throw new ValidationException(NAME_REQUIRED);
        }

        if (offeringDto.getName().trim().length() < 2) {
            throw new ValidationException(NAME_MIN_LENGTH);
        }

        if (offeringDto.getPrice() <= 0) {
            throw new ValidationException(PRICE_POSITIVE);
        }

        if (offeringDto.getPrice() > 1000) {
            throw new ValidationException(PRICE_MAX);
        }

        if (offeringDto.getDuration() <= 0) {
            throw new ValidationException(DURATION_POSITIVE);
        }

        if (offeringDto.getDuration() > 240) {
            throw new ValidationException(DURATION_MAX);
        }

    }
}
