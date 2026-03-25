package com.maximebaron.springbook.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateBookRequest(
        @Size(min = 1)
        String title,

        @Size(min = 1)
        String author,

        String description,
        BookGenre genre,
        BookFormat format,

        @Positive
        Integer pages,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @PastOrPresent
        LocalDate publishedAt,

        @Min(0) @Max(5)
        Short rating
) {}
