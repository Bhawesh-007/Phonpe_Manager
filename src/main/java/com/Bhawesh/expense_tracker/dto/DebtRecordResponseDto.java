package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.entity.DebtRecord;
import com.Bhawesh.expense_tracker.enums.DebtStatus;
import com.Bhawesh.expense_tracker.enums.DebtType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DebtRecordResponseDto {
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal amount;
    private DebtType type;
    private DebtStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime duedate;

    public static DebtRecordResponseDto fromEntity(DebtRecord debtRecord) {
        return new DebtRecordResponseDto(
                debtRecord.getId(),
                debtRecord.getUser().getId(),
                debtRecord.getName(),
                debtRecord.getAmount(),
                debtRecord.getType(),
                debtRecord.getStatus(),
                debtRecord.getDescription(),
                debtRecord.getCreatedAt(),
                debtRecord.getDuedate()
        );
    }
}
