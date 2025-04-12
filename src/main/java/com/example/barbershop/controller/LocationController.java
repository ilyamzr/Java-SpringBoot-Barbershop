package com.example.barbershop.controller;

import com.example.barbershop.dto.LocationDto;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    private static final String ID_MUST_BE_POSITIVE = "ID must be greater than 0";
    private static final String NAME_REQUIRED = "Location name is required";
    private static final String ADDRESS_REQUIRED = "Address is required";
    private static final String NAME_MIN_LENGTH = "Location name must be at least 2 characters";
    private static final String ADDRESS_MIN_LENGTH = "Address must be at least 5 characters";

    @Operation(summary = "Get all locations", description = "Retrieve a list of all locations.")
    @GetMapping
    public List<LocationDto> getLocations() {
        logger.info("Fetching all locations");
        return locationService.findAll();
    }

    @Operation(summary = "Get location by ID", description
            = "Retrieve a location by its unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        validateId(id);
        logger.info("Fetching location with id: {}", id);
        return locationService.findById(id)
                .map(location -> {
                    logger.info("Found location: {}", location);
                    return ResponseEntity.ok(location);
                })
                .orElseGet(() -> {
                    logger.warn("Location not found for id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Create a new location", description
            = "Create a new location with the provided details.")
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody LocationDto locationDto) {
        validateLocationDto(locationDto);
        logger.info("Creating new location: {}", locationDto);
        LocationDto createdLocation = locationService.createLocation(locationDto);
        logger.info("Created location with id: {}", createdLocation.getLocationId());
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a location", description
            = "Update the details of an existing location by its ID.")
    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Long id,
            @RequestBody LocationDto locationDto) {
        validateId(id);
        validateLocationDto(locationDto);
        logger.info("Updating location with id: {}", id);
        LocationDto updatedLocation = locationService.updateLocation(id, locationDto);
        logger.info("Updated location with id: {}", id);
        return ResponseEntity.ok(updatedLocation);
    }

    @Operation(summary = "Delete a location", description = "Delete a location by its unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        validateId(id);
        logger.info("Deleting location with id: {}", id);
        locationService.deleteLocation(id);
        logger.info("Deleted location with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException(ID_MUST_BE_POSITIVE);
        }
    }

    private void validateLocationDto(LocationDto locationDto) {
        if (locationDto.getName() == null || locationDto.getName().trim().isEmpty()) {
            throw new ValidationException(NAME_REQUIRED);
        }

        if (locationDto.getName().trim().length() < 2) {
            throw new ValidationException(NAME_MIN_LENGTH);
        }

        if (locationDto.getAddress() == null || locationDto.getAddress().trim().isEmpty()) {
            throw new ValidationException(ADDRESS_REQUIRED);
        }

        if (locationDto.getAddress().trim().length() < 5) {
            throw new ValidationException(ADDRESS_MIN_LENGTH);
        }
    }
}
