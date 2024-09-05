package com.library.app.service;

import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.entity.BorrowingRecord;
import com.library.app.entity.Patron;
import com.library.app.entity.Book;
import com.library.app.repository.BorrowingRecordRepos;
import com.library.app.repository.BookRepos;
import com.library.app.repository.PatronRepos;
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
    @Cacheable("borrowing record")
    public List<BorrowingRecord> getAllRecords() {
        return borrowingRecordRepository.findAll();
    }
    @Cacheable(value = "borrowing record", key = "#id")
    public BorrowingRecord getRecordById(Long id) {
        return borrowingRecordRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Record not found"));
    }
    @Transactional
    @CachePut(value = "borrowingRecords", key = "#result.id")
    public BorrowingRecord borrowBook(Long bookId, Long patronId,LocalDate returnDate) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        Patron patron = patronRepository.findById(patronId).orElseThrow(() -> new ResourceNotFoundException("Patron not found"));

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.now());
        borrowingRecord.setReturnDate(returnDate);
        return borrowingRecordRepository.save(borrowingRecord);
    }

    @CachePut(value = "borrowingRecords", key = "#result.id")
    @Transactional
    public BorrowingRecord returnBook(Long bookId, Long patronId) {
        BorrowingRecord borrowingRecord = borrowingRecordRepository.findByBookIdAndPatronId(bookId, patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing record not found"));
        borrowingRecord.setReturnDate(LocalDate.now());
        return borrowingRecordRepository.save(borrowingRecord);
    }
    @Transactional
    @CachePut(value = "borrowing record", key = "#id")
    public BorrowingRecord updateRecord(Long id, BorrowingRecord record) {
        BorrowingRecord newRecord = getRecordById(id);
        newRecord.setReturnDate(record.getReturnDate());
        newRecord.setBook(record.getBook());
        newRecord.setBorrowDate(record.getBorrowDate());
        newRecord.setPatron(record.getPatron());
        return borrowingRecordRepository.save(newRecord);
    }
    @Cacheable(value = "borrowingRecords", key = "#id")
    public BorrowingRecord getBorrowingRecordById(Long id) {
        return borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing record not found"));
    }

    @CacheEvict(value = "borrowingRecords", key = "#id")
    @Transactional
    public void deleteBorrowingRecord(Long id) {
        borrowingRecordRepository.deleteById(id);
    }
}


