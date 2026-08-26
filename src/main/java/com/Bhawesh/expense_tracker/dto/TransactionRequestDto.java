package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    private String senderUniqueName;
    private String receiverUniqueName;
    private String Note;
    private BigDecimal amount;
}
