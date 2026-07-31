package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccountResponseDto {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private AccountType accountType;
    private LocalDateTime createdAt;

    public static AccountResponseDto fromEntity(Account account) {
        return new AccountResponseDto(
                account.getId(),
                account.getUser().getId(),
                account.getBalance(),
                account.getAccountType(),
                account.getCreatedAt()
        );
    }
}
