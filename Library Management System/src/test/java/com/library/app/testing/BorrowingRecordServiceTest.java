package com.library.app.testing;

import com.library.app.entity.Book;
import com.library.app.entity.BorrowingRecord;
import com.library.app.entity.Patron;
import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.repository.BorrowingRecordRepos;
import com.library.app.repository.BookRepos;
import com.library.app.repository.PatronRepos;
import com.library.app.security.SecurityConfig;
import com.library.app.service.BorrowingRecordService;
import com.library.app.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BorrowingRecordServiceTest {

    @Mock
    private BorrowingRecordRepos borrowingRecordRepository;

    @Mock
    private BookRepos bookRepository;

    @Mock
    private PatronRepos patronRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private SecurityConfig securityConfig;

    @InjectMocks
    private BorrowingRecordService borrowingRecordService;

    private Book book;
    private Patron patron;
    private BorrowingRecord borrowingRecord;

    @BeforeEach
    public void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        patron = new Patron();
        patron.setId(1L);
        patron.setName("Test Patron");
        patron.setContactInformation("test@example.com");

        borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.now());
        borrowingRecord.setReturnDate(LocalDate.now().plusWeeks(2));
        borrowingRecord.setId(1L);
    }

    @Test
    public void testBorrowBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(patronRepository.findById(1L)).thenReturn(Optional.of(patron));
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(borrowingRecord);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        BorrowingRecord result = borrowingRecordService.borrowBook(1L, 1L, LocalDate.now().plusWeeks(2));

        assertNotNull(result);
        assertEquals(book, result.getBook());
        assertEquals(patron, result.getPatron());
        assertEquals(LocalDate.now(), result.getBorrowDate());
        assertEquals(LocalDate.now().plusWeeks(2), result.getReturnDate());

        verify(bookRepository, times(1)).findById(1L);
        verify(patronRepository, times(1)).findById(1L);
        verify(borrowingRecordRepository, times(1)).save(any(BorrowingRecord.class));
        verify(auditService, times(1)).log(eq("BorrowingRecord"), eq(result.getId()), eq("BORROW"), eq("testuser"));
    }

    @Test
    public void testBorrowBook_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            borrowingRecordService.borrowBook(1L, 1L, LocalDate.now().plusWeeks(2));
        });

        verify(bookRepository, times(1)).findById(1L);
        verify(patronRepository, never()).findById(anyLong());
        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
        verify(auditService, never()).log(anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    public void testBorrowBook_PatronNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(patronRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            borrowingRecordService.borrowBook(1L, 1L, LocalDate.now().plusWeeks(2));
        });

        verify(bookRepository, times(1)).findById(1L);
        verify(patronRepository, times(1)).findById(1L);
        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
        verify(auditService, never()).log(anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    public void testReturnBook_Success() {
        when(borrowingRecordRepository.findByBookIdAndPatronId(1L, 1L)).thenReturn(Optional.of(borrowingRecord));
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(borrowingRecord);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        BorrowingRecord result = borrowingRecordService.returnBook(1L, 1L);

        assertNotNull(result);
        assertEquals(LocalDate.now(), result.getReturnDate());

        verify(borrowingRecordRepository, times(1)).findByBookIdAndPatronId(1L, 1L);
        verify(borrowingRecordRepository, times(1)).save(any(BorrowingRecord.class));
        verify(auditService, times(1)).log(eq("BorrowingRecord"), eq(result.getId()), eq("RETURN"), eq("testuser"));
    }

    @Test
    public void testReturnBook_NotFound() {
        when(borrowingRecordRepository.findByBookIdAndPatronId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            borrowingRecordService.returnBook(1L, 1L);
        });

        verify(borrowingRecordRepository, times(1)).findByBookIdAndPatronId(1L, 1L);
        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
        verify(auditService, never()).log(anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    public void testUpdateRecord_Success() {
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.of(borrowingRecord));
        when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(borrowingRecord);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        BorrowingRecord updatedRecord = borrowingRecordService.updateRecord(1L, borrowingRecord);

        assertNotNull(updatedRecord);
        assertEquals(borrowingRecord, updatedRecord);

        verify(borrowingRecordRepository, times(1)).findById(1L);
        verify(borrowingRecordRepository, times(1)).save(any(BorrowingRecord.class));
        verify(auditService, times(1)).log(eq("BorrowingRecord"), eq(updatedRecord.getId()), eq("UPDATE"), eq("testuser"));
    }

    @Test
    public void testUpdateRecord_NotFound() {
        when(borrowingRecordRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            borrowingRecordService.updateRecord(1L, borrowingRecord);
        });

        verify(borrowingRecordRepository, times(1)).findById(1L);
        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
        verify(auditService, never()).log(anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    public void testDeleteRecord() {
        doNothing().when(borrowingRecordRepository).deleteById(1L);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        borrowingRecordService.deleteBorrowingRecord(1L);

        verify(borrowingRecordRepository, times(1)).deleteById(1L);
        verify(auditService, times(1)).log(eq("BorrowingRecord"), eq(1L), eq("DELETE"), eq("testuser"));
    }
}
