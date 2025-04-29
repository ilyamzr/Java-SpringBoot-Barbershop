package com.example.barbershop.log;

import com.example.barbershop.model.LogFileTask;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogField {

    final Map<String, LogFileTask> tasks = new ConcurrentHashMap<>();
    private final LogFileGenerator generatorService;

    @Autowired
    public LogField(LogFileGenerator generatorService) {
        this.generatorService = generatorService;
    }

    @SneakyThrows
    public String createLogFileTask(String date, String level) {
        String taskId = UUID.randomUUID().toString();
        LogFileTask task = new LogFileTask(taskId);
        tasks.put(taskId, task);
        generatorService.generateLogFileAsync(task, date, level);
        return taskId;
    }

    public LogFileTask getTaskStatus(String taskId) {
        return tasks.get(taskId);
    }

    public Path getLogFilePath(String taskId) {
        LogFileTask task = tasks.get(taskId);
        if (task != null && "COMPLETED".equals(task.getStatus())) {
            return task.getFilePath();
        }
        return null;
    }
}