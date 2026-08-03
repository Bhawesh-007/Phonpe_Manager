package com.Bhawesh.expense_tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MlTransactionResponseDto {
    private String status;

    @JsonProperty("total_extracted")
    private Integer totalExtracted;

    private List<MlTransactionItemDto> data;
}
