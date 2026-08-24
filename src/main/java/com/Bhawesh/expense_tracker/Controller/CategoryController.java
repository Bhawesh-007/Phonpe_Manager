package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.CategoryRequestDto;
import com.Bhawesh.expense_tracker.dto.CategoryResponseDto;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto categoryreq, @AuthenticationPrincipal User currentUser) {
        Category createdCategory = categoryService.createCategory(categoryreq, currentUser);
        return new ResponseEntity<>(CategoryResponseDto.fromEntity(createdCategory), HttpStatus.CREATED);
    }

    @GetMapping("/standard")
    public ResponseEntity<List<String>> getStandardCategories() {
        return ResponseEntity.ok(categoryService.getStandardCategories());
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories().stream().map(CategoryResponseDto::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<CategoryResponseDto>> getMyCategories(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(categoryService.getMyCategories(currentUser).stream().map(CategoryResponseDto::fromEntity).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDto categoryreq, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(CategoryResponseDto.fromEntity(categoryService.updateCategory(id, categoryreq, currentUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        categoryService.deleteCategory(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
