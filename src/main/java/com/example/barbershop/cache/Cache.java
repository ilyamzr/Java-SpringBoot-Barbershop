package com.example.barbershop.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class Cache {
    private static final int MAX_CACHE_SIZE = 100;
    private final Map<String, Object> cache;

    public Cache() {
        this.cache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };
    }

    public void put(String key, Object value) {
        synchronized (cache) {
            cache.put(key, value);
        }
    }

    public Optional<Object> get(String key) {
        synchronized (cache) {
            return Optional.ofNullable(cache.get(key));
        }
    }

    public void remove(String key) {
        synchronized (cache) {
            cache.remove(key);
        }
    }

    public void clear() {
        synchronized (cache) {
            cache.clear();
        }
    }

    public int size() {
        synchronized (cache) {
            return cache.size();
        }
    }
}