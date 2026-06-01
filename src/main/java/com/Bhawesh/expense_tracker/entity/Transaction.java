package com.Bhawesh.expense_tracker.entity;

import com.Bhawesh.expense_tracker.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id" , nullable = false , referencedColumnName = "id")
    private Account senderAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id" , nullable = false , referencedColumnName = "id")
    private Account receiverAccount;
    @Column(nullable = false , precision = 19 , scale = 4)
    private BigDecimal amount;
    @Enumerated (EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionType;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private String note;
}
