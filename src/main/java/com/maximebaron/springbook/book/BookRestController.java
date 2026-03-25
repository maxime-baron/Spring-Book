package com.maximebaron.springbook.book;

import com.maximebaron.springbook.book.command.CreateBookCommand;
import com.maximebaron.springbook.book.command.UpdateBookCommand;
import com.maximebaron.springbook.book.dto.BookResponse;
import com.maximebaron.springbook.book.dto.CreateBookRequest;
import com.maximebaron.springbook.book.dto.BookFilterRequest;
import com.maximebaron.springbook.book.dto.UpdateBookRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookRestController {
    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookRestController(BookService bookService, BookMapper bookMapper) {
        this.bookMapper = bookMapper;
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public Page<BookResponse> getAll(@ModelAttribute BookFilterRequest filters, @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return bookService.findAll(filters, pageable)
                .map(bookMapper::toBookResponse);
    }

    @GetMapping("/books/{isbn}")
    public BookResponse get(@PathVariable String isbn) {
        BookEntity newBook = bookService.getBookByIsbn(isbn);
        return bookMapper.toBookResponse(newBook);
    }

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse post(@Valid @RequestBody CreateBookRequest book) {
        CreateBookCommand command = bookMapper.toCreateBookCommand(book);
        BookEntity newBook = bookService.createBook(command);
        return bookMapper.toBookResponse(newBook);
    }

    @PutMapping("/books/{isbn}")
    @ResponseStatus(HttpStatus.OK)
    public BookResponse put(@Valid @PathVariable String isbn, @RequestBody UpdateBookRequest book) {
        UpdateBookCommand command = bookMapper.toUpdateBookCommand(book);
        BookEntity newBook = bookService.updateBook(command, isbn);
        return bookMapper.toBookResponse(newBook);
    }

    @DeleteMapping("/books/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
    }
}
