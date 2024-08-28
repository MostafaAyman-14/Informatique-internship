package com.library.app.controller;

import com.library.app.entity.Book;
import com.library.app.repository.BookRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {
    @Autowired
    BookRepos bookRepos;
    @GetMapping("/Book/")
    public List<Book> getAllBooks(){
    return bookRepos.findAll();
    }
}
