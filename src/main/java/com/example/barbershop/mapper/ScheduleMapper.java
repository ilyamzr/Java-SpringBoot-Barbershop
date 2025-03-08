package com.example.barbershop.mapper;

import com.example.barbershop.dto.ScheduleDto;
import com.example.barbershop.model.Schedule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScheduleMapper {

    public ScheduleDto toDto(Schedule schedule) {
        ScheduleDto dto = toDtoShallow(schedule);
        dto.setBarber(BarberMapper.toDtoShallow(schedule.getBarber()));
        return dto;
    }

    public ScheduleDto toDtoShallow(Schedule schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        return dto;
    }

    public Schedule toEntity(ScheduleDto dto) {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(dto.getScheduleId());
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        return schedule;
    }
}