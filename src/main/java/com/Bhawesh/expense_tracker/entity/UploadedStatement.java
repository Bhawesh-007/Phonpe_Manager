package com.Bhawesh.expense_tracker.entity;

import com.Bhawesh.expense_tracker.enums.DebtStatus;
import com.Bhawesh.expense_tracker.enums.DebtType;
import com.Bhawesh.expense_tracker.enums.StatementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UploadedStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatementStatus status;
    private Integer Extracted_count;
    private String error_msg;
    @CreationTimestamp
    private LocalDateTime uploadedAt;


}
