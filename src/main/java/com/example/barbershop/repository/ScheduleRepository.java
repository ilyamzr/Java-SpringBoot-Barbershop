package com.example.barbershop.repository;

import com.example.barbershop.model.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByBarberBarberId(Long barberId);
}