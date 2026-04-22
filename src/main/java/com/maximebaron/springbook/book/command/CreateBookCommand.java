package com.maximebaron.springbook.book.command;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateBookCommand(
        @NotBlank(message = "The ISBN cannot be empty") String isbn,
        @NotBlank(message = "The title cannot be empty") String title,
        @NotBlank(message = "The author cannot be empty") String author,
        String description,
        @NotNull(message = "The genre cannot be empty") BookGenre genre,
        @NotNull(message = "The format cannot be empty") BookFormat format,
        @NotNull(message = "The number of pages cannot be empty") @Positive(message = "The number of pages cannot be less than 1") Integer pages,

        @NotNull(message = "The publication date cannot be empty")
        @PastOrPresent(message = "The publication date cannot be in the future")
        LocalDate publishedAt,

        @NotNull(message = "The rating cannot be empty")
        @Min(value = 0, message = "The minimum value is 0") @Max(value = 5, message = "The maximum value is 5")
        Double rating
) {}
