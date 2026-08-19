package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.CategoryRequestDto;
import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.Role;
import com.Bhawesh.expense_tracker.exception.BusinessRuleViolationException;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.CategoryRepository;
import com.Bhawesh.expense_tracker.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private static final int MAX_CATEGORIES_PER_USER = 20;

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public Category createCategory(CategoryRequestDto categoryreq, User currentUser) {
        if (categoryRepository.countByUserId(currentUser.getId()) >= MAX_CATEGORIES_PER_USER) {
            throw new BusinessRuleViolationException("Cannot create more than " + MAX_CATEGORIES_PER_USER + " categories");
        }
        Category category = Category.builder()
                .name(categoryreq.getCategoryName())
                .type(categoryreq.getCategoryType())
                .user(currentUser)
                .description(categoryreq.getDescription())
                .build();
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getMyCategories(User currentUser) {
        return categoryRepository.findByUserId(currentUser.getId());
    }

    @Transactional
    public Category updateCategory(Long categoryId, CategoryRequestDto categoryreq, User currentUser) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        assertOwnerOrAdmin(category, currentUser);
        category.setName(categoryreq.getCategoryName());
        category.setType(categoryreq.getCategoryType());
        category.setDescription(categoryreq.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId, User currentUser) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        assertOwnerOrAdmin(category, currentUser);
        if (expenseRepository.existsByCategory_Id(categoryId)) {
            throw new BusinessRuleViolationException("Cannot delete a category that is used by existing expenses");
        }
        categoryRepository.delete(category);
    }

    private void assertOwnerOrAdmin(Category category, User currentUser) {
        boolean isOwner = category.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You do not have access to this resource");
        }
    }
}
