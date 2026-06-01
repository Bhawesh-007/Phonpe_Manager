package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    private Long senderAccountId;
    private Long receiverAccountId;
    private String Note;
    private BigDecimal amount;
}
