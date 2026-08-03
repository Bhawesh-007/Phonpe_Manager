package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(CategoryType type);
    long countByUserId(Long userId);
    List<Category> findByUserId(Long userId);
    Optional<Category> findByNameIgnoreCaseAndUserId(String name, Long userId);
}