package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense , Long> {
    List<Expense> findByAccountId(Long accountId);
    List<Expense> findByAccountIdAndTimestampBetween(Long accountId , LocalDateTime startdate , LocalDateTime enddate);

}
