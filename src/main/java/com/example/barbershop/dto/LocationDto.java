package com.example.barbershop.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationDto {
    private Long locationId;
    private String name;
    private String address;
    private Set<String> barbers;
}
