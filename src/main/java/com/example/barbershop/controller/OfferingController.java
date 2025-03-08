package com.example.barbershop.controller;

import com.example.barbershop.dto.OfferingDto;
import com.example.barbershop.service.OfferingService;
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
@RequestMapping("/offerings")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;

    @GetMapping
    public List<OfferingDto> getAllOfferings() {
        return offeringService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferingDto> getOfferingById(@PathVariable Long id) {
        return offeringService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public OfferingDto createOffering(@RequestBody OfferingDto offeringDto) {
        return offeringService.save(offeringDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferingDto> updateOffering(@PathVariable Long id,
                                                      @RequestBody OfferingDto offeringDto) {
        OfferingDto updatedOffering = offeringService.updateOffering(id, offeringDto);
        return ResponseEntity.ok(updatedOffering);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffering(@PathVariable Long id) {
        offeringService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}