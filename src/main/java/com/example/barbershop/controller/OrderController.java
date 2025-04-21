package com.example.barbershop.controller;

import com.example.barbershop.dto.OrderDto;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private static final String ID_MUST_BE_POSITIVE    = "ID must be greater than 0";
    private static final String BARBER_REQUIRED        = "Barber ID is required";
    private static final String OFFERING_REQUIRED      = "Offering ID is required";
    private static final String LOCATION_REQUIRED      = "Location ID is required";
    private static final String USER_REQUIRED          = "User ID is required";
    private static final String DATE_REQUIRED          = "Order date is required";
    private static final String PAST_DATE              = "Order date cannot be in the past";
    private static final String FUTURE_DATE_LIMIT_EXCEEDED = "Order date is too far in the future";

    @Operation(summary = "Get all orders", description = "Retrieve all orders.")
    @GetMapping
    public List<OrderDto> getAll() {
        logger.info("Fetching all orders");
        return orderService.findAll();
    }

    @Operation(summary = "Get order by ID", description = "Retrieve a single order by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        validateId(id);
        logger.info("Fetching order with ID {}", id);
        return orderService.findById(id)
                .map(o -> {
                    logger.info("Order found");
                    return ResponseEntity.ok(o);
                })
                .orElseGet(() -> {
                    logger.warn("Order not found");
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Create a new order", description
            = "Create order selecting barber, offering, location, user and date.")
    @PostMapping
    public OrderDto create(@RequestBody OrderDto dto) {
        validateOrderDto(dto);
        logger.info("Creating new order");
        OrderDto created = orderService.save(dto);
        logger.info("Order created");
        return created;
    }

    @Operation(summary = "Update an order", description = "Update an existing order.")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(
            @PathVariable Long id,
            @RequestBody OrderDto dto) {
        validateId(id);
        validateOrderDto(dto);
        logger.info("Updating order {}", id);
        OrderDto updated = orderService.update(id, dto);
        logger.info("Order updated");
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete an order", description = "Delete order by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        validateId(id);
        logger.info("Deleting order {}", id);
        orderService.deleteById(id);
        logger.info("Order deleted");
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bulk create orders", description = "Create multiple orders at once.")
    @PostMapping("/bulk")
    public ResponseEntity<List<OrderDto>> bulkCreate(@RequestBody List<OrderDto> dtos) {
        dtos.forEach(this::validateOrderDto);
        logger.info("Bulk creating orders");
        List<OrderDto> saved = orderService.saveAll(dtos);
        logger.info("Bulk orders created");
        return ResponseEntity.ok(saved);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException(ID_MUST_BE_POSITIVE);
        }
    }


    private void validateOrderDto(OrderDto dto) {
        if (dto.getBarberId() == null) {
            throw new ValidationException(BARBER_REQUIRED);
        }
        if (dto.getOfferingId() == null) {
            throw new ValidationException(OFFERING_REQUIRED);
        }
        if (dto.getLocationId() == null) {
            throw new ValidationException(LOCATION_REQUIRED);
        }
        if (dto.getUserId() == null) {
            throw new ValidationException(USER_REQUIRED);
        }
        if (dto.getOrderDate() == null) {
            throw new ValidationException(DATE_REQUIRED);
        }
        if (dto.getOrderDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException(PAST_DATE);
        }
        if (dto.getOrderDate().isAfter(LocalDateTime.now().plusMonths(3))) {
            throw new ValidationException(FUTURE_DATE_LIMIT_EXCEEDED);
        }
    }

}
