package com.Bhawesh.expense_tracker.repository;

import com.Bhawesh.expense_tracker.entity.MerchantCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantCacheRepository extends JpaRepository<MerchantCache, Long> {

    Optional<MerchantCache> findByUserIdAndMerchantName(Long userId, String merchantName);
}
