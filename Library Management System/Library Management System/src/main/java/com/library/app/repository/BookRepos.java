package com.library.app.repository;

import com.library.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepos extends JpaRepository<Book,Long> {
}
