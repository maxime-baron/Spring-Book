package com.maximebaron.springbook.book.command;

import com.maximebaron.springbook.book.BookFormat;
import com.maximebaron.springbook.book.BookGenre;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record FindBooksQuery(
        @Size(min = 1, message = "The title filter must be at least 1 character if provided")
        String title,

        @Size(min = 1, message = "The author filter must be at least 1 character if provided")
        String author,

        BookGenre genre,
        BookFormat format
) {
}
