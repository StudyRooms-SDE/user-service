package com.sde.project.user.config;

import com.sde.project.user.models.responses.ExceptionResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetailsNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@OpenAPIDefinition
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {DataIntegrityViolationException.class, DuplicateKeyException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(content = @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json"))
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ExceptionResponse responseBody = new ExceptionResponse(
                Instant.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {PermissionDeniedDataAccessException.class, AccessDeniedException.class, AuthenticationException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ApiResponse(content = @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json"))
    protected ResponseEntity<Object> handlePermissionDenied(RuntimeException ex, WebRequest request) {
        ExceptionResponse responseBody = new ExceptionResponse(Instant.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {DataRetrievalFailureException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json"))
    protected ResponseEntity<Object> handleDataRetrievalFailure(RuntimeException ex, WebRequest request) {
        ExceptionResponse responseBody = new ExceptionResponse(Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(content = @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json"))
    protected ResponseEntity<Object> handleOtherExceptions(RuntimeException ex, WebRequest request) {
        ExceptionResponse responseBody = new ExceptionResponse(Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ConnectionDetailsNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ApiResponse(content = @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json"))
    protected ResponseEntity<Object> handleExternalApiExceptions(RuntimeException ex, WebRequest request) {
        ExceptionResponse responseBody = new ExceptionResponse(Instant.now().toString(),
                HttpStatus.BAD_GATEWAY.value(),
                HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(responseBody, new HttpHeaders(), HttpStatus.BAD_GATEWAY);
    }

}

