package com.example.barbershop.controller;

import com.example.barbershop.dto.BarberDto;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.service.BarberService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/barbers")
@RequiredArgsConstructor
public class BarberController {
    private final BarberService barberService;
    private static final Logger logger = LoggerFactory.getLogger(BarberController.class);

    private static final String ID_MUST_BE_POSITIVE = "ID must be greater than 0";
    private static final String NAME_REQUIRED = "Barber name is required";
    private static final String AVAILABLE_DAYS_REQUIRED = "Available days are required";
    private static final String START_TIME_REQUIRED = "Start time is required";
    private static final String END_TIME_REQUIRED = "End time is required";
    private static final String END_TIME_AFTER_START = "End time must be after start time";
    private static final String LOCATION_NAME_REQUIRED = "Location name is required";

    @Operation(summary = "Get all barbers", description = "Retrieve a list of all barbers.")
    @GetMapping
    public List<BarberDto> getAllBarbers() {
        logger.info("Fetching all barbers");
        return barberService.findAll();
    }

    @Operation(summary = "Get barber by ID", description = "Retrieve a barber by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<BarberDto> getBarberById(@PathVariable Long id) {
        validateId(id);
        logger.info("Fetching barber with ID");
        return barberService.findById(id)
                .map(barber -> {
                    logger.info("Barber found");
                    return ResponseEntity.ok(barber);
                })
                .orElseGet(() -> {
                    logger.warn("Barber not found");
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Create a new barber", description = "Create a new barber with the provided details.")
    @PostMapping()
    public BarberDto createBarber(@RequestBody BarberDto barberDto) {
        validateBarberDto(barberDto);
        logger.info("Creating new barber");
        BarberDto createdBarber = barberService.save(barberDto);
        logger.info("Barber created");
        return createdBarber;
    }

    @Operation(summary = "Update a barber", description = "Update the details of an existing barber by their ID.")
    @PutMapping("/{id}")
    public ResponseEntity<BarberDto> updateBarber(
            @PathVariable Long id,
            @RequestBody BarberDto barberDto) {
        validateId(id);
        validateBarberDto(barberDto);
        logger.info("Updating barber");
        BarberDto updatedBarber = barberService.updateBarber(id, barberDto);
        logger.info("Barber updated");
        return ResponseEntity.ok(updatedBarber);
    }

    @Operation(summary = "Delete a barber", description = "Delete a barber by their unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable Long id) {
        validateId(id);
        logger.info("Deleting barber");
        barberService.deleteById(id);
        logger.info("Barber deleted");
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add an offering to a barber", description = "Add a specific offering to a barber by their IDs.")
    @PostMapping("/{barberId}/offerings/{offeringId}")
    public ResponseEntity<BarberDto> addOfferingToBarber(
            @PathVariable Long barberId,
            @PathVariable Long offeringId) {
        validateId(barberId);
        validateId(offeringId);
        logger.info("Adding offering to barber");
        BarberDto updatedBarber = barberService.addOfferingToBarber(barberId, offeringId);
        logger.info("Offering added to barber");
        return ResponseEntity.ok(updatedBarber);
    }

    @Operation(summary = "Remove an offering from a barber", description = "Remove a specific offering from a barber by their IDs.")
    @DeleteMapping("/{barberId}/offerings/{offeringId}")
    public ResponseEntity<BarberDto> removeOfferingFromBarber(
            @PathVariable Long barberId,
            @PathVariable Long offeringId) {
        validateId(barberId);
        validateId(offeringId);
        logger.info("Removing offering from barber");
        BarberDto updatedBarber = barberService.removeOfferingFromBarber(barberId, offeringId);
        logger.info("Offering removed from barber");
        return ResponseEntity.ok(updatedBarber);
    }

    @Operation(summary = "Get barbers by location", description = "Retrieve a list of barbers by their location name.")
    @GetMapping("/by-location")
    public ResponseEntity<List<BarberDto>> getBarbersByLocation(
            @RequestParam String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            throw new ValidationException(LOCATION_NAME_REQUIRED);
        }
        logger.info("Fetching barbers by location");
        List<BarberDto> barbers = barberService.getBarbersByLocationName(locationName);
        logger.info("Barbers found for location");
        return ResponseEntity.ok(barbers);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException(ID_MUST_BE_POSITIVE);
        }
    }

    private void validateBarberDto(BarberDto barberDto) {
        if (barberDto.getName() == null || barberDto.getName().trim().isEmpty()) {
            throw new ValidationException(NAME_REQUIRED);
        }

        if (barberDto.getAvailableDays() == null || barberDto.getAvailableDays().isEmpty()) {
            throw new ValidationException(AVAILABLE_DAYS_REQUIRED);
        }

        if (barberDto.getStartTime() == null) {
            throw new ValidationException(START_TIME_REQUIRED);
        }

        if (barberDto.getEndTime() == null) {
            throw new ValidationException(END_TIME_REQUIRED);
        }

        if (barberDto.getEndTime().before(barberDto.getStartTime())) {
            throw new ValidationException(END_TIME_AFTER_START);
        }
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple barbers", description = "Create multiple barbers in a single request.")
    public ResponseEntity<List<BarberDto>> createBarbers(@RequestBody List<BarberDto> barberDtos) {
        logger.info("Creating multiple barbers");
        barberDtos.forEach(this::validateBarberDto);
        List<BarberDto> savedBarbers = barberService.saveAll(barberDtos);
        logger.info("Multiple barbers created");
        return ResponseEntity.ok(savedBarbers);
    }
}