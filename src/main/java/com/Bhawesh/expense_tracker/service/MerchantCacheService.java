package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.entity.MerchantCache;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.repository.MerchantCacheRepository;
import com.Bhawesh.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantCacheService {

    private final MerchantCacheRepository merchantCacheRepository;
    private final UserRepository userRepository;


    public String lookup(Long userId, String merchantName) {
        return merchantCacheRepository
                .findByUserIdAndMerchantName(userId, merchantName.toUpperCase())
                .map(MerchantCache::getCategoryName)
                .orElse(null);
    }

    /**
     * Saves or updates a merchant → category mapping for a user.
     * If the merchant already exists for this user, the category is updated (upsert).
     */
    public MerchantCache save(Long userId, String merchantName, String categoryName) {
        User user = userRepository.findById((long) userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        String normalizedMerchant = merchantName.toUpperCase();

        // Upsert: update existing entry or create a new one
        Optional<MerchantCache> existing = merchantCacheRepository
                .findByUserIdAndMerchantName(userId, normalizedMerchant);

        if (existing.isPresent()) {
            existing.get().setCategoryName(categoryName);
            return merchantCacheRepository.save(existing.get());
        }

        MerchantCache newEntry = MerchantCache.builder()
                .user(user)
                .merchantName(normalizedMerchant)
                .categoryName(categoryName)
                .build();

        return merchantCacheRepository.save(newEntry);
    }
}
