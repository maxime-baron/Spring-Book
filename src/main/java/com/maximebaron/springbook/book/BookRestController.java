package com.maximebaron.springbook.book;

import com.maximebaron.springbook.book.command.CreateBookCommand;
import com.maximebaron.springbook.book.command.UpdateBookCommand;
import com.maximebaron.springbook.book.dto.BookResponse;
import com.maximebaron.springbook.book.dto.CreateBookRequest;
import com.maximebaron.springbook.book.dto.BookFilterRequest;
import com.maximebaron.springbook.book.dto.PageableRequest;
import com.maximebaron.springbook.book.dto.UpdateBookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Everything about books")
public class BookRestController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookRestController(BookService bookService, BookMapper bookMapper) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @GetMapping
    @Operation(summary = "Retrieves list of books", description = "Retrieves a paginated list of books with filtering.")
    public Page<BookResponse> getAll(
            @ParameterObject @ModelAttribute BookFilterRequest filters,
            @ParameterObject @ModelAttribute PageableRequest pageableRequest) {
        return bookService.findAll(filters, pageableRequest.toPageable()).map(bookMapper::toBookResponse);
    }

    @GetMapping("/{isbn}")
    @Operation(summary = "Find book by identifier", description = "Retrieves details about a specific book using its ISBN.")
    public BookResponse get(@PathVariable String isbn) {
        BookEntity newBook = bookService.getBookByIsbn(isbn);
        return bookMapper.toBookResponse(newBook);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book", description = "Adds a new book.")
    public BookResponse post(@Valid @RequestBody CreateBookRequest book) {
        CreateBookCommand command = bookMapper.toCreateBookCommand(book);
        BookEntity newBook = bookService.createBook(command);
        return bookMapper.toBookResponse(newBook);
    }

    @PutMapping("/{isbn}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a book", description = "Updates information for a specific book.")
    public BookResponse put(@Valid @PathVariable String isbn, @RequestBody UpdateBookRequest book) {
        UpdateBookCommand command = bookMapper.toUpdateBookCommand(book);
        BookEntity newBook = bookService.updateBook(command, isbn);
        return bookMapper.toBookResponse(newBook);
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book", description = "Deletes a specific book.")
    public void delete(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
    }
}
