package com.Bhawesh.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class AnalyticsOverviewDto {
    private BigDecimal totalSpend;
    private Long expenseCount;
    private List<MonthlySpendDto> monthlySpend;
    private List<CategorySpendDto> categorySpend;

    @Data
    @AllArgsConstructor
    public static class MonthlySpendDto {
        private String month;
        private BigDecimal amount;
    }

    @Data
    @AllArgsConstructor
    public static class CategorySpendDto {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
    }
}
