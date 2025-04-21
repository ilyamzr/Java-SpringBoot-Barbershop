package com.example.barbershop.controller;

import com.example.barbershop.dto.UserDto;
import com.example.barbershop.exception.ValidationException;
import com.example.barbershop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final String ID_MUST_BE_POSITIVE = "ID must be greater than 0";
    private static final String USERNAME_REQUIRED = "Username is required";
    private static final String PASSWORD_REQUIRED = "Password is required";

    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @GetMapping
    public List<UserDto> getAllUsers() {
        logger.info("Fetching all users");
        return userService.findAll();
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user by their unique ID.")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        validateId(id);
        logger.info("Fetching user with ID {}", id);
        return userService.findById(id)
                .map(user -> {
                    logger.info("User found");
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.warn("User not found");
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Create a new user", description
            = "Create a new user with the provided details.")
    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        validateUserDto(userDto);
        logger.info("Creating new user");
        UserDto createdUser = userService.save(userDto);
        logger.info("User created");
        return createdUser;
    }

    @Operation(summary = "Update a user", description
            = "Update the details of an existing user by their ID.")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserDto userDto) {
        validateId(id);
        validateUserDto(userDto);
        logger.info("Updating user with ID {}", id);
        UserDto updatedUser = userService.updateUser(id, userDto);
        logger.info("User updated");
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Delete a user by their unique ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        validateId(id);
        logger.info("Deleting user with ID {}", id);
        userService.deleteById(id);
        logger.info("User deleted");
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create multiple users", description
            = "Create multiple users in a single request.")
    @PostMapping("/bulk")
    public ResponseEntity<List<UserDto>> createUsers(@RequestBody List<UserDto> userDtos) {
        logger.info("Creating multiple users");
        userDtos.forEach(this::validateUserDto);
        List<UserDto> savedUsers = userService.saveAll(userDtos);
        logger.info("Multiple users created");
        return ResponseEntity.ok(savedUsers);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException(ID_MUST_BE_POSITIVE);
        }
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new ValidationException(USERNAME_REQUIRED);
        }

        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new ValidationException(PASSWORD_REQUIRED);
        }
    }
}
