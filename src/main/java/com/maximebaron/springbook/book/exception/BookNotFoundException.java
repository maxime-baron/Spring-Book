package com.maximebaron.springbook.book.exception;

import com.maximebaron.springbook.shared.exception.BusinessException;
import com.maximebaron.springbook.shared.exception.ErrorCode;

public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(String isbn) {
        super(String.format("The book with the ISBN '%s' doesn't exist.", isbn), ErrorCode.RESOURCE_NOT_FOUND);
    }
}
