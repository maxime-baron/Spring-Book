package com.maximebaron.springbook.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAny(Exception ex) {
        log.error("Une erreur inattendue est survenue : ", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur technique est survenue. Veuillez contacter l'administrateur.");
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex) {
        HttpStatus status = mapErrorCodeToStatus(ex.getErrorCode());
        return ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        body.setProperty("errors", fieldErrors);
        return body;
    }

    private HttpStatus mapErrorCodeToStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case RESOURCE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case GENERIC_ERROR -> HttpStatus.BAD_REQUEST;
        };
    }

    public record FieldError(String field, String message) {}

}
