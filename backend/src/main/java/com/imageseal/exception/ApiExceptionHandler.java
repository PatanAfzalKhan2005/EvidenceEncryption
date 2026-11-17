package com.imageseal.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<?> missing(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(NoSuchFileException.class)
    ResponseEntity<?> missingFile(NoSuchFileException e) {
        log.warn("Image storage file is missing: {}", e.getFile());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "The image file is missing from server storage."));
    }

    @ExceptionHandler(SecurityException.class)
    ResponseEntity<?> forbidden(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "You are not allowed to access this resource."));
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<?> databaseFailure(DataAccessException e) {
        log.error("ImageSeal database request failed", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("message", "The MySQL database could not complete this request. Check that it is running and that the imageseal schema is current."));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> failure(Exception e) {
        log.error("Unhandled ImageSeal API error", e);
        String message = hasCause(e, javax.crypto.AEADBadTagException.class)
                ? "The stored image cannot be decrypted. Check that IMAGE_ENCRYPTION_KEY matches the key used when it was stored."
                : "The server could not complete this image request. Check the Spring Boot log for the underlying error.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", message));
    }

    private boolean hasCause(Throwable error, Class<? extends Throwable> type) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (type.isInstance(cause)) return true;
        }
        return false;
    }
}
