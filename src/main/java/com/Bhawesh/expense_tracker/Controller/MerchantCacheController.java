package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.entity.MerchantCache;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.MerchantCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/merchant-cache")
@RequiredArgsConstructor
public class MerchantCacheController {

    private final MerchantCacheService merchantCacheService;

    /**
     * Called by the Python parser service to check if a merchant is already
     * confirmed for this user before making an expensive Gemini API call.
     *
     * GET /api/merchant-cache/lookup?userId=5&merchantName=SWIGGY
     */
    @GetMapping("/lookup")
    public ResponseEntity<Map<String, String>> lookup(
            @RequestParam Long userId,
            @RequestParam String merchantName
    ) {
        String category = merchantCacheService.lookup(userId, merchantName);

        if (category != null) {
            return ResponseEntity.ok(Map.of(
                    "merchantName", merchantName.toUpperCase(),
                    "categoryName", category
            ));
        }
        // 204 No Content = cache miss, parser should proceed to Gemini
        return ResponseEntity.noContent().build();
    }

    /**
     * Called by the Python parser service to save a newly learned merchant
     * category after Gemini classifies it.
     *
     * POST /api/merchant-cache
     * Body: { "userId": 5, "merchantName": "SWIGGY", "categoryName": "Food and Dining" }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> save(
            @RequestBody MerchantCacheRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal User currentUser
    ) {
        Long userId = request.userId() != null ? request.userId() : (currentUser != null ? currentUser.getId() : null);
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
        }

        MerchantCache saved = merchantCacheService.save(
                userId,
                request.merchantName(),
                request.categoryName()
        );
        return ResponseEntity.ok(Map.of(
                "status", "saved",
                "merchantName", saved.getMerchantName(),
                "categoryName", saved.getCategoryName()
        ));
    }


    public record MerchantCacheRequest(Long userId, String merchantName, String categoryName) {}
}
