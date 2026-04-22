package com.maximebaron.springbook.book.command;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateBookCommand(
        @Size(min = 1, message = "The ISBN cannot be blank")
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

        @PastOrPresent(message = "The publication date cannot be in the future")
        LocalDate publishedAt,

        @Min(value = 0, message = "The minimum value is 0") @Max(value = 5, message = "The maximum value is 5")
        Double rating
) {}
