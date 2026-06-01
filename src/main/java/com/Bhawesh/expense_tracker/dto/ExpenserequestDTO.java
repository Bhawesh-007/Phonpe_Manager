package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ExpenserequestDTO {
    private Long accountId;
    private Long categoryId;
    private String note;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
