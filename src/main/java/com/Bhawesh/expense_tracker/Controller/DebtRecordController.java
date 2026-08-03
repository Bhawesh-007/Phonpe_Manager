package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.DebtRecordRequestDto;
import com.Bhawesh.expense_tracker.dto.DebtRecordResponseDto;
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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debt-records")
public class DebtRecordController {
    private final DebtRecordService debtRecordService;

    @PostMapping
    public ResponseEntity<DebtRecordResponseDto> createDebtRecord(@Valid @RequestBody DebtRecordRequestDto request, @AuthenticationPrincipal User currentUser) {
        DebtRecord createdDebtRecord = debtRecordService.createDebtRecord(request, currentUser);
        return new ResponseEntity<>(DebtRecordResponseDto.fromEntity(createdDebtRecord), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<List<DebtRecordResponseDto>> getMyDebtRecords(@AuthenticationPrincipal User currentUser,
                                                               @RequestParam(required = false) DebtStatus status) {
        List<DebtRecord> records = status != null
                ? debtRecordService.getMyDebtRecordsByStatus(currentUser, status)
                : debtRecordService.getMyDebtRecords(currentUser);
        return ResponseEntity.ok(records.stream().map(DebtRecordResponseDto::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebtRecordResponseDto> getDebtRecordById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(DebtRecordResponseDto.fromEntity(debtRecordService.getDebtRecordById(id, currentUser)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebtRecordResponseDto> updateDebtRecord(@PathVariable Long id, @Valid @RequestBody DebtRecordRequestDto request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(DebtRecordResponseDto.fromEntity(debtRecordService.updateDebtRecord(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebtRecord(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        debtRecordService.deleteDebtRecord(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
