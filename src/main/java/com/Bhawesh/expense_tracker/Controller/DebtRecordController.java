package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.DebtRecordRequestDto;
import com.Bhawesh.expense_tracker.entity.DebtRecord;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.DebtStatus;
import com.Bhawesh.expense_tracker.service.DebtRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debt-records")
public class DebtRecordController {
    private final DebtRecordService debtRecordService;

    @PostMapping
    public ResponseEntity<DebtRecord> createDebtRecord(@Valid @RequestBody DebtRecordRequestDto request, @AuthenticationPrincipal User currentUser) {
        DebtRecord createdDebtRecord = debtRecordService.createDebtRecord(request, currentUser);
        return new ResponseEntity<>(createdDebtRecord, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<List<DebtRecord>> getMyDebtRecords(@AuthenticationPrincipal User currentUser,
                                                               @RequestParam(required = false) DebtStatus status) {
        if (status != null) {
            return ResponseEntity.ok(debtRecordService.getMyDebtRecordsByStatus(currentUser, status));
        }
        return ResponseEntity.ok(debtRecordService.getMyDebtRecords(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtRecord> getDebtRecordById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(debtRecordService.getDebtRecordById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebtRecord> updateDebtRecord(@PathVariable Long id, @Valid @RequestBody DebtRecordRequestDto request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(debtRecordService.updateDebtRecord(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebtRecord(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        debtRecordService.deleteDebtRecord(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
