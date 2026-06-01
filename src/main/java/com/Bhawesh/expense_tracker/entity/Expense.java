package com.Bhawesh.expense_tracker.entity;

import com.Bhawesh.expense_tracker.enums.ExpenseSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id" , nullable = false , referencedColumnName = "id")
    private Account account;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "category_id" , nullable = false , referencedColumnName = "id")
    private Category category;
    @Column(nullable = false , precision = 19 , scale = 4)
    private BigDecimal amount;
    private String Description;
    @Column (nullable = false)
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private ExpenseSource source;
}
