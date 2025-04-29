package com.example.barbershop.controller;

import com.example.barbershop.log.LogField;
import com.example.barbershop.model.LogFileTask;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogField logFileId;
    private static final Path LOGS_DIRECTORY = Path.of("logs");

    public LogController(LogField logFileId) {
        this.logFileId = logFileId;
    }

    @Operation(summary = "Request log file sorted by date and logging level")
    @GetMapping
    public ResponseEntity<byte[]> getLogFile(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "all") String level) {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Path logFilePath = LOGS_DIRECTORY.resolve("barbershop-" + date + ".log").normalize();

        if (Files.exists(logFilePath) && logFilePath.startsWith(LOGS_DIRECTORY)) {
            try (var linesStream = Files.lines(logFilePath, StandardCharsets.UTF_8)) {
                List<String> lines;

                if (!"all".equalsIgnoreCase(level)) {
                    String logLevel = level.toUpperCase();
                    var logPattern = Pattern.compile(
                            "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} "
                                    + logLevel + " ");

                    lines = linesStream.filter(line -> logPattern.matcher(line).find())
                            .toList();
                } else {
                    lines = linesStream.toList();
                }

                byte[] logFileBytes = String.join("\n", lines)
                        .getBytes(StandardCharsets.UTF_8);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                headers.setContentDispositionFormData("attachment",
                        "barbershop-" + date + "-" + level + ".log");

                return new ResponseEntity<>(logFileBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a task to generate a log file asynchronously")
    @PostMapping("/generate")
    public ResponseEntity<String> createLogFileTask(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "all") String level) {

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return new ResponseEntity<>("Invalid date format. Please use 'yyyy-MM-dd'.", HttpStatus.BAD_REQUEST);
        }

        String taskId = logFileId.createLogFileTask(date, level);

        return new ResponseEntity<>(taskId, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Get the status of a log file generation task")
    @GetMapping("/status/{taskId}")
    public ResponseEntity<LogFileTask> getTaskStatus(@PathVariable String taskId) {
        LogFileTask task = logFileId.getTaskStatus(taskId);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @Operation(summary = "Download the generated log file by task ID")
    @GetMapping("/download/{taskId}")
    public ResponseEntity<byte[]> downloadLogFile(@PathVariable String taskId) {
        Path filePath = logFileId.getLogFilePath(taskId);
        if (filePath == null || !filePath.startsWith(LOGS_DIRECTORY)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            byte[] fileBytes = Files.readAllBytes(filePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", filePath.getFileName().toString());
            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
