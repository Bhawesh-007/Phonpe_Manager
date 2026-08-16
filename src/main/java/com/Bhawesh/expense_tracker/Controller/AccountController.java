package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.AccountRequestDto;
import com.Bhawesh.expense_tracker.dto.AccountResponseDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.Role;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.UserRepository;
import com.Bhawesh.expense_tracker.service.AccountService;
import com.Bhawesh.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ExpenseService expenseService;
    private final UserRepository userRepository;
    @PostMapping("/create")
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountRequestDto request,
            @AuthenticationPrincipal User currentUser
    ) {
        Account createdAccount = accountService.createAccount(request, currentUser);
        return new ResponseEntity<>(AccountResponseDto.fromEntity(createdAccount), org.springframework.http.HttpStatus.CREATED);
    }
    //now i have to make an endpoint for the user to fetch different accounts
    //
    @GetMapping("/user/{userid}/accounts")
    public ResponseEntity<List<AccountResponseDto>> getUserAccounts(@PathVariable Long userid , @AuthenticationPrincipal User currentUser) {
        UserorAdmin(userid , currentUser);
        List<Account> account = accountRepository.findByUserId(userid);
        return ResponseEntity.ok(account.stream().map(AccountResponseDto::fromEntity).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(AccountResponseDto.fromEntity(accountService.getAccountById(id, currentUser)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDto>> getMyAccounts(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(accountRepository.findByUserId(currentUser.getId()).stream()
                .map(AccountResponseDto::fromEntity).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDto request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(AccountResponseDto.fromEntity(accountService.updateAccount(id, request, currentUser)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        accountService.deleteAccount(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    public boolean UserorAdmin(Long id , User currentUser){
        boolean isUser = currentUser.getId().equals(id);
        boolean isAdmin = currentUser.getRole().equals(Role.ADMIN);
        if(!isUser && !isAdmin){throw new UnauthorizedAccessException("You do not have access to this resource");}
        return true;
    }
}
