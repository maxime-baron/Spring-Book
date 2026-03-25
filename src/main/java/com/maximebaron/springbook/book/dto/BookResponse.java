package com.maximebaron.springbook.book.dto;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookResponse (
        String isbn,
        String title,
        String author,
        String description,
        BookGenre genre,
        BookFormat format,
        Integer pages,
        LocalDate publishedAt,
        Double rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){}
