package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.DebtRecord;
import com.Bhawesh.expense_tracker.enums.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRecordRepository extends JpaRepository<DebtRecord, Long> {
    java.util.List<DebtRecord> findByUserId(Long userId);
    List<DebtRecord> findByUserIdAndStatus(Long userId , DebtStatus status);
}
