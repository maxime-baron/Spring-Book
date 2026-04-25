package com.maximebaron.springbook.book;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("BookRepository Integration Tests")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("existsByIsbn")
    class ExistsByIsbn {

        @Test
        @DisplayName("should return true when ISBN exists")
        void shouldReturnTrueWhenIsbnExists() {
            boolean result = bookRepository.existsByIsbn("9782290210512");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false when ISBN does not exist")
        void shouldReturnFalseWhenIsbnDoesNotExist() {
            boolean result = bookRepository.existsByIsbn("9999999999999");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByIsbn")
    class FindByIsbn {

        @Test
        @DisplayName("should return book when ISBN exists")
        void shouldReturnBookWhenIsbnExists() {
            Optional<BookEntity> result = bookRepository.findByIsbn("9782290210512");

            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Kilomètre zéro");
        }

        @Test
        @DisplayName("should return empty when ISBN does not exist")
        void shouldReturnEmptyWhenIsbnDoesNotExist() {
            Optional<BookEntity> result = bookRepository.findByIsbn("9999999999999");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("should save new book successfully")
        void shouldSaveNewBookSuccessfully() {
            BookEntity book = BookEntity.builder()
                    .isbn("1111111111111")
                    .title("Test Book")
                    .author("Test Author")
                    .description("Test description")
                    .genre(BookGenre.TECHNOLOGY)
                    .format(BookFormat.ESSAY)
                    .pages(100)
                    .publishedAt(LocalDate.of(2020, 1, 1))
                    .rating(4.0)
                    .build();

            BookEntity saved = bookRepository.save(book);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getIsbn()).isEqualTo("1111111111111");
            assertThat(saved.getTitle()).isEqualTo("Test Book");
        }

        @Test
        @DisplayName("should throw exception when saving duplicate ISBN")
        void shouldThrowExceptionWhenSavingDuplicateIsbn() {
            BookEntity book = BookEntity.builder()
                    .isbn("9782290210512")
                    .title("Duplicate Book")
                    .author("Duplicate Author")
                    .description("Test description")
                    .genre(BookGenre.TECHNOLOGY)
                    .format(BookFormat.ESSAY)
                    .pages(100)
                    .publishedAt(LocalDate.of(2020, 1, 1))
                    .rating(4.0)
                    .build();

            assertThatThrownBy(() -> bookRepository.saveAndFlush(book))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("should update existing book")
        void shouldUpdateExistingBook() {
            Optional<BookEntity> existing = bookRepository.findByIsbn("9782290210512");
            BookEntity book = existing.get();
            book.setTitle("Updated Title");

            BookEntity updated = bookRepository.save(book);

            assertThat(updated.getTitle()).isEqualTo("Updated Title");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return book when ID exists")
        void shouldReturnBookWhenIdExists() {
            Optional<BookEntity> result = bookRepository.findById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getIsbn()).isEqualTo("9782290210512");
        }

        @Test
        @DisplayName("should return empty when ID does not exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            Optional<BookEntity> result = bookRepository.findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete existing book")
        void shouldDeleteExistingBook() {
            Optional<BookEntity> existing = bookRepository.findByIsbn("9782290210512");
            BookEntity book = existing.get();

            bookRepository.delete(book);
            entityManager.flush();

            Optional<BookEntity> result = bookRepository.findById(1L);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should do nothing when deleting non-existing book")
        void shouldDoNothingWhenDeletingNonExistingBook() {
            BookEntity nonExisting = BookEntity.builder()
                    .isbn("9999999999999")
                    .title("Non Existing")
                    .author("Author")
                    .description("Desc")
                    .genre(BookGenre.TECHNOLOGY)
                    .format(BookFormat.ESSAY)
                    .pages(100)
                    .publishedAt(LocalDate.of(2020, 1, 1))
                    .rating(4.0)
                    .build();

            bookRepository.delete(nonExisting);

            assertThat(bookRepository.count()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("findAll with Specification")
    class FindAllWithSpecification {

        @Test
        @DisplayName("should return all books when no filter")
        void shouldReturnAllBooksWhenNoFilter() {
            var result = bookRepository.findAll();

            assertThat(result).hasSize(5);
        }

        @Test
        @DisplayName("should filter by title")
        void shouldFilterByTitle() {
            Specification<BookEntity> spec = (root, _, cb) ->
                    cb.like(cb.lower(root.get("title")), "%kilomètre%");

            var result = bookRepository.findAll(spec);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getTitle()).isEqualTo("Kilomètre zéro");
        }

        @Test
        @DisplayName("should filter by author")
        void shouldFilterByAuthor() {
            Specification<BookEntity> spec = (root, _, cb) ->
                    cb.equal(root.get("author"), "Anne Alombert");

            var result = bookRepository.findAll(spec);

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("should filter by genre")
        void shouldFilterByGenre() {
            Specification<BookEntity> spec = (root, _, cb) ->
                    cb.equal(root.get("genre"), BookGenre.ROMANCE);

            var result = bookRepository.findAll(spec);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getTitle()).isEqualTo("The Whisperer ");
        }

        @Test
        @DisplayName("should combine multiple specifications")
        void shouldCombineMultipleSpecifications() {
            Specification<BookEntity> specTitle = (root, _, cb) ->
                    cb.like(cb.lower(root.get("title")), "%capital%");
            Specification<BookEntity> specGenre = (root, _, cb) ->
                    cb.equal(root.get("genre"), BookGenre.TECHNOLOGY);
            Specification<BookEntity> combined = specTitle.and(specGenre);

            var result = bookRepository.findAll(combined);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("count")
    class Count {

        @Test
        @DisplayName("should return 5 books from data.sql")
        void shouldReturn5BooksFromDataSql() {
            long count = bookRepository.count();

            assertThat(count).isEqualTo(5);
        }
    }
}
