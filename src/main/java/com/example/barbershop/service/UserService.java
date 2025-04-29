package com.example.barbershop.service;

import com.example.barbershop.cache.Cache;
import com.example.barbershop.dto.UserDto;
import com.example.barbershop.mapper.UserMapper;
import com.example.barbershop.model.User;
import com.example.barbershop.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String ALL_USERS_CACHE_KEY = "all_users";
    private static final String USER_CACHE_KEY_PREFIX = "user_";

    private final UserRepository userRepository;
    private final Cache cache;

    public List<UserDto> findAll() {
        Optional<Object> cachedUsers = cache.get(ALL_USERS_CACHE_KEY);
        if (cachedUsers.isPresent()) {
            return (List<UserDto>) cachedUsers.get();
        }

        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        cache.put(ALL_USERS_CACHE_KEY, users);

        return users;
    }

    public Optional<UserDto> findById(Long id) {
        String cacheKey = USER_CACHE_KEY_PREFIX + id;

        Optional<Object> cachedUser = cache.get(cacheKey);
        if (cachedUser.isPresent()) {
            return Optional.of((UserDto) cachedUser.get());
        }

        Optional<UserDto> userDto = userRepository.findById(id)
                .map(UserMapper::toDto);

        userDto.ifPresent(dto -> cache.put(cacheKey, dto));

        return userDto;
    }

    public UserDto save(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        UserDto savedDto = UserMapper.toDto(saved);

        String cacheKey = USER_CACHE_KEY_PREFIX + saved.getUserId();
        cache.remove(ALL_USERS_CACHE_KEY);
        cache.put(cacheKey, savedDto);

        return savedDto;
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        User updated = userRepository.save(user);
        UserDto updatedDto = UserMapper.toDto(updated);

        String cacheKey = USER_CACHE_KEY_PREFIX + id;
        cache.put(cacheKey, updatedDto);
        cache.remove(ALL_USERS_CACHE_KEY);

        return updatedDto;
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);

        String cacheKey = USER_CACHE_KEY_PREFIX + id;
        cache.remove(cacheKey);
        cache.remove(ALL_USERS_CACHE_KEY);
    }

    @Transactional
    public List<UserDto> saveAll(List<UserDto> userDtos) {
        List<UserDto> savedDtos = userDtos.stream()
                .map(UserMapper::toEntity)
                .map(userRepository::save)
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        cache.remove(ALL_USERS_CACHE_KEY);

        savedDtos.forEach(dto ->
                cache.put(USER_CACHE_KEY_PREFIX + dto.getUserId(), dto)
        );

        return savedDtos;
    }
}