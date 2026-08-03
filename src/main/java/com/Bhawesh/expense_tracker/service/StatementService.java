package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.MlTransactionItemDto;
import com.Bhawesh.expense_tracker.dto.MlTransactionResponseDto;
import com.Bhawesh.expense_tracker.dto.TransactionDTO;
import com.Bhawesh.expense_tracker.entity.Account;
import com.Bhawesh.expense_tracker.entity.Category;
import com.Bhawesh.expense_tracker.entity.UploadedStatement;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.CategoryType;
import com.Bhawesh.expense_tracker.enums.StatementStatus;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.StatementParsingException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.AccountRepository;
import com.Bhawesh.expense_tracker.repository.CategoryRepository;
import com.Bhawesh.expense_tracker.repository.UploadedStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatementService {
    private static final String UNCATEGORIZED = "Uncategorized";

    private final ExpensePdfService expensePdfService;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final UploadedStatementRepository uploadedStatementRepository;
    private final RestTemplate restTemplate;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    public UploadedStatement uploadStatement(MultipartFile file , Long accountId , User currentUser) {
        validateFile(file);
        Account account = getOwnedAccount(accountId , currentUser);

        UploadedStatement statement = UploadedStatement.builder()
                .user(currentUser)
                .account(account)
                .fileName(file.getOriginalFilename())
                .status(StatementStatus.PENDING)
                .build();

        List<MlTransactionItemDto> mlTransactions;
        try {
            mlTransactions = getTransactions(file);
        } catch (IOException | RestClientException e) {
            statement.setStatus(StatementStatus.FAILED);
            statement.setError_msg(e.getMessage());
            uploadedStatementRepository.save(statement);
            throw new StatementParsingException("Error occurred while parsing the statement", e);
        }

        List<TransactionDTO> transactionDTOs = mlTransactions.stream()
                .map(transaction -> {
                    TransactionDTO transactionDTO = new TransactionDTO();
                    transactionDTO.setNote(transaction.getNote());
                    transactionDTO.setAmount(transaction.getAmount());
                    transactionDTO.setTimestamp(transaction.getTimestamp());
                    transactionDTO.setCategoryId(resolveCategoryId(transaction.getCategoryName(), currentUser));
                    return transactionDTO;
                })
                .collect(Collectors.toList());

        if (!transactionDTOs.isEmpty()) {
            expensePdfService.SaveBulkExpenses(accountId, transactionDTOs);
        }

        statement.setStatus(StatementStatus.SUCCESS);
        statement.setExtracted_count(transactionDTOs.size());
        return uploadedStatementRepository.save(statement);
    }

    private Long resolveCategoryId(String categoryName, User currentUser) {
        return categoryRepository.findByNameIgnoreCaseAndUserId(categoryName, currentUser.getId())
                .or(() -> categoryRepository.findByNameIgnoreCaseAndUserId(UNCATEGORIZED, currentUser.getId()))
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name(UNCATEGORIZED)
                        .type(CategoryType.EXPENSE)
                        .user(currentUser)
                        .build()))
                .getId();
    }
    private void  validateFile(MultipartFile file) {
        if(file==null||file.isEmpty()) {
            throw new StatementParsingException("File is empty");
        }
        String fileName = file.getOriginalFilename();
        if(fileName==null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new StatementParsingException("File is not pdf");
        }
    }
    private Account getOwnedAccount(Long accountId , User currentUser)  {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        if(!account.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not allowed to perform this action");
        }
        return account;
    }
    private List<MlTransactionItemDto> getTransactions(MultipartFile file) throws IOException {
        //implementation for preparing the file
        MultiValueMap<String , Object> body = new LinkedMultiValueMap<String , Object>();
        //So here i am preparing a request for the server as a client
        //so what i have to do is prepare a map with appropriate key value pairs
        body.add("file",new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename(){
              return file.getOriginalFilename();
            }
        });
        //now i will create http headers and combine it with the body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //now set the body
         HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(body,headers);
         //NOW SET THE RESPONSE ENTITY
        ResponseEntity<MlTransactionResponseDto> response = restTemplate.postForEntity(
                mlServiceUrl + "/parser/process", requestEntity, MlTransactionResponseDto.class);
        MlTransactionResponseDto responseDto = response.getBody();
        if(responseDto==null) {
            throw new StatementParsingException("Response is null");
        }
        return responseDto.getData();
    }

}
