package com.library.app.testing;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.library.app.entity.Book;
import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.repository.BookRepos;
import com.library.app.security.SecurityConfig;
import com.library.app.service.BookService;
import com.library.app.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepos bookRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private SecurityConfig securityConfig;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublicationYear(2020);
        book.setIsbn("1234567890");
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));

        List<Book> books = bookService.getAllBooks();

        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(book, books.get(0));
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(1L);

        assertNotNull(foundBook);
        assertEquals(book, foundBook);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void testAddBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");

        Book savedBook = bookService.addBook(book);

        assertNotNull(savedBook);
        assertEquals(book, savedBook);
        verify(auditService, times(1)).log(eq("Book"), eq(book.getId()), eq("ADD"), eq("testuser"));
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");

        Book updatedBook = bookService.updateBook(1L, book);

        assertNotNull(updatedBook);
        assertEquals(book, updatedBook);
        verify(auditService, times(1)).log(eq("Book"), eq(book.getId()), eq("UPDATE"), eq("testuser"));
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(1L, book));
    }

    @Test
    void testDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
        verify(auditService, times(1)).log(eq("Book"), eq(1L), eq("DELETE"), eq("testuser"));
    }
}
