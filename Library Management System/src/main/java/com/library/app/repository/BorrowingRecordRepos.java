package com.library.app.repository;

import com.library.app.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRecordRepos extends JpaRepository<BorrowingRecord,Long> {
}
