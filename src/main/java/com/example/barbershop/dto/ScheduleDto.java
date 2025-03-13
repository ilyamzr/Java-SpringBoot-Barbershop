package com.example.barbershop.dto;

import com.example.barbershop.model.Schedule.DayOfWeek;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class ScheduleDto {
    private Long scheduleId;
    private Set<DayOfWeek> availableDays;
    private LocalTime startTime;
    private LocalTime endTime;
}