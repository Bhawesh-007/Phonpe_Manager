package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    List<Account> findByUserId(Long userId);
    List<Account> findByUserIdAndAccountType(Long userId, AccountType accountType);
    Account findByUserIdAndUniqueName(Long userId, String uniqueName);
}

