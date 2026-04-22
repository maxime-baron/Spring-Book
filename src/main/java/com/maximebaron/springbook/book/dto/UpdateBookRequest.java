package com.maximebaron.springbook.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateBookRequest(
        @Size(min = 1, message = "The ISBN must be in ISBN format if provided")
        String isbn,

        @Size(min = 1, message = "The title must be at least 1 character if provided")
        String title,

        @Size(min = 1, message = "The author must be at least 1 character if provided")
        String author,

        String description,
        BookGenre genre,
        BookFormat format,

        @Positive(message = "The number of pages cannot be less than 1")
        Integer pages,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @PastOrPresent(message = "The publication date cannot be in the future")
        LocalDate publishedAt,

        @Min(value = 0, message = "The minimum value is 0") @Max(value = 5, message = "The maximum value is 5")
        Double rating
) {}
