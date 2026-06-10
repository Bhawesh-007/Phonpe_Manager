package com.Bhawesh.expense_tracker.Controller;

import com.Bhawesh.expense_tracker.dto.TransactionRequestDto;
import com.Bhawesh.expense_tracker.entity.Transaction;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;;
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(@RequestBody TransactionRequestDto request
     , @AuthenticationPrincipal User currentUser){
        Transaction savedTransaction = transactionService.transferMoney(request,currentUser);
        return ResponseEntity.ok(savedTransaction);
    }
}
