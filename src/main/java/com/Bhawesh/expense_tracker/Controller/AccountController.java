package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.AccountRequestDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountRequestDto request,
            @AuthenticationPrincipal User currentUser
    ) {
        Account createdAccount = accountService.createAccount(request, currentUser);

        return ResponseEntity.ok(createdAccount.getAccountType() + " account created successfully with ID: " + createdAccount.getId());
    }
}
