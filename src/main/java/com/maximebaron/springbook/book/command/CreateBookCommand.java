package com.maximebaron.springbook.book.command;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateBookCommand(
        String isbn,
        @NotBlank String title,
        @NotBlank String author,
        String description,
        @NotNull BookGenre genre,
        @NotNull BookFormat format,
        @NotNull @Positive Integer pages,

        @NotNull
        @PastOrPresent
        LocalDate publishedAt,

        @NotNull
        @Min(0) @Max(5)
        Short rating
) {}
