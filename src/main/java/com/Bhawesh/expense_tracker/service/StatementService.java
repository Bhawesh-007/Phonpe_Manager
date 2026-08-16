package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.*;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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

    public StatementParseResponseDto parseStatement(MultipartFile file, Long accountId, User currentUser) {
        validateFile(file);
        Account account = getOwnedAccount(accountId, currentUser);
        UploadedStatement statement = uploadedStatementRepository.save(UploadedStatement.builder()
                .user(currentUser).account(account).fileName(file.getOriginalFilename()).status(StatementStatus.PENDING).build());
        try {
            List<StatementReviewItemDto> reviewItems = getTransactions(file).stream()
                    .map(item -> toReviewItem(item, currentUser)).toList();
            statement.setStatus(StatementStatus.PARSED);
            statement.setExtracted_count(reviewItems.size());
            statement = uploadedStatementRepository.save(statement);
            return new StatementParseResponseDto(StatementResponseDto.fromEntity(statement), reviewItems);
        } catch (IOException | RestClientException | StatementParsingException e) {
            statement.setStatus(StatementStatus.FAILED);
            statement.setError_msg(e.getMessage());
            uploadedStatementRepository.save(statement);
            throw new StatementParsingException("Unable to parse this statement", e);
        }
    }

    @Transactional
    public StatementResponseDto confirmStatement(Long statementId, StatementConfirmRequestDto request, User currentUser) {
        UploadedStatement statement = getOwnedStatement(statementId, currentUser);
        if (statement.getStatus() != StatementStatus.PARSED) {
            throw new com.Bhawesh.expense_tracker.exception.BusinessRuleViolationException("Only parsed statements can be confirmed");
        }
        expensePdfService.saveConfirmedExpenses(statement.getAccount(), currentUser, request.getTransactions());
        statement.setStatus(StatementStatus.IMPORTED);
        statement.setExtracted_count(request.getTransactions().size());
        statement.setError_msg(null);
        return StatementResponseDto.fromEntity(uploadedStatementRepository.save(statement));
    }

    public List<StatementResponseDto> getMyStatements(User currentUser) {
        return uploadedStatementRepository.findByUserIdOrderByUploadedAtDesc(currentUser.getId()).stream()
                .map(StatementResponseDto::fromEntity).toList();
    }

    public StatementResponseDto getStatement(Long statementId, User currentUser) {
        return StatementResponseDto.fromEntity(getOwnedStatement(statementId, currentUser));
    }

    private StatementReviewItemDto toReviewItem(MlTransactionItemDto item, User currentUser) {
        Category category = resolveCategory(item.getCategoryName(), currentUser);
        boolean needsReview = !category.getName().equalsIgnoreCase(item.getCategoryName());
        return new StatementReviewItemDto(category.getId(), item.getCategoryName(), needsReview, item.getNote(),
                BigDecimal.valueOf(item.getAmount()), item.getTimestamp());
    }

    private Category resolveCategory(String categoryName, User currentUser) {
        return categoryRepository.findByNameIgnoreCaseAndUserId(categoryName, currentUser.getId())
                .or(() -> categoryRepository.findByNameIgnoreCaseAndUserId(UNCATEGORIZED, currentUser.getId()))
                .orElseGet(() -> categoryRepository.save(Category.builder().name(UNCATEGORIZED)
                        .type(CategoryType.EXPENSE).user(currentUser).build()));
    }

    private UploadedStatement getOwnedStatement(Long id, User user) {
        UploadedStatement statement = uploadedStatementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Statement not found with id: " + id));
        if (!statement.getUser().getId().equals(user.getId())) throw new UnauthorizedAccessException("You do not have access to this statement");
        return statement;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new StatementParsingException("File is empty");
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new StatementParsingException("File must be a PDF");
        }
    }

    private Account getOwnedAccount(Long id, User user) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        if (!account.getUser().getId().equals(user.getId())) throw new UnauthorizedAccessException("You do not have access to this account");
        return account;
    }

    private List<MlTransactionItemDto> getTransactions(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) { @Override public String getFilename() { return file.getOriginalFilename(); } });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<MlTransactionResponseDto> response = restTemplate.postForEntity(mlServiceUrl + "/parser/process",
                new HttpEntity<>(body, headers), MlTransactionResponseDto.class);
        MlTransactionResponseDto payload = response.getBody();
        if (payload == null || payload.getData() == null || !"success".equalsIgnoreCase(payload.getStatus())) {
            throw new StatementParsingException("Parser returned an invalid response");
        }
        return payload.getData();
    }
}
