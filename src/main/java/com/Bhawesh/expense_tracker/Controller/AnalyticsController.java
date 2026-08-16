package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.AnalyticsOverviewDto;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewDto> overview(@AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) Long accountId) {
        return ResponseEntity.ok(analyticsService.overview(currentUser, from, to, accountId));
    }
}
