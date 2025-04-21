package com.example.barbershop.mapper;

import com.example.barbershop.dto.OrderDto;
import com.example.barbershop.model.Order;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {

    public OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        if (order.getBarber() != null) {
            dto.setBarberId(order.getBarber().getBarberId());
        }
        if (order.getOffering() != null) {
            dto.setOfferingId(order.getOffering().getOfferingId());
        }
        if (order.getLocation() != null) {
            dto.setLocationId(order.getLocation().getLocationId());
        }
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getUserId());
        }
        return dto;
    }

    public Order toEntity(OrderDto dto) {
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setOrderDate(dto.getOrderDate());
        // связи проставляются в сервисе
        return order;
    }
}
