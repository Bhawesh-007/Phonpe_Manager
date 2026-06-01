package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepsoitory extends JpaRepository<Category, Long> {
    List<Category> findByType(CategoryType type);
}
