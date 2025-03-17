package com.example.barbershop.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Cache {
    private static final int MAX_CACHE_SIZE = 100;
    private final Map<String, Object> cache;
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    public Cache() {
        this.cache = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                boolean shouldRemove = size() > MAX_CACHE_SIZE;
                if (shouldRemove) {
                    logger.info("Удаление старейшей записи из кэша: ключ={}, значение={}",
                            eldest.getKey(), eldest.getValue());
                }
                return shouldRemove;
            }
        };
    }

    public void put(String key, Object value) {
        synchronized (cache) {
            cache.put(key, value);
            logger.info("Добавлено в кэш: ключ={}, значение={}", key, value);
        }
    }

    public Optional<Object> get(String key) {
        synchronized (cache) {
            Object value = cache.get(key);
            if (value != null) {
                logger.info("Попадание в кэш: ключ={}, значение={}", key, value);
            } else {
                logger.info("Промах кэша: ключ={}", key);
            }
            return Optional.ofNullable(value);
        }
    }

    public void remove(String key) {
        synchronized (cache) {
            Object removedValue = cache.remove(key);
            if (removedValue != null) {
                logger.info("Удалено из кэша: ключ={}, значение={}", key, removedValue);
            } else {
                logger.info("Попытка удалить несуществующий ключ из кэша: ключ={}", key);
            }
        }
    }

    public int size() {
        synchronized (cache) {
            int size = cache.size();
            logger.info("Текущий размер кэша: {}", size);
            return size;
        }
    }
}
