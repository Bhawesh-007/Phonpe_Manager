package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.AccountRequestDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.Role;
import com.Bhawesh.expense_tracker.exception.BusinessRuleViolationException;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Account createAccount(AccountRequestDto request , User currentUser){
        Account account = new Account().builder()
                .accountType(request.getAccountType())
                .balance(request.getInitialBalance())
                .creditLimit(request.getCreditLimit())
                .user(currentUser)
                .build();
        return accountRepository.save(account);

    }

    public Account getAccountById(Long id, User currentUser) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        assertOwnerOrAdmin(account, currentUser);
        return account;
    }

    @Transactional
    public Account updateAccount(Long id, AccountRequestDto request, User currentUser) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        assertOwnerOrAdmin(account, currentUser);
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setCreditLimit(request.getCreditLimit());
        return accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id, User currentUser) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        assertOwnerOrAdmin(account, currentUser);
        if (transactionRepository.existsBySenderAccount_IdOrReceiverAccount_Id(id, id)) {
            throw new BusinessRuleViolationException("Cannot delete an account with existing transactions");
        }
        accountRepository.delete(account);
    }

    private void assertOwnerOrAdmin(Account account, User currentUser) {
        boolean isOwner = account.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You do not have access to this resource");
        }
    }
}
