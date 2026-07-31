package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private CategoryType type;
    private Long userId;

    public static CategoryResponseDto fromEntity(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getUser().getId()
        );
    }
}
