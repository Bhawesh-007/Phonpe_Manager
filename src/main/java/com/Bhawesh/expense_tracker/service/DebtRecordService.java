package com.Bhawesh.expense_tracker.service;

import com.Bhawesh.expense_tracker.dto.DebtRecordRequestDto;
import com.Bhawesh.expense_tracker.entity.DebtRecord;
import com.Bhawesh.expense_tracker.entity.User;
import com.Bhawesh.expense_tracker.enums.DebtStatus;
import com.Bhawesh.expense_tracker.enums.Role;
import com.Bhawesh.expense_tracker.exception.ResourceNotFoundException;
import com.Bhawesh.expense_tracker.exception.UnauthorizedAccessException;
import com.Bhawesh.expense_tracker.repository.DebtRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtRecordService {
    private final DebtRecordRepository debtRecordRepository;

    @Transactional
    public DebtRecord createDebtRecord(DebtRecordRequestDto request, User currentUser) {
        DebtRecord debtRecord = DebtRecord.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .type(request.getType())
                .status(DebtStatus.PENDING)
                .description(request.getDescription())
                .duedate(request.getDuedate())
                .user(currentUser)
                .build();
        return debtRecordRepository.save(debtRecord);
    }

    public List<DebtRecord> getMyDebtRecords(User currentUser) {
        return debtRecordRepository.findByUserId(currentUser.getId());
    }

    public List<DebtRecord> getMyDebtRecordsByStatus(User currentUser, DebtStatus status) {
        return debtRecordRepository.findByUserIdAndStatus(currentUser.getId(), status);
    }

    public DebtRecord getDebtRecordById(Long id, User currentUser) {
        DebtRecord debtRecord = debtRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + id));
        assertOwnerOrAdmin(debtRecord, currentUser);
        return debtRecord;
    }

    @Transactional
    public DebtRecord updateDebtRecord(Long id, DebtRecordRequestDto request, User currentUser) {
        DebtRecord debtRecord = debtRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + id));
        assertOwnerOrAdmin(debtRecord, currentUser);
        debtRecord.setName(request.getName());
        debtRecord.setAmount(request.getAmount());
        debtRecord.setType(request.getType());
        debtRecord.setStatus(request.getStatus());
        debtRecord.setDescription(request.getDescription());
        debtRecord.setDuedate(request.getDuedate());
        return debtRecordRepository.save(debtRecord);
    }

    @Transactional
    public void deleteDebtRecord(Long id, User currentUser) {
        DebtRecord debtRecord = debtRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debt record not found with id: " + id));
        assertOwnerOrAdmin(debtRecord, currentUser);
        debtRecordRepository.delete(debtRecord);
    }

    private void assertOwnerOrAdmin(DebtRecord debtRecord, User currentUser) {
        boolean isOwner = debtRecord.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedAccessException("You do not have access to this resource");
        }
    }
}
