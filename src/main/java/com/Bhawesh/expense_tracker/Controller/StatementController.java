package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.StatementConfirmRequestDto;
import com.Bhawesh.expense_tracker.dto.StatementParseResponseDto;
import com.Bhawesh.expense_tracker.dto.StatementResponseDto;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.StatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @PostMapping("/parse")
    public ResponseEntity<StatementParseResponseDto> parseStatement(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") Long accountId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(statementService.parseStatement(file, accountId, currentUser));
    }

    @PostMapping("/{statementId}/confirm")
    public ResponseEntity<StatementResponseDto> confirmStatement(@PathVariable Long statementId,
            @Valid @RequestBody StatementConfirmRequestDto request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(statementService.confirmStatement(statementId, request, currentUser));
    }

    @GetMapping
    public ResponseEntity<java.util.List<StatementResponseDto>> getMyStatements(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(statementService.getMyStatements(currentUser));
    }

    @GetMapping("/{statementId}")
    public ResponseEntity<StatementResponseDto> getStatement(@PathVariable Long statementId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(statementService.getStatement(statementId, currentUser));
    }
}
