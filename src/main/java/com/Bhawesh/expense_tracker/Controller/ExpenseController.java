package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.ExpenserequestDTO;
import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")

public class ExpenseController {
    private final ExpenseService expenseService;
    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody  ExpenserequestDTO request){
         Expense savedExpense = expenseService.createExpense(request);
         return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }
    // Route 1: Get all expenses
    @GetMapping
    public ResponseEntity<java.util.List<Expense>> getAllExpense() {
        return ResponseEntity.ok(expenseService.getAllExpense());
    }

    // Route 2: Get a single expense by ID
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // Route 3: Get all expenses for a specific account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<java.util.List<Expense>> getExpensesByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(expenseService.getExpensesByAccount(accountId));
    }
}
