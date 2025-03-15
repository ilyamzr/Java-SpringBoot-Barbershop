package com.example.barbershop.dto;

import com.example.barbershop.model.Barber.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Time;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarberDto {
    private Long barberId;
    private String name;
    private Set<DayOfWeek> availableDays;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time startTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time endTime;
    private Set<OfferingDto> offerings;
    private String locationName;
}