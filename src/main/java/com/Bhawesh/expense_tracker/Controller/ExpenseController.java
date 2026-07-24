package com.Bhawesh.expense_tracker.Controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenserequestDTO request){
         Expense savedExpense = expenseService.createExpense(request);
         return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    // Caller's own expenses only — any authenticated user.
    @GetMapping("/me")
    public ResponseEntity<List<Expense>> getMyExpenses(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getMyExpenses(currentUser));
    }

    // Every expense in the system — admin only (enforced in ExpenseService via @PreAuthorize).
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpense() {
        return ResponseEntity.ok(expenseService.getAllExpense());
    }

    // Single expense — owner or admin only.
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getExpenseById(id, currentUser));
    }

    // Expenses for one account — owner or admin only.
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Expense>> getExpensesByAccount(@PathVariable Long accountId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.getExpensesByAccount(accountId, currentUser));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody ExpenserequestDTO request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request, currentUser));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        expenseService.deleteExpense(id, currentUser);
        return ResponseEntity.ok().build();
    }
}
