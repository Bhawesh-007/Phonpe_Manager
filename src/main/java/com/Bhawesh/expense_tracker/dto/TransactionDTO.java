package com.Bhawesh.expense_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    @NotBlank(message = "Note cannot be empty")
    private String note;

    @Positive(message = "Expense amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @NotNull(message = "category id is required")
    private Long categoryId;
}
//see this dto is to ensure that any user cant modify internal database table manally dto restricts them to fill exactly
//what is required