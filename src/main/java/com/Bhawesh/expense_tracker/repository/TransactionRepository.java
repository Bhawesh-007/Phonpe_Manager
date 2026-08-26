package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TransactionRepository extends JpaRepository<Transaction , Long> {
    @Query("SELECT t FROM Transaction t WHERE t.senderAccount.user.id = :userId OR t.receiverAccount.user.id = :userId")
    List<Transaction> findAllByUserId(@Param("userId") Long userId);

    boolean existsBySenderAccount_IdOrReceiverAccount_Id(Long senderAccountId, Long receiverAccountId);
}
