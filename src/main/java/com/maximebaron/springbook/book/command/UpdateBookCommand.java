package com.maximebaron.springbook.book.command;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateBookCommand(
        @Size(min = 1)
        String title,

        @Size(min = 1)
        String author,

        String description,
        BookGenre genre,
        BookFormat format,
        @Positive Integer pages,

        @PastOrPresent
        LocalDate publishedAt,

        @Min(0) @Max(5)
        Double rating
) {}
