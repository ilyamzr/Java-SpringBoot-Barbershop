package com.example.barbershop.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AvailabilityDto {
    private String date;
    private List<String> times;
}