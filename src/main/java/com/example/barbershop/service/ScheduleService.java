package com.example.barbershop.service;

import com.example.barbershop.dto.ScheduleDto;
import com.example.barbershop.mapper.ScheduleMapper;
import com.example.barbershop.model.Barber;
import com.example.barbershop.model.Schedule;
import com.example.barbershop.repository.BarberRepository;
import com.example.barbershop.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private static final String SCHEDULE_NOT_FOUND = "Schedule not found";
    private static final String BARBER_NOT_FOUND = "Barber not found";

    private final ScheduleRepository scheduleRepository;
    private final BarberRepository barberRepository;

    public ScheduleDto getScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .map(ScheduleMapper::toDto)
                .orElseThrow(() -> new RuntimeException(SCHEDULE_NOT_FOUND));
    }

    @Transactional
    public ScheduleDto createScheduleForBarber(Long barberId, ScheduleDto scheduleDto) {
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> new RuntimeException(BARBER_NOT_FOUND));

        Schedule schedule = ScheduleMapper.toEntity(scheduleDto);
        schedule.setBarber(barber);
        Schedule savedSchedule = scheduleRepository.save(schedule);

        barber.getSchedules().add(savedSchedule);
        barberRepository.save(barber);

        return ScheduleMapper.toDto(savedSchedule);
    }

    @Transactional
    public ScheduleDto updateSchedule(Long scheduleId, ScheduleDto scheduleDto) {
        Schedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException(SCHEDULE_NOT_FOUND));

        existingSchedule.setAvailableDays(scheduleDto.getAvailableDays());
        existingSchedule.setStartTime(scheduleDto.getStartTime());
        existingSchedule.setEndTime(scheduleDto.getEndTime());

        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return ScheduleMapper.toDto(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException(SCHEDULE_NOT_FOUND));

        Barber barber = schedule.getBarber();
        if (barber != null) {
            barber.getSchedules().remove(schedule);
            barberRepository.save(barber);
        }

        scheduleRepository.delete(schedule);
    }
}