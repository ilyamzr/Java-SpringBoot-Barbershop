package com.example.barbershop.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDto {
    private Long scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BarberDto barber;
}