package com.example.barbershop.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferingDto {
    private Long serviceId;
    private String name;
    private double price;
    private int duration;
    private String description;
    private Set<BarberDto> barbers;
}