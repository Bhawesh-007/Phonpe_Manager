package com.Bhawesh.expense_tracker.entity;

import com.Bhawesh.expense_tracker.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false , referencedColumnName = "id")
    private User user;
    @Column(nullable = false , precision = 19 , scale = 4)
    private BigDecimal balance;
    @Enumerated (EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "account" , cascade = CascadeType.ALL)
    private List<Expense> expenses;
    @OneToMany(mappedBy ="senderAccount")
    private List<Transaction> sentTransactions;
    @OneToMany(mappedBy ="receiverAccount")
    private List<Transaction> receivedTransactions;
}
