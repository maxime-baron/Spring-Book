package com.maximebaron.springbook.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // On lie le code métier au status HTTP par défaut
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND),
    GENERIC_ERROR(HttpStatus.BAD_REQUEST);

    private final HttpStatus defaultStatus;

    ErrorCode(HttpStatus defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

}
