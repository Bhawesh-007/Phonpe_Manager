package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.entity.UploadedStatement;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @PostMapping("/upload")
    public ResponseEntity<UploadedStatement> uploadStatement(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") Long accountId,
            @AuthenticationPrincipal User currentUser
    ) {
        UploadedStatement statement = statementService.uploadStatement(file, accountId, currentUser);
        return ResponseEntity.ok(statement);
    }
}
