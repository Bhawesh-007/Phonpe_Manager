package com.Bhawesh.expense_tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "merchant_cache",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_merchant",
        columnNames = {"user_id", "merchant_name"}
    )
)
public class MerchantCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")

    private User user;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @CreationTimestamp
    @Column(name = "confirmed_at", updatable = false)
    private LocalDateTime confirmedAt;
}
