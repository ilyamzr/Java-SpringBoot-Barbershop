package com.example.barbershop.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Cache {
    private static final int MAX_CACHE_SIZE = 2;
    private final Map<String, CacheEntry> cacheMap;
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);
    private static final Duration TTL = Duration.ofMinutes(1);

    private static class CacheEntry {
        private final Object value;
        private final Instant createdAt;

        public CacheEntry(Object value) {
            this.value = value;
            this.createdAt = Instant.now();
        }

        public boolean isExpired() {
            return Instant.now().isAfter(createdAt.plus(TTL));
        }
    }

    public Cache() {
        this.cacheMap = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                boolean shouldRemove = size() > MAX_CACHE_SIZE;
                if (shouldRemove) {
                    logger.info("Переполнение кэша. Удаление старейшей записи из кэша: ключ={}",
                            eldest.getKey());
                }
                return shouldRemove;
            }
        };
    }

    public void put(String key, Object value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheEntry(value));
            logger.info("Добавлено в кэш: ключ={}", key);
        }
    }

    public Optional<Object> get(String key) {
        synchronized (cacheMap) {
            CacheEntry entry = cacheMap.get(key);
            if (entry != null) {
                logger.info("Попадание в кэш: ключ={}", key);
                return Optional.of(entry.value);
            } else {
                logger.info("Промах кэша: ключ={}", key);
                return Optional.empty();
            }
        }
    }

    public void remove(String key) {
        synchronized (cacheMap) {
            CacheEntry removedEntry = cacheMap.remove(key);
            if (removedEntry != null) {
                logger.info("Удалено из кэша: ключ={}", key);
            } else {
                logger.info("Попытка удалить несуществующий ключ из кэша: ключ={}", key);
            }
        }
    }

    private void cleanUp() {
        logger.info("Очистка кэша");
        cacheMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public int size() {
        synchronized (cacheMap) {
            cleanUp();
            int size = cacheMap.size();
            logger.info("Текущий размер кэша: {}", size);
            return size;
        }
    }
}