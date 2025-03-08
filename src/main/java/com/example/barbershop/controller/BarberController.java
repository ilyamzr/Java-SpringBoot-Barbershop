package com.example.barbershop.controller;

import com.example.barbershop.dto.BarberDto;
import com.example.barbershop.service.BarberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/barbers")
@RequiredArgsConstructor
public class BarberController {
    private final BarberService barberService;

    @GetMapping
    public List<BarberDto> getAllBarbers() {
        return barberService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarberDto> getBarberById(@PathVariable Long id) {
        return barberService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public BarberDto createBarber(@RequestBody BarberDto barberDto) {
        return barberService.save(barberDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BarberDto> updateBarber(@PathVariable Long id,
                                                  @RequestBody BarberDto barberDto) {
        BarberDto updatedBarber = barberService.updateBarber(id, barberDto);
        return ResponseEntity.ok(updatedBarber);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable Long id) {
        barberService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{barberId}/offerings/{offeringId}")
    public ResponseEntity<BarberDto> addOfferingToBarber(@PathVariable Long barberId,
                                                         @PathVariable Long offeringId) {
        BarberDto updatedBarber = barberService.addOfferingToBarber(barberId, offeringId);
        return ResponseEntity.ok(updatedBarber);
    }

    @DeleteMapping("/{barberId}/offerings/{offeringId}")
    public ResponseEntity<BarberDto> removeOfferingFromBarber(@PathVariable Long barberId,
                                                              @PathVariable Long offeringId) {
        BarberDto updatedBarber = barberService.removeOfferingFromBarber(barberId, offeringId);
        return ResponseEntity.ok(updatedBarber);
    }
}