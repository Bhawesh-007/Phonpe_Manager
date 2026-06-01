package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.UploadedStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedStatementRepository extends JpaRepository<UploadedStatement, Long> {
    List<UploadedStatement> findByUserId(Long userId);;
}
