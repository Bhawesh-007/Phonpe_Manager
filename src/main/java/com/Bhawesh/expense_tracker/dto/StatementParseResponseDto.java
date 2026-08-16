package com.Bhawesh.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatementParseResponseDto {
    private StatementResponseDto statement;
    private List<StatementReviewItemDto> transactions;
}
