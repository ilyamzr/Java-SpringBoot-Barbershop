package com.example.barbershop.controller;

import com.example.barbershop.dto.ScheduleDto;
import com.example.barbershop.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @PostMapping("/barbers/{barberId}")
    public ResponseEntity<ScheduleDto> createSchedule(
            @PathVariable Long barberId,
            @RequestBody ScheduleDto scheduleDto) {
        return new ResponseEntity<>(
                scheduleService.createScheduleForBarber(barberId, scheduleDto),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDto> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleDto scheduleDto) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}