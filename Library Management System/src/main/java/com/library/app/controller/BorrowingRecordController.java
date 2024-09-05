package com.library.app.controller;

import com.library.app.entity.BorrowingRecord;
import com.library.app.entity.Patron;
import com.library.app.service.BorrowingRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/Record")
public class BorrowingRecordController {
    @Autowired
    private BorrowingRecordService borrowingRecordService;

    @GetMapping
    public List<BorrowingRecord> getAllRecords(){
      return borrowingRecordService.getAllRecords();
    }

    @GetMapping("/{id}")
    public BorrowingRecord getRecordById(@PathVariable Long id){
       return borrowingRecordService.getRecordById(id);
    }

    @PostMapping
    public BorrowingRecord borrowBook( @Valid @RequestBody BorrowingRecord borrowing) {
        return borrowingRecordService.borrowBook(borrowing.getBook().getId(), borrowing.getPatron().getId(),borrowing.getReturnDate());
    }

    @PutMapping("/return/{bookId}/patron/{patronId}")
    public BorrowingRecord returnBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        return borrowingRecordService.returnBook(bookId, patronId);
    }
    @PutMapping("/updateRecord/{id}")
    public BorrowingRecord updateRecord(@PathVariable Long id,@Valid @RequestBody BorrowingRecord record){
        return borrowingRecordService.updateRecord(id,record);
    }

    @DeleteMapping("/deleteRecord/{Id}")
            public void deleteRecord(@PathVariable Long Id) {
         borrowingRecordService.deleteBorrowingRecord(Id);
    }
}

