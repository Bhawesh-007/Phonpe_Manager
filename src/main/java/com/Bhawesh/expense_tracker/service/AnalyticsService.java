package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.AnalyticsOverviewDto;
import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final ExpenseRepository expenseRepository;
    private final AccountRepository accountRepository;

    public AnalyticsOverviewDto overview(User user, LocalDate from, LocalDate to, Long accountId) {
        if (accountId != null && accountRepository.findById(accountId)
                .map(account -> !account.getUser().getId().equals(user.getId())).orElse(true)) {
            throw new UnauthorizedAccessException("You do not have access to this account");
        }
        LocalDateTime start = (from == null ? LocalDate.now().withDayOfMonth(1) : from).atStartOfDay();
        LocalDateTime end = (to == null ? LocalDate.now() : to).plusDays(1).atStartOfDay();
        List<Expense> expenses = expenseRepository.findByAccount_User_Id(user.getId()).stream()
                .filter(e -> accountId == null || e.getAccount().getId().equals(accountId))
                .filter(e -> !e.getTimestamp().isBefore(start) && e.getTimestamp().isBefore(end)).toList();
        BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<AnalyticsOverviewDto.MonthlySpendDto> monthly = expenses.stream().collect(Collectors.groupingBy(
                        e -> YearMonth.from(e.getTimestamp()), Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)))
                .entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> new AnalyticsOverviewDto.MonthlySpendDto(e.getKey().toString(), e.getValue())).toList();
        List<AnalyticsOverviewDto.CategorySpendDto> categories = expenses.stream().collect(Collectors.groupingBy(
                        Expense::getCategory, Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)))
                .entrySet().stream().map(e -> new AnalyticsOverviewDto.CategorySpendDto(e.getKey().getId(), e.getKey().getName(), e.getValue()))
                .sorted(Comparator.comparing(AnalyticsOverviewDto.CategorySpendDto::getAmount).reversed()).toList();
        return new AnalyticsOverviewDto(total, (long) expenses.size(), monthly, categories);
    }
}
