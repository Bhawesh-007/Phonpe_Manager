package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.ExpenseResponseDto;
import com.Bhawesh.expense_tracker.dto.ExpenserequestDTO;
import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> addExpense(@RequestBody ExpenserequestDTO request, @AuthenticationPrincipal User currentUser){
         Expense savedExpense = expenseService.createExpense(request, currentUser);
         return new ResponseEntity<>(ExpenseResponseDto.fromEntity(savedExpense), HttpStatus.CREATED);
    }

    // Caller's own expenses only — any authenticated user.
    @GetMapping("/me")
    public ResponseEntity<List<ExpenseResponseDto>> getMyExpenses(@RequestParam(required = false) String categoryName, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getMyExpenses(currentUser, categoryName).stream().map(ExpenseResponseDto::fromEntity).collect(Collectors.toList()));
    }

    // Every expense in the system — admin only (enforced in ExpenseService via @PreAuthorize).
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDto>> getAllExpense(@RequestParam(required = false) String categoryName) {
        return ResponseEntity.ok(expenseService.getAllExpense(categoryName).stream().map(ExpenseResponseDto::fromEntity).collect(Collectors.toList()));
    }

    // Single expense — owner or admin only.
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ExpenseResponseDto.fromEntity(expenseService.getExpenseById(id, currentUser)));
    }

    // Expenses for one account — owner or admin only.
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<ExpenseResponseDto>> getExpensesByAccount(@PathVariable Long accountId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getExpensesByAccount(accountId, currentUser).stream().map(ExpenseResponseDto::fromEntity).collect(Collectors.toList()));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable Long id, @RequestBody ExpenserequestDTO request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ExpenseResponseDto.fromEntity(expenseService.updateExpense(id, request, currentUser)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        expenseService.deleteExpense(id, currentUser);
        return ResponseEntity.ok().build();
    }
}
