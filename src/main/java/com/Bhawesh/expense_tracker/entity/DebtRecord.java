package com.Bhawesh.expense_tracker.entity;

import com.Bhawesh.expense_tracker.enums.DebtStatus;
import com.Bhawesh.expense_tracker.enums.DebtType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false , precision = 19 , scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private DebtType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DebtStatus status;
    private  String description;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime duedate;
}
