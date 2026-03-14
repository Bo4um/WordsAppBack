package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.CorporateAccount;
import com.bo4um.wordsappback.entity.CorporateEmployee;
import com.bo4um.wordsappback.repository.CorporateAccountRepository;
import com.bo4um.wordsappback.repository.CorporateEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorporateService {

    private final CorporateAccountRepository accountRepository;
    private final CorporateEmployeeRepository employeeRepository;

    /**
     * Create new corporate account
     */
    @Transactional
    public CorporateAccount createCorporateAccount(String companyName, String industry, Integer maxEmployees) {
        String accountCode = generateAccountCode();

        CorporateAccount account = CorporateAccount.builder()
                .companyName(companyName)
                .industry(industry)
                .accountCode(accountCode)
                .maxEmployees(maxEmployees)
                .currentEmployees(0)
                .subscriptionEndDate(LocalDateTime.now().plusYears(1))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        return accountRepository.save(account);
    }

    /**
     * Join corporate account with code
     */
    @Transactional
    public CorporateEmployee joinCorporateAccount(Long userId, String accountCode, String department, String position) {
        CorporateAccount account = accountRepository.findByAccountCode(accountCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account code"));

        if (account.getCurrentEmployees() >= account.getMaxEmployees()) {
            throw new IllegalArgumentException("Corporate account is full");
        }

        CorporateEmployee employee = CorporateEmployee.builder()
                .userId(userId)
                .corporateAccountId(account.getId())
                .department(department)
                .position(position)
                .isActive(true)
                .joinedAt(LocalDateTime.now())
                .lastActiveAt(LocalDateTime.now())
                .build();

        account.setCurrentEmployees(account.getCurrentEmployees() + 1);
        accountRepository.save(account);

        return employeeRepository.save(employee);
    }

    /**
     * Get corporate dashboard data
     */
    @Transactional(readOnly = true)
    public CorporateDashboardDTO getDashboard(Long corporateAccountId) {
        CorporateAccount account = accountRepository.findById(corporateAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        List<CorporateEmployee> employees = employeeRepository.findByCorporateAccountId(corporateAccountId);
        long activeEmployees = employees.stream().filter(CorporateEmployee::getIsActive).count();

        return CorporateDashboardDTO.builder()
                .companyName(account.getCompanyName())
                .totalEmployees(account.getCurrentEmployees())
                .activeEmployees((int) activeEmployees)
                .subscriptionEndDate(account.getSubscriptionEndDate())
                .build();
    }

    /**
     * Get employee's corporate info
     */
    @Transactional(readOnly = true)
    public CorporateInfoDTO getEmployeeCorporateInfo(Long userId) {
        List<CorporateEmployee> employees = employeeRepository.findByUserId(userId);

        if (employees.isEmpty()) {
            return null;
        }

        CorporateEmployee employee = employees.get(0);
        CorporateAccount account = accountRepository.findById(employee.getCorporateAccountId())
                .orElse(null);

        return CorporateInfoDTO.builder()
                .companyName(account != null ? account.getCompanyName() : "Unknown")
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .joinedAt(employee.getJoinedAt())
                .build();
    }

    private String generateAccountCode() {
        return "CORP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // DTOs
    @lombok.Builder
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class CorporateDashboardDTO {
        private String companyName;
        private Integer totalEmployees;
        private Integer activeEmployees;
        private LocalDateTime subscriptionEndDate;
    }

    @lombok.Builder
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class CorporateInfoDTO {
        private String companyName;
        private String department;
        private String position;
        private LocalDateTime joinedAt;
    }
}
