package com.maximebaron.springbook.book.dto;

import jakarta.validation.constraints.NotBlank;

public record GetBookRequest(
        @NotBlank String isbn
) {
}
