package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.ExpenserequestDTO;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.entity.Expense;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.Role;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.CategoryRepository;
import com.Bhawesh.expense_tracker.repository.ExpenseRepository;
import com.Bhawesh.expense_tracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class ExpenseService {
   private final ExpenseRepository expenseRepository;
   private final AccountRepository accountRepository;
   private final CategoryRepository categoryRepository;

   @Transactional
    public Expense createExpense(ExpenserequestDTO request){
       Account account = accountRepository.findById(request.getAccountId()).orElseThrow(()-> new ResourceNotFoundException("Account not found"));
       Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
       if(account.getBalance().compareTo(request.getAmount()) < 0){
           throw new RuntimeException("Insufficient balance");
       }
       account.setBalance(account.getBalance().subtract(request.getAmount()));
       accountRepository.save(account);
      Expense expense = Expense.builder()
              .account(account)
              .category(category)
              .amount(request.getAmount())
              .Description(request.getNote())
              .timestamp(request.getTimestamp())
              .build();

      return expenseRepository.save(expense);
   }

   // Own expenses only — any authenticated user can call this.
   public List<Expense> getMyExpenses(User currentUser){
       return expenseRepository.findByAccount_User_Id(currentUser.getId());
   }

   // Every expense in the system, regardless of owner — admin only.
   @PreAuthorize("hasAuthority('ADMIN')") //this is authorizing only admins to invoke a method
   public List<Expense> getAllExpense(){
       return expenseRepository.findAll();
   }

   public Expense getExpenseById(Long id, User currentUser){
       Expense expense = expenseRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException("Expense not found with id: " + id));
       assertOwnerOrAdmin(expense.getAccount(), currentUser);
       return expense;
   }

   public List<Expense> getExpensesByAccount(Long accountId, User currentUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        assertOwnerOrAdmin(account, currentUser);
        return expenseRepository.findByAccountId(accountId);
   }

   public void assertOwnerOrAdmin(Account account, User currentUser){
       boolean isOwner = account.getUser().getId().equals(currentUser.getId());
       boolean isAdmin = currentUser.getRole() == Role.ADMIN;
       if(!isOwner && !isAdmin){
           throw new UnauthorizedAccessException("You do not have access to this resource");
       }
   }

   @Transactional
   public Expense updateExpense( Long id , ExpenserequestDTO request ,  User currentUser){
        //see what i want is to first find the record of expense id which i am getting
       //then i have  to fetch the account from which this payment was made
       //then i will add that amount of that transaction into the account balance
       //then i will cross-verify wheter updated payment is valid or not


       Expense existingExpense = expenseRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
       assertOwnerOrAdmin(existingExpense.getAccount(),currentUser );
       //fetch the target account and category
       Account newAccount = accountRepository.findById(request.getAccountId())
               .orElseThrow(() -> new RuntimeException("Account not found with id: " + request.getAccountId()));
       Category category = categoryRepository.findById(request.getCategoryId())
               .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
       Account oldAccount = existingExpense.getAccount();
       oldAccount.setBalance(oldAccount.getBalance().add(existingExpense.getAmount()));
       accountRepository.save(oldAccount);
       if(newAccount.getBalance().compareTo(request.getAmount()) < 0){
           throw new RuntimeException("Insufficient balance in the account");
       }
       newAccount.setBalance(newAccount.getBalance().subtract(request.getAmount()));
       accountRepository.save(newAccount);
       existingExpense.setAccount(newAccount);
       existingExpense.setCategory(category);
       existingExpense.setAmount(request.getAmount());
       // Match your entity's description field casing (.setDescription or .setDescription)
       existingExpense.setDescription(request.getNote());
       existingExpense.setTimestamp(request.getTimestamp());

       return expenseRepository.save(existingExpense);



   }
    @Transactional
    public void deleteExpense(Long id, User currentUser) {
        // 1. Fetch the existing expense

        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        assertOwnerOrAdmin(expense.getAccount(), currentUser);
        // 2. Refund the money back to the linked account
        Account account = expense.getAccount();
        account.setBalance(account.getBalance().add(expense.getAmount()));
        accountRepository.save(account);

        // 3. Erase the record from the database
        expenseRepository.delete(expense);
    }


}
