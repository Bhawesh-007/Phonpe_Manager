package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.TransactionRequestDto;
import com.Bhawesh.expense_tracker.dto.TransactionResponseDto;
import com.Bhawesh.expense_tracker.entity.Transaction;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transferMoney(@RequestBody TransactionRequestDto request
     , @AuthenticationPrincipal User currentUser){
        Transaction savedTransaction = transactionService.transferMoney(request,currentUser);
        return ResponseEntity.ok(TransactionResponseDto.fromEntity(savedTransaction));
    }
    @GetMapping("/get_transactions")
    public List<TransactionResponseDto> getTransactions(@AuthenticationPrincipal User currentUser){
        List<Transaction> transactions = transactionService.getAllTransactions(currentUser);
        return transactions.stream()
                .map(TransactionResponseDto::fromEntity)
                .toList();
    }

}
