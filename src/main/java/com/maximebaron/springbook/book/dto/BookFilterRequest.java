package com.maximebaron.springbook.book.dto;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;

public record BookFilterRequest(
        String title,
        String author,
        BookGenre genre,
        BookFormat format
) {
}
