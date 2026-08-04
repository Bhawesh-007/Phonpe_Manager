package com.Bhawesh.expense_tracker.dto;

import com.Bhawesh.expense_tracker.entity.UploadedStatement;
import com.Bhawesh.expense_tracker.enums.StatementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StatementResponseDto {
    private Long id;
    private Long userId;
    private Long accountId;
    private String fileName;
    private StatementStatus status;
    private Integer extractedCount;
    private String errorMsg;
    private LocalDateTime uploadedAt;

    public static StatementResponseDto fromEntity(UploadedStatement statement) {
        return new StatementResponseDto(
                statement.getId(),
                statement.getUser().getId(),
                statement.getAccount().getId(),
                statement.getFileName(),
                statement.getStatus(),
                statement.getExtracted_count(),
                statement.getError_msg(),
                statement.getUploadedAt()
        );
    }
}
