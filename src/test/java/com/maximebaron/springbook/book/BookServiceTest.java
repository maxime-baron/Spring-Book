package com.maximebaron.springbook.book;

import com.maximebaron.springbook.book.command.CreateBookCommand;
import com.maximebaron.springbook.book.command.FindBooksQuery;
import com.maximebaron.springbook.book.command.UpdateBookCommand;
import com.maximebaron.springbook.book.exception.BookAlreadyExistsException;
import com.maximebaron.springbook.book.exception.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Spy private BookMapper bookMapper = Mappers.getMapper(BookMapper.class);
    @InjectMocks private BookService bookService;

    private static final String VALID_ISBN = "978-3-16-148410-0";

    private CreateBookCommand buildDefaultCreateCommand() {
        return CreateBookCommand.builder()
                .isbn(VALID_ISBN)
                .title("Effective Java")
                .author("Joshua Bloch")
                .description("Un super livre")
                .pages(416)
                .genre(BookGenre.TECHNOLOGY)
                .format(BookFormat.ESSAY)
                .publishedAt(LocalDate.of(2018, 1, 1))
                .rating(5.0)
                .build();
    }

    private UpdateBookCommand buildDefaultUpdateCommand() {
        return UpdateBookCommand.builder()
                .title("Nouveau Titre")
                .author("Nouvel Auteur")
                .description("Nouvelle Description")
                .genre(BookGenre.TECHNOLOGY)
                .format(BookFormat.ESSAY)
                .pages(500)
                .publishedAt(LocalDate.now())
                .rating((double) 5)
                .build();
    }

    @Nested
    class FindAll {
        private Pageable pageable;

        @BeforeEach
        void setUp() {
            pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC,"publishedAt"));
        }

        @Test
        void noFilters_returnsRepositoryResult() {
            FindBooksQuery filters = new FindBooksQuery(null, null, null, null);
            Page<BookEntity> expected = new PageImpl<>(List.of(new BookEntity()));

            when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(expected);

            Page<BookEntity> result = bookService.findAll(filters, pageable);

            assertThat(result).isEqualTo(expected);
            verify(bookRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void withTitle_passesSpecificationToRepository() {
            FindBooksQuery filters = new FindBooksQuery("dune", null, null, null);
            when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(Page.empty());

            bookService.findAll(filters, pageable);

            // We verify that the repository is being called correctly (the LIKE logic is in the DB,
            // and cannot be unit-tested without a Spring context)
            verify(bookRepository).findAll(any(Specification.class), eq(pageable));
        }

        @ParameterizedTest(name = "title=\"{0}\", author=\"{1}\"")
        @MethodSource("blankFilterProvider")
        void withBlankFilter_treatsAsNoFilter(String title, String author) {
            FindBooksQuery filters = new FindBooksQuery(title, author, null, null);
            when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(Page.empty());

            bookService.findAll(filters, pageable);

            verify(bookRepository).findAll(any(Specification.class), eq(pageable));
        }

        static Stream<Arguments> blankFilterProvider() {
            return Stream.of(
                    Arguments.of("   ", null),  // blank title
                    Arguments.of(null, "   ")   // blank author
            );
        }


        @Test
        void withMultipleFilters_callsRepositoryOnce() {
            FindBooksQuery filters = new FindBooksQuery(
                    "dune", "Frank Herbert", BookGenre.SCIENCE, BookFormat.NOVEL
            );
            when(bookRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(Page.empty());

            bookService.findAll(filters, pageable);

            verify(bookRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void pageableIsForwardedToRepository() {
            Pageable customPage = PageRequest.of(2, 2, Sort.by(Sort.Direction.DESC, "author"));
            FindBooksQuery filters = new FindBooksQuery(null, null, null, null);
            when(bookRepository.findAll(any(Specification.class), eq(customPage)))
                    .thenReturn(Page.empty());

            bookService.findAll(filters, customPage);

            verify(bookRepository).findAll(any(Specification.class), eq(customPage));
        }
    }

    @Nested
    class CreateBook {

        @Test
        void newIsbn_savesAndReturnsEntity() {
            // --- ARRANGE ---
            CreateBookCommand command = buildDefaultCreateCommand();

            when(bookRepository.existsByIsbn(command.isbn())).thenReturn(false);

            // --- ACT ---
            BookEntity actualEntity = bookService.createBook(command);

            // --- ASSERT ---
            assertThat(actualEntity)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "createdAt", "updatedAt")
                    .isEqualTo(command);
            verify(bookRepository).save(actualEntity);
        }

        @Test
        void existingIsbn_throwsBookAlreadyExistsException() {
            // --- ARRANGE ---
            CreateBookCommand command = buildDefaultCreateCommand();

            when(bookRepository.existsByIsbn(command.isbn())).thenReturn(true);

            // --- ACT & ASSERT ---
            assertThatThrownBy(() -> bookService.createBook(command))
                    .isInstanceOf(BookAlreadyExistsException.class);

            verify(bookRepository, never()).save(any(BookEntity.class));
            verifyNoInteractions(bookMapper);
        }
    }

    @Nested
    class GetBookByIsbn {
        @Test
        void existingIsbn_returnsBookEntity() {
            // --- ARRANGE ---
            BookEntity expectedBook = BookEntity.builder()
                    .isbn(VALID_ISBN)
                    .build();

            when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.of(expectedBook));

            // --- ACT ---
            BookEntity actualEntity = bookService.getBookByIsbn(VALID_ISBN);

            // --- ASSERT ---
            assertThat(actualEntity).isEqualTo(expectedBook);
            verify(bookRepository).findByIsbn(VALID_ISBN);
        }

        @Test
        void unknownIsbn_throwsBookNotFoundException() {
            // --- ARRANGE ---
            when(bookRepository.findByIsbn(VALID_ISBN)).thenReturn(Optional.empty());

            // --- ACT & ASSERT ---
            assertThatThrownBy(() -> bookService.getBookByIsbn(VALID_ISBN))
                    .isInstanceOf(BookNotFoundException.class);

            verify(bookRepository).findByIsbn(VALID_ISBN);
        }
    }

    @Nested
    class UpdateBook {

        @Test
        void existingIsbn_updatesAndReturnsEntity() {
            // --- ARRANGE ---
            String isbn = VALID_ISBN;
            UpdateBookCommand command = buildDefaultUpdateCommand();

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
            assertThat(actualEntity.getIsbn()).isEqualTo(isbn);

            verify(bookRepository).save(existingBook);
        }

        @Test
        void unknownIsbn_throwsBookNotFoundException() {
            // --- ARRANGE ---
            String isbn = VALID_ISBN;
            UpdateBookCommand command = buildDefaultUpdateCommand();

            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

            // --- ACT & ASSERT ---
            assertThatThrownBy(() -> bookService.updateBook(command, isbn))
                    .isInstanceOf(BookNotFoundException.class);

            verify(bookRepository).findByIsbn(isbn);
            verify(bookRepository, never()).save(any());
        }
    }

    @Nested
    class DeleteBook {

        @Test
        void existingIsbn_deletesEntity() {
            // --- ARRANGE ---
            String isbn = VALID_ISBN;

            BookEntity expectedEntity = new BookEntity();
            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(expectedEntity));

            // --- ACT ---
            bookService.deleteBook(isbn);

            // --- ASSERT ---

            verify(bookRepository).delete(expectedEntity);
            verify(bookRepository).findByIsbn(isbn);
        }

        @Test
        void unknownIsbn_throwsBookNotFoundException() {
            // --- ARRANGE ---
            String isbn = VALID_ISBN;

            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

            // --- ACT & ASSERT ---
            assertThatThrownBy(() -> bookService.deleteBook(isbn))
                    .isInstanceOf(BookNotFoundException.class);

            verify(bookRepository).findByIsbn(isbn);
        }
    }
}
