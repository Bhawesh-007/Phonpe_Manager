package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Category type is required (e.g., EXPENSE, INCOME)")
    private CategoryType categoryType;
}
