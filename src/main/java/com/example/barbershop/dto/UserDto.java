package com.example.barbershop.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private Long userId;
    private String username;
    private String password;
}
