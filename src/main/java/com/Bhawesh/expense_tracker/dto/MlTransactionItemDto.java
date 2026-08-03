package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MlTransactionItemDto {
    private Long categoryId;
    private String categoryName;
    private String note;
    private Double amount;
    private LocalDateTime timestamp;
}
//this would be extracted from the ml model and then this
//dto will be formed and further fed to the ml classification service