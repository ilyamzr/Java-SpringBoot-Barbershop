package com.example.barbershop.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferingDto {
    private Long offeringId;
    private String name;
    private double price;
    private int duration;
    private Set<BarberDto> barbers;
}