package com.Bhawesh.expense_tracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StatementConfirmRequestDto {
    @NotNull(message = "Transactions are required")
    private List<@Valid StatementReviewItemDto> transactions;
}
