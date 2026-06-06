package com.Bhawesh.expense_tracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkRequestDTO {
    private Long accountId;
    private List<TransactionDTO> transactions;

}
