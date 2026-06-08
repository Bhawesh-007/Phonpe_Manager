package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequestDto {
    @NotNull(message = "Account type is required (e.g., CHECKING, SAVINGS)")
    private AccountType accountType;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance cannot be negative")
    private BigDecimal initialBalance;
}
