package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.enums.DebtStatus;
import com.Bhawesh.expense_tracker.enums.DebtType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DebtRecordRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Debt type is required (e.g., BORROWED, DEBT)")
    private DebtType type;

    @NotNull(message = "Debt status is required")
    private DebtStatus status;

    private String description;

    private LocalDateTime duedate;
}
