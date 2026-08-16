package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.TransactionRequestDto;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.Transaction;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.TransactionStatus;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    @Transactional // if app crashes halfway in this method then everything just rolls back and no money is lost
    public Transaction transferMoney(TransactionRequestDto request , User currentUser){
        Account sender = accountRepository.findById(request.getSenderAccountId())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        if(!sender.getUser().getId().equals((currentUser.getId()))){
            throw new RuntimeException(("Unauthorized access to sender account"));
        }
        Account receiver = accountRepository.findById(request.getReceiverAccountId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if(sender.getBalance().compareTo(request.getAmount())<0){
            throw new RuntimeException("Insufficient balance");
        }
        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));
        accountRepository.save(sender);
        accountRepository.save(receiver);
        Transaction transaction = Transaction.builder()
                .senderAccount(sender)
                .receiverAccount(receiver)
                .amount(request.getAmount())
                .note(request.getNote())
                .transactionType(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();
        return transactionRepository.save(transaction);
    }
}