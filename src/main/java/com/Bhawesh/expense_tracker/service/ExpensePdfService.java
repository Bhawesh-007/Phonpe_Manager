package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.StatementReviewItemDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.ExpenseSource;
import com.Bhawesh.expense_tracker.exception.BusinessRuleViolationException;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.CategoryRepository;
import com.Bhawesh.expense_tracker.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpensePdfService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    /** Validates every row before changing the balance, so confirmation is all-or-nothing. */
    @Transactional
    public List<Expense> saveConfirmedExpenses(Account account, User currentUser, List<StatementReviewItemDto> transactions) {
        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You do not have access to this account");
        }

        List<Category> categories = transactions.stream().map(item -> {
            Category category = categoryRepository.findById(item.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + item.getCategoryId()));
            if (!category.getUser().getId().equals(currentUser.getId())) {
                throw new UnauthorizedAccessException("You do not have access to category " + item.getCategoryId());
            }
            return category;
        }).toList();

        BigDecimal total = transactions.stream().map(StatementReviewItemDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (account.getBalance().compareTo(total) < 0) {
            throw new BusinessRuleViolationException("Insufficient balance for this statement import");
        }

        List<Expense> expenses = java.util.stream.IntStream.range(0, transactions.size())
                .mapToObj(index -> {
                    StatementReviewItemDto item = transactions.get(index);
                    return Expense.builder()
                            .account(account)
                            .category(categories.get(index))
                            .amount(item.getAmount())
                            .Description(item.getNote())
                            .timestamp(item.getTimestamp())
                            .source(ExpenseSource.PDF_IMPORT)
                            .build();
                }).toList();
        account.setBalance(account.getBalance().subtract(total));
        return expenseRepository.saveAll(expenses);
    }
}
