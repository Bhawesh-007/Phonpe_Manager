package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.AccountRequestDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.Role;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountRequestDto request,
            @AuthenticationPrincipal User currentUser
    ) {
        Account createdAccount = accountService.createAccount(request, currentUser);

        return ResponseEntity.ok(createdAccount.getAccountType() + " account created successfully with ID: " + createdAccount.getId());
    }
    //now i have to make an endpoint for the user to fetch different accounts
    //
    @GetMapping("/user/{userid}/accounts")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable Long userid , @AuthenticationPrincipal User currentUser) {
        UserorAdmin(userid , currentUser);
        List<Account> account = accountRepository.findByUserId(userid);
        return ResponseEntity.ok(account);
    }
    public boolean UserorAdmin(Long id , User currentUser){
        boolean isUser = currentUser.getId().equals(id);
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);
        if(!isUser && !isAdmin){throw new UnauthorizedAccessException("You do not have access to this resource");}
        return true;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        UserorAdmin(account.getUser().getId(), currentUser);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDto request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(accountService.updateAccount(id, request, currentUser));
    }
userRepository.findById(id).map(user -> user.getRole() == Role.ADMIN).orElse(false);
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        accountService.deleteAccount(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
