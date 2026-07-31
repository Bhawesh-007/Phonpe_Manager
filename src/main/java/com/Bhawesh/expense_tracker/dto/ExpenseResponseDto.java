package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.enums.ExpenseSource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExpenseResponseDto {
    private Long id;
    private Long accountId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private ExpenseSource source;

    public static ExpenseResponseDto fromEntity(Expense expense) {
        return new ExpenseResponseDto(
                expense.getId(),
                expense.getAccount().getId(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getTimestamp(),
                expense.getSource()
        );
    }
}
