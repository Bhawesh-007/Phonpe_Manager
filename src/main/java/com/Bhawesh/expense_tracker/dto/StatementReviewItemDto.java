package com.Bhawesh.expense_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatementReviewItemDto {
    @NotNull(message = "Category is required")
    private Long categoryId;
    private String suggestedCategoryName;
    private boolean categoryNeedsReview;
    @NotBlank(message = "Note cannot be empty")
    private String note;
    @NotNull(message = "Expense amount is required")
    @Positive(message = "Expense amount must be greater than zero")
    private BigDecimal amount;
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
}
