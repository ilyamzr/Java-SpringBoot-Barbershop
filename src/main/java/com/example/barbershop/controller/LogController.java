package com.example.barbershop.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private static final String LOG_DIRECTORY = "logs/";
    private static final String LOG_FILE_PREFIX = "barbershop-";
    private static final String LOG_FILE_EXTENSION = ".log";
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    @Operation(summary = "Get log file filtered by date and log level")
    @GetMapping("/logs")
    public ResponseEntity<byte[]> getLogFile(
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "all") String level) {

        logger.info("Processing log file request");

        if (!DATE_PATTERN.matcher(date).matches()) {
            logger.warn("Invalid date format received");
            return ResponseEntity.badRequest()
                    .body("Invalid date format. Use YYYY-MM-DD".getBytes());
        }

        String logFileName = LOG_DIRECTORY + LOG_FILE_PREFIX + date + LOG_FILE_EXTENSION;
        Path logFilePath = Paths.get(logFileName).normalize();

        if (!Files.exists(logFilePath)) {
            logger.warn("Requested log file not found");
            return ResponseEntity.notFound().build();
        }

        try (Stream<String> linesStream = Files.lines(logFilePath, StandardCharsets.UTF_8)) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment",
                    LOG_FILE_PREFIX + date + "-" + level + LOG_FILE_EXTENSION);

            logger.info("Log file processed successfully");
            List<String> filteredLines = filterLogLines(linesStream, level);
            byte[] logFileBytes = String.join("\n", filteredLines)
                    .getBytes(StandardCharsets.UTF_8);

            return new ResponseEntity<>(logFileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Error processing log file request", e);
            return ResponseEntity.internalServerError()
                    .body("Error reading log file".getBytes());
        }
    }

    private List<String> filterLogLines(Stream<String> linesStream, String level) {
        if ("all".equalsIgnoreCase(level)) {
            return linesStream.toList();
        }

        String logLevel = level.toUpperCase();
        Pattern logPattern = Pattern.compile(
                "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} " + logLevel + " ");

        return linesStream.filter(line -> logPattern.matcher(line).find())
                .toList();
    }
}