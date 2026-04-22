package com.maximebaron.springbook.book.dto;

import com.maximebaron.springbook.book.SortField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Schema(description = "Pagination parameters")
public record PageableParams(
        @Schema(description = "Page number", example = "2", defaultValue = "0")
        @PositiveOrZero(message = "The page number must be at least 0")
        Integer page,
        @Schema(description = "Page size", example = "20", defaultValue = "20")
        @Max(value = 20, message = "The maximum number of items per page must not exceed 20")
        @Min(value = 1, message = "The minimum number of items per page must be at least 1")
        Integer size,
        @Schema(description = "Sort by field", defaultValue = "publishedAt")
        SortField sort,
        @Schema(description = "Sort direction", defaultValue = "DESC")
        Sort.Direction direction
) {
    public Pageable toPageable() {
        SortField sortField = sort != null ? sort : SortField.publishedAt;
        Sort.Direction sortDirection = direction != null ? direction : Sort.Direction.DESC;
        return PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(sortDirection, sortField.name())
        );
    }
}
