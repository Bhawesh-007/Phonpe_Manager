package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.TransactionDTO;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.enums.ExpenseSource;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.CategoryRepository;
import com.Bhawesh.expense_tracker.repository.ExpenseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpensePdfService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    // Constructor Injection
    public ExpensePdfService(ExpenseRepository expenseRepository,
                          CategoryRepository categoryRepository,
                          AccountRepository accountRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }
    @Transactional
    public void SaveBulkExpenses(Long accountId , List<TransactionDTO> transactions){
        //converting list of dtos into list of entities
        Account accountproxy = accountRepository.getReferenceById(accountId);
        List<Expense>ExpensesToSave = transactions.stream().map(transactionDTO -> {
            Category categoryproxy = categoryRepository.getReferenceById(transactionDTO.getCategoryId());
            return Expense.builder()
                    .account(accountproxy)
                    .category(categoryproxy)
                    .amount(BigDecimal.valueOf(transactionDTO.getAmount()))
                    .Description(transactionDTO.getNote())
                    .timestamp(transactionDTO.getTimestamp())
                    .source(ExpenseSource.PDF_IMPORT)
                    .build();



        }).collect(Collectors.toList());
        expenseRepository.saveAll(ExpensesToSave);
    }
}
