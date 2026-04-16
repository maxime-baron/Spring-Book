package com.maximebaron.springbook.book.dto;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter parameters for books")
public record BookFilterRequest(
        @Schema(description = "Title filter", example = "Spring Boot")
        String title,
        @Schema(description = "Author filter", example = "John Doe")
        String author,
        @Schema(description = "Genre filter", example = "FICTION")
        BookGenre genre,
        @Schema(description = "Format filter", example = "NOVEL")
        BookFormat format
) {
}
