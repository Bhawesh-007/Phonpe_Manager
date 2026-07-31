package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.CategoryRequestDto;
import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequestDto categoryreq, @AuthenticationPrincipal User currentUser) {
        Category createdCategory = categoryService.createCategory(categoryreq, currentUser);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/me")
    public ResponseEntity<List<Category>> getMyCategories(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(categoryService.getMyCategories(currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto categoryreq, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryreq, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        categoryService.deleteCategory(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
