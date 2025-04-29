package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.OrderDto;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.mapper.OrderMapper;
import com.example.barbershop.model.Barber;
import com.example.barbershop.model.Location;
import com.example.barbershop.model.Offering;
import com.example.barbershop.model.Order;
import com.example.barbershop.model.User;
import com.example.barbershop.repository.BarberRepository;
import com.example.barbershop.repository.LocationRepository;
import com.example.barbershop.repository.OfferingRepository;
import com.example.barbershop.repository.OrderRepository;
import com.example.barbershop.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final String ORDER_NOT_FOUND        = "Order not found";
    private static final String BARBER_NOT_FOUND       = "Barber not found";
    private static final String OFFERING_NOT_FOUND     = "Offering not found";
    private static final String LOCATION_NOT_FOUND     = "Location not found";
    private static final String USER_NOT_FOUND         = "User not found";

    private static final String ALL_ORDERS_CACHE_KEY     = "all_orders";
    private static final String ORDER_CACHE_KEY_PREFIX   = "order_";

    private final OrderRepository     orderRepository;
    private final BarberRepository    barberRepository;
    private final OfferingRepository  offeringRepository;
    private final LocationRepository  locationRepository;
    private final UserRepository      userRepository;
    private final Cache               cache;

    public List<OrderDto> findAll() {
        Optional<Object> cached = cache.get(ALL_ORDERS_CACHE_KEY);
        if (cached.isPresent()) {
            return (List<OrderDto>) cached.get();
        }
        List<OrderDto> dtos = orderRepository.findAll().stream()
                .map(OrderMapper::toDto)
                .collect(Collectors.toList());
        cache.put(ALL_ORDERS_CACHE_KEY, dtos);
        return dtos;
    }

    public Optional<OrderDto> findById(Long id) {
        String key = ORDER_CACHE_KEY_PREFIX + id;
        Optional<Object> cached = cache.get(key);
        if (cached.isPresent()) {
            return Optional.of((OrderDto) cached.get());
        }
        Optional<OrderDto> dto = orderRepository.findById(id)
                .map(OrderMapper::toDto);
        dto.ifPresent(d -> cache.put(key, d));
        return dto;
    }

    @Transactional
    public OrderDto save(OrderDto dto) {

        if (dto.getOrderDate() == null) {
            throw new ValidationException("Order date is required");
        }

        LocalDateTime now = LocalDateTime.now();
        if (dto.getOrderDate().isBefore(now)) {
            throw new ValidationException("Order date cannot be in the past");
        }

        if (dto.getOrderDate().isAfter(now.plusMonths(6))) {
            throw new ValidationException("Order date cannot be more than 6 months in the future");
        }

        Barber barber = barberRepository.findById(dto.getBarberId())
                .orElseThrow(() -> new ValidationException(BARBER_NOT_FOUND));
        Offering offering = offeringRepository.findById(dto.getOfferingId())
                .orElseThrow(() -> new ValidationException(OFFERING_NOT_FOUND));

        if (!barber.getOfferings().contains(offering)) {
            throw new ValidationException("Barber does not provide the selected offering");
        }

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ValidationException(LOCATION_NOT_FOUND));

        Barber.DayOfWeek orderDay = Barber.DayOfWeek.valueOf(dto.getOrderDate()
                .getDayOfWeek().name());
        if (!barber.getAvailableDays().contains(orderDay)) {
            throw new ValidationException("Barber does not work on " + orderDay);
        }

        LocalTime orderTime = dto.getOrderDate().toLocalTime();
        LocalTime barberStart = barber.getStartTime().toLocalTime();
        LocalTime barberEnd = barber.getEndTime().toLocalTime();

        if (orderTime.isBefore(barberStart) || orderTime.isAfter(barberEnd)) {
            throw new ValidationException("Barber is not available at " + orderTime
                    + ". Works from " + barberStart + " to " + barberEnd);
        }

        if (barber.getLocation() != null && !barber.getLocation().getLocationId()
                .equals(location.getLocationId())) {
            throw new ValidationException("Barber does not work at the selected location");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ValidationException(USER_NOT_FOUND));

        Order order = OrderMapper.toEntity(dto);
        order.setBarber(barber);
        order.setOffering(offering);
        order.setLocation(location);
        order.setUser(user);

        Order saved = orderRepository.save(order);
        OrderDto savedDto = OrderMapper.toDto(saved);

        cache.remove(ALL_ORDERS_CACHE_KEY);
        cache.put(ORDER_CACHE_KEY_PREFIX + saved.getOrderId(), savedDto);

        return savedDto;
    }

    @Transactional
    public OrderDto update(Long id, OrderDto dto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ValidationException(ORDER_NOT_FOUND));

        if (dto.getOrderDate() != null) {
            order.setOrderDate(dto.getOrderDate());
        }
        if (dto.getBarberId() != null) {
            Barber barber = barberRepository.findById(dto.getBarberId())
                    .orElseThrow(() -> new ValidationException(BARBER_NOT_FOUND));
            order.setBarber(barber);
        }
        if (dto.getOfferingId() != null) {
            Offering offering = offeringRepository.findById(dto.getOfferingId())
                    .orElseThrow(() -> new ValidationException(OFFERING_NOT_FOUND));
            order.setOffering(offering);
        }
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                    .orElseThrow(() -> new ValidationException(LOCATION_NOT_FOUND));
            order.setLocation(location);
        }
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ValidationException(USER_NOT_FOUND));
            order.setUser(user);
        }

        Order updated = orderRepository.save(order);
        OrderDto updatedDto = OrderMapper.toDto(updated);

        cache.remove(ALL_ORDERS_CACHE_KEY);
        cache.put(ORDER_CACHE_KEY_PREFIX + id, updatedDto);

        return updatedDto;
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
        cache.remove(ORDER_CACHE_KEY_PREFIX + id);
        cache.remove(ALL_ORDERS_CACHE_KEY);
    }

    @Transactional
    public List<OrderDto> saveAll(List<OrderDto> dtos) {
        return dtos.stream()
                .map(this::save)
                .collect(Stream.toList());
    }
}