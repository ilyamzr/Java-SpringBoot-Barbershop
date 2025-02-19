package com.example.barbershop.controller;

import com.example.barbershop.dto.ResponseDto;
import com.example.barbershop.service.BarberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//http://localhost:8080/query?barberName=John
//http://localhost:8080/query?barberName=Mike

@RestController
public class BarberShopController {

    private final BarberService barberService;

    @Autowired
    public BarberShopController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping("/query")
    public ResponseDto getBarberQuery(@RequestParam String barberName) {
        return barberService.getBarberAvailability(barberName);
    }

    @GetMapping("/path/{barberName}")
    public ResponseDto getBarberPath(@PathVariable String barberName) {
        return barberService.getBarberAvailability(barberName);
    }
}