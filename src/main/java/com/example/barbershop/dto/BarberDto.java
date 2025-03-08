package com.example.barbershop.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarberDto {
    private Long barberId;
    private String name;
    private Set<String> workingDays;
    private Set<OfferingDto> offerings;
    private Set<ScheduleDto> schedules;
}