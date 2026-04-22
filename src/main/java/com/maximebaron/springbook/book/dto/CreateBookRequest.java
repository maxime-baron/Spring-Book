package com.maximebaron.springbook.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateBookRequest(
        @NotBlank(message = "The ISBN cannot be empty") String isbn,
        @NotBlank(message = "The title cannot be empty") String title,
        @NotBlank(message = "The author cannot be empty") String author,
        String description,
        @NotNull(message = "The genre cannot be empty") BookGenre genre,
        @NotNull(message = "The format cannot be empty") BookFormat format,
        @NotNull(message = "The number of pages cannot be empty") @Positive(message = "The number of pages cannot be less than 1") Integer pages,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull(message = "The publication date cannot be empty")
        @PastOrPresent(message = "The publication date cannot be in the future")
        LocalDate publishedAt,

        @NotNull(message = "The rating cannot be empty")
        @Min(value = 0, message = "The minimum value is 0") @Max(value = 5, message = "The maximum value is 5")
        Double rating
) {}
