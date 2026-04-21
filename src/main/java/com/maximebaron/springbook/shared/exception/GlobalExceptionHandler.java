package com.maximebaron.springbook.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    private HttpStatus mapErrorCodeToStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case RESOURCE_ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case GENERIC_ERROR -> HttpStatus.BAD_REQUEST;
        };
    }

}
