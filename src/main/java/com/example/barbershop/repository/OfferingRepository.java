package com.example.barbershop.repository;

import com.example.barbershop.model.Offering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
}