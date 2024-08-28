package com.library.app.repository;

import com.library.app.entity.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatronRepos extends JpaRepository<Patron,Long> {
}
