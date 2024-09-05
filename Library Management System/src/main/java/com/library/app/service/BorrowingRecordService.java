package com.library.app.service;

import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.entity.BorrowingRecord;
import com.library.app.entity.Patron;
import com.library.app.entity.Book;
import com.library.app.repository.BorrowingRecordRepos;
import com.library.app.repository.BookRepos;
import com.library.app.repository.PatronRepos;
import com.library.app.security.SecurityConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingRecordService {

    @Autowired
    private BorrowingRecordRepos borrowingRecordRepository;

    @Autowired
    private BookRepos bookRepository;

    @Autowired
    private PatronRepos patronRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private SecurityConfig securityConfig;

    @Cacheable("borrowingRecords")
    public List<BorrowingRecord> getAllRecords() {
        return borrowingRecordRepository.findAll();
    }

    @Cacheable(value = "borrowingRecords", key = "#id")
    public BorrowingRecord getRecordById(Long id) {
        return borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
    }

    @Transactional
    @CachePut(value = "borrowingRecords", key = "#result.id")
    public BorrowingRecord borrowBook(Long bookId, Long patronId, LocalDate returnDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found"));

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.now());
        borrowingRecord.setReturnDate(returnDate);
        BorrowingRecord savedRecord = borrowingRecordRepository.save(borrowingRecord);

        auditService.log("BorrowingRecord", savedRecord.getId(), "BORROW", getCurrentUsername());
        return savedRecord;
    }

    @Transactional
    @CachePut(value = "borrowingRecords", key = "#result.id")
    public BorrowingRecord returnBook(Long bookId, Long patronId) {
        BorrowingRecord borrowingRecord = borrowingRecordRepository.findByBookIdAndPatronId(bookId, patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing record not found"));
        borrowingRecord.setReturnDate(LocalDate.now());
        BorrowingRecord updatedRecord = borrowingRecordRepository.save(borrowingRecord);

        auditService.log("BorrowingRecord", updatedRecord.getId(), "RETURN", getCurrentUsername());
        return updatedRecord;
    }

    @Transactional
    @CachePut(value = "borrowingRecords", key = "#id")
    public BorrowingRecord updateRecord(Long id, BorrowingRecord record) {
        BorrowingRecord existingRecord = getRecordById(id);
        existingRecord.setReturnDate(record.getReturnDate());
        existingRecord.setBook(record.getBook());
        existingRecord.setBorrowDate(record.getBorrowDate());
        existingRecord.setPatron(record.getPatron());
        BorrowingRecord updatedRecord = borrowingRecordRepository.save(existingRecord);

        auditService.log("BorrowingRecord", updatedRecord.getId(), "UPDATE", getCurrentUsername());
        return updatedRecord;
    }

    @Cacheable(value = "borrowingRecords", key = "#id")
    public BorrowingRecord getBorrowingRecordById(Long id) {
        return borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing record not found"));
    }

    @Transactional
    @CacheEvict(value = "borrowingRecords", key = "#id")
    public void deleteBorrowingRecord(Long id) {
        borrowingRecordRepository.deleteById(id);
        auditService.log("BorrowingRecord", id, "DELETE", getCurrentUsername());
    }

    private String getCurrentUsername() {
        return securityConfig.getCurrentUsername();
    }
}
