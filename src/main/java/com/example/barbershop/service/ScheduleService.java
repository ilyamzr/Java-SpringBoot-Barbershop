package com.example.barbershop.service;

import com.example.barbershop.dto.ScheduleDto;
import com.example.barbershop.mapper.ScheduleMapper;
import com.example.barbershop.model.Schedule;
import com.example.barbershop.repository.ScheduleRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ScheduleService {
    private static final String SCHEDULE_NOT_FOUND = "Schedule not found";

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleDto> findAll() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<ScheduleDto> findById(Long id) {
        return scheduleRepository.findById(id)
                .map(ScheduleMapper::toDto);
    }

    public ScheduleDto save(ScheduleDto scheduleDto) {
        Schedule schedule = ScheduleMapper.toEntity(scheduleDto);
        Schedule saved = scheduleRepository.save(schedule);
        return ScheduleMapper.toDto(saved);
    }

    public ScheduleDto updateSchedule(Long id, ScheduleDto scheduleDto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(SCHEDULE_NOT_FOUND));
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        Schedule updated = scheduleRepository.save(schedule);
        return ScheduleMapper.toDto(updated);
    }

    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    public List<ScheduleDto> findByBarberId(Long barberId) {
        return scheduleRepository.findByBarberBarberId(barberId).stream()
                .map(ScheduleMapper::toDto)
                .collect(Collectors.toList());
    }
}