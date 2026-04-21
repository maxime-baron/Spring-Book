package com.maximebaron.springbook.book;

import com.maximebaron.springbook.book.command.CreateBookCommand;
import com.maximebaron.springbook.book.command.FindBooksQuery;
import com.maximebaron.springbook.book.command.UpdateBookCommand;
import com.maximebaron.springbook.book.exception.BookAlreadyExistsException;
import com.maximebaron.springbook.book.exception.BookNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public Page<BookEntity> findAll(@Valid FindBooksQuery filters, Pageable pageable) {
        Specification<BookEntity> spec = (_, _, cb) -> cb.conjunction();

        if (filters.title() != null && !filters.title().isBlank()) {
            spec = spec.and((root, _, cb) -> cb.like(cb.lower(root.get("title")), "%" + filters.title().toLowerCase() + "%"));
        }
        if (filters.author() != null && !filters.author().isBlank()) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("author"), filters.author()));
        }
        if (filters.genre() != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("genre"), filters.genre()));
        }
        if (filters.format() != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.get("format"), filters.format()));
        }

        return bookRepository.findAll(spec, pageable);
    }

    public BookEntity getBookByIsbn (@NotBlank String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public BookEntity createBook (@Valid CreateBookCommand command) {
        boolean bookExist = bookRepository.existsByIsbn(command.isbn());

        if(bookExist){
            throw new BookAlreadyExistsException(command.title());
        }

        BookEntity newBook = bookMapper.toEntity(command);
        bookRepository.save(newBook);

        return newBook;
    }

    public BookEntity updateBook (@Valid UpdateBookCommand command, String isbn) {
        BookEntity existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        bookMapper.updateEntityFromCommand(command, existingBook);

        return bookRepository.save(existingBook);
    }

    public void deleteBook (@NotBlank String isbn) {
        BookEntity existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        bookRepository.delete(existingBook);
    }
}
