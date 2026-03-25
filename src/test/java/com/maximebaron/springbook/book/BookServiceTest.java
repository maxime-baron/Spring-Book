package com.maximebaron.springbook.book;

import com.maximebaron.springbook.book.command.CreateBookCommand;
import com.maximebaron.springbook.book.command.UpdateBookCommand;
import com.maximebaron.springbook.book.dto.BookFilterRequest;
import com.maximebaron.springbook.book.exception.BookAlreadyExistsException;
import com.maximebaron.springbook.book.exception.BookNotFoundException;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Spy
    private BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    @InjectMocks
    private BookService bookService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"publishedAt"));
    }

    @Test
    void findAll_noFilters_returnsRepositoryResult() {
        BookFilterRequest filters = new BookFilterRequest(null, null, null, null);
        Page<BookEntity> expected = new PageImpl<>(List.of(new BookEntity()));

        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expected);

        Page<BookEntity> result = bookService.findAll(filters, pageable);

        assertThat(result).isEqualTo(expected);
        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAll_withTitle_passesSpecificationToRepository() {
        BookFilterRequest filters = new BookFilterRequest("dune", null, null, null);
        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        bookService.findAll(filters, pageable);

        // On vérifie que le repo est bien appelé (la logique LIKE est dans la DB,
        // pas testable unitairement sans Spring context)
        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAll_withBlankAuthor_treatsAsNoFilter() {
        BookFilterRequest filters = new BookFilterRequest(null, "   ", null, null);
        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        bookService.findAll(filters, pageable);

        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAll_withBlankTitle_treatsAsNoFilter() {
        BookFilterRequest filters = new BookFilterRequest("   ", null, null, null);
        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        bookService.findAll(filters, pageable);

        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAll_withMultipleFilters_callsRepositoryOnce() {
        BookFilterRequest filters = new BookFilterRequest(
                "dune", "Frank Herbert", BookGenre.SCIENCE, BookFormat.NOVEL
        );
        when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());

        bookService.findAll(filters, pageable);

        verify(bookRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAll_pageableIsForwardedToRepository() {
        Pageable customPage = PageRequest.of(2, 2, Sort.by(Sort.Direction.DESC, "author"));
        BookFilterRequest filters = new BookFilterRequest(null, null, null, null);
        when(bookRepository.findAll(any(Specification.class), eq(customPage)))
                .thenReturn(Page.empty());

        bookService.findAll(filters, customPage);

        verify(bookRepository).findAll(any(Specification.class), eq(customPage));
    }

    @Test
    void should_create_book_successfully() {
        // --- ARRANGE ---
        CreateBookCommand command = CreateBookCommand.builder()
                .isbn("978-3-16-148410-0")
                .title("Effective Java")
                .author("Joshua Bloch")
                .description("Un super livre")
                .pages(416)
                .genre(BookGenre.TECHNOLOGY)
                .format(BookFormat.ESSAY)
                .publishedAt(LocalDate.of(2018, 1, 1))
                .rating((double) 5)
                .build();

        BookEntity expectedEntity = new BookEntity();

        when(bookRepository.existsByIsbn(command.isbn())).thenReturn(false);
        when(bookMapper.toEntity(command)).thenReturn(expectedEntity);

        // --- ACT ---
        BookEntity actualEntity = bookService.createBook(command);

        // --- ASSERT ---
        assertThat(actualEntity).isEqualTo(expectedEntity);
        verify(bookRepository, times(1)).save(expectedEntity);
    }

    @Test
    void should_throw_exception_when_isbn_already_exists() {
        // --- ARRANGE ---
        CreateBookCommand command = CreateBookCommand.builder()
                .isbn("978-3-16-148410-0")
                .title("Effective Java")
                .author("Joshua Bloch")
                .description("Un super livre")
                .pages(416)
                .genre(BookGenre.TECHNOLOGY)
                .format(BookFormat.ESSAY)
                .publishedAt(LocalDate.of(2018, 1, 1))
                .rating((double) 5)
                .build();

        when(bookRepository.existsByIsbn(command.isbn())).thenReturn(true);

        // --- ACT & ASSERT ---
        assertThatThrownBy(() -> bookService.createBook(command))
                .isInstanceOf(BookAlreadyExistsException.class);

        verify(bookRepository, never()).save(any(BookEntity.class));
        verifyNoInteractions(bookMapper);
    }

    @Test
    void should_return_book_entity_successfuly(){
        // --- ARRANGE ---
        String isbn = "978-3-16-148410-0";
        BookEntity expectedBook = BookEntity.builder()
                .isbn("978-3-16-148410-0")
                .build();

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(expectedBook));

        // --- ACT ---
        BookEntity actualEntity = bookService.getBookByIsbn(isbn);

        // --- ASSERT ---
        assertThat(actualEntity).isEqualTo(expectedBook);
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    void should_throw_exception_when_isbn_doesnt_exist(){
        // --- ARRANGE ---
        String isbn = "978-3-16-148410-0";
        BookEntity expectedBook = BookEntity.builder()
                .isbn("978-3-16-148410-0")
                .build();

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThatThrownBy(() -> bookService.getBookByIsbn(isbn))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    void should_update_book_successfuly(){
        // --- ARRANGE ---
        String isbn = "978-3-16-148410-0";

        UpdateBookCommand command = UpdateBookCommand.builder()
                .title("Nouveau Titre")
                .author("Nouvel Auteur")
                .description("Nouvelle Description")
                .genre(BookGenre.TECHNOLOGY)
                .format(BookFormat.ESSAY)
                .pages(500)
                .publishedAt(LocalDate.now())
                .rating((double) 5)
                .build();

        BookEntity existingBook = BookEntity.builder().isbn(isbn).build();

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // --- ACT ---
        BookEntity actualEntity = bookService.updateBook(command, isbn);

        // --- ASSERT ---
        assertThat(actualEntity)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(command);

        verify(bookRepository).save(existingBook);
    }

    @Test
    void should_throw_exception_when_book_doesnt_exist(){
        // --- ARRANGE ---
        String isbn = "978-3-16-148410-0";
        UpdateBookCommand command = UpdateBookCommand.builder()
                .description("Un super livre")
                .pages(416)
                .build();

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThatThrownBy(() -> bookService.updateBook(command, isbn))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).findByIsbn(isbn);
    }

    public void deleteBook (@NotBlank String isbn) {
        BookEntity existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        bookRepository.delete(existingBook);
    }

    @Test
    void should_delete_book(){
        // --- ARRANGE ---
        String isbn = "978-3-16-148410-0";

        BookEntity expectedEntity = new BookEntity();
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(expectedEntity));

        // --- ACT ---
        bookService.deleteBook(isbn);

        // --- ASSERT ---

        verify(bookRepository, times(1)).delete(expectedEntity);
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    void should_throw_exception_when_there_is_not_book_to_delete(){
        // --- ARRANGE ---
        String isbn = "978-3-16-148410-0";

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThatThrownBy(() -> bookService.deleteBook(isbn))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository).findByIsbn(isbn);
    }

    @Transactional(readOnly = true)
    public Page<BookEntity> findAll(BookFilterRequest filters, Pageable pageable) {
        Specification<BookEntity> spec = (root, query, cb) -> cb.conjunction();

        if (filters.title() != null && !filters.title().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + filters.title().toLowerCase() + "%"));
        }
        if (filters.author() != null && !filters.author().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("author"), filters.author()));
        }
        if (filters.genre() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("genre"), filters.genre()));
        }
        if (filters.format() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("format"), filters.format()));
        }

        return bookRepository.findAll(spec, pageable);
    }

    @Test
    void should_return_paginated_book_list() {
        // --- ARRANGE ---

        // --- ACT ---

        // --- ASSERT ---

    }

}
