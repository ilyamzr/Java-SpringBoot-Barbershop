package com.example.barbershop.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDto {
    private Long orderId;
    private LocalDateTime orderDate;

    private Long barberId;
    private Long offeringId;
    private Long locationId;
    private Long userId;
}
