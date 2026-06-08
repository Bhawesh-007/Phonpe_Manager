package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.AccountRequestDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public Account createAccount(AccountRequestDto request , User currentUser){
        Account account = new Account().builder()
                .accountType(request.getAccountType())
                .balance(request.getInitialBalance())
                .user(currentUser)
                .build();
        return accountRepository.save(account);

    }
}
