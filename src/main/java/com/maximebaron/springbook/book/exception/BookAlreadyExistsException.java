package com.maximebaron.springbook.book.exception;

import com.maximebaron.springbook.shared.exception.BusinessException;
import com.maximebaron.springbook.shared.exception.ErrorCode;

public class BookAlreadyExistsException extends BusinessException {
    public BookAlreadyExistsException(String title) {
        super(String.format("The book '%s' already exist.", title), ErrorCode.RESOURCE_ALREADY_EXISTS);
    }
}
