package com.maximebaron.springbook.book.dto;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Filter parameters for books")
public record BookFilterParams(
        @Schema(description = "Title filter", example = "Spring Boot")
        @Size(min = 1, message = "The title filter must be at least 1 character if provided")
        String title,
        @Schema(description = "Author filter", example = "John Doe")
        @Size(min = 1, message = "The author filter must be at least 1 character if provided")
        String author,
        @Schema(description = "Genre filter", example = "FICTION")
        BookGenre genre,
        @Schema(description = "Format filter", example = "NOVEL")
        BookFormat format
) {
}
