package com.Bhawesh.expense_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

//now this will by user entity
@Entity
@Data
@NoArgsConstructor
@Builder
@Table(name = "app_users")
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String PasswordHash;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Account> accounts;
    @OneToMany (mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<UploadedStatement> uploadedStatements;
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<DebtRecord> debtRecords;
}
