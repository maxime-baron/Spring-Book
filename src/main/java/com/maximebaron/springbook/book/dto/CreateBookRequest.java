package com.maximebaron.springbook.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateBookRequest(
        String isbn,
        @NotBlank String title,
        @NotBlank String author,
        String description,
        @NotNull BookGenre genre,
        @NotNull BookFormat format,
        @NotNull @Positive Integer pages,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull
        @PastOrPresent
        LocalDate publishedAt,

        @NotNull
        @Min(0) @Max(5)
        Double rating
) {}
