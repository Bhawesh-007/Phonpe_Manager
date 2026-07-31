package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.entity.Transaction;
import com.Bhawesh.expense_tracker.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private Long senderAccountId;
    private Long receiverAccountId;
    private BigDecimal amount;
    private TransactionStatus transactionType;
    private LocalDateTime createdAt;
    private String note;

    public static TransactionResponseDto fromEntity(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getSenderAccount().getId(),
                transaction.getReceiverAccount().getId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getCreatedAt(),
                transaction.getNote()
        );
    }
}
