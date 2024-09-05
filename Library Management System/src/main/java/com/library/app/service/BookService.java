package com.library.app.service;
import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.entity.Book;
import com.library.app.repository.BookRepos;
import com.library.app.security.SecurityConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepos bookRepository;
    @Autowired
    private AuditService auditService;
    @Autowired
    private SecurityConfig user;
    @Cacheable("books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    @Cacheable(value = "books", key = "#id")
    public Book getBookById(Long id) {

        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    }
@Transactional
    @CachePut(value = "books", key = "#book.id")
    public Book addBook(Book book) {
        Book savedBook=bookRepository.save(book);
       String username=user.getCurrentUsername();
        auditService.log("Book", savedBook.getId(), "ADD", username);
        return savedBook;
    }

    @Transactional
    @CachePut(value = "books", key = "#id")
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setIsbn(bookDetails.getIsbn());
        Book updatedBook=bookRepository.save(book);
        String username=user.getCurrentUsername();
        auditService.log("Book", updatedBook.getId(), "UPDATE", username);
        return updatedBook;
    }

    @Transactional
    @CacheEvict(value = "books", key = "#id")
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
        String username=user.getCurrentUsername();
        auditService.log("Book", id, "DELETE", username);
    }
}



