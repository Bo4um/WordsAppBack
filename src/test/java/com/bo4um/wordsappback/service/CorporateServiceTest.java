package com.bo4um.wordsappback.service;

import com.bo4um.wordsappback.entity.CorporateAccount;
import com.bo4um.wordsappback.entity.CorporateEmployee;
import com.bo4um.wordsappback.repository.CorporateAccountRepository;
import com.bo4um.wordsappback.repository.CorporateEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CorporateService Unit Tests")
class CorporateServiceTest {

    @Mock
    private CorporateAccountRepository accountRepository;

    @Mock
    private CorporateEmployeeRepository employeeRepository;

    @InjectMocks
    private CorporateService corporateService;

    private CorporateAccount testAccount;

    @BeforeEach
    void setUp() {
        testAccount = CorporateAccount.builder()
                .id(1L)
                .companyName("Test Corp")
                .industry("Technology")
                .accountCode("CORP-TEST123")
                .maxEmployees(100)
                .currentEmployees(0)
                .subscriptionEndDate(LocalDateTime.now().plusYears(1))
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("Should create corporate account")
    void createCorporateAccount_Success() {
        // Given
        when(accountRepository.save(any(CorporateAccount.class))).thenReturn(testAccount);

        // When
        CorporateAccount result = corporateService.createCorporateAccount("Test Corp", "Technology", 100);

        // Then
        assertNotNull(result);
        assertEquals("Test Corp", result.getCompanyName());
        assertNotNull(result.getAccountCode());
    }

    @Test
    @DisplayName("Should join corporate account")
    void joinCorporateAccount_Success() {
        // Given
        when(accountRepository.findByAccountCode("CORP-TEST123")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(CorporateAccount.class))).thenReturn(testAccount);
        when(employeeRepository.save(any(CorporateEmployee.class))).thenReturn(CorporateEmployee.builder()
                .id(1L)
                .userId(1L)
                .corporateAccountId(1L)
                .build());

        // When
        CorporateEmployee result = corporateService.joinCorporateAccount(1L, "CORP-TEST123", "Engineering", "Developer");

        // Then
        assertNotNull(result);
        assertEquals(1, testAccount.getCurrentEmployees());
    }

    @Test
    @DisplayName("Should throw exception for invalid account code")
    void joinCorporateAccount_InvalidCode() {
        // Given
        when(accountRepository.findByAccountCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                corporateService.joinCorporateAccount(1L, "INVALID", "Engineering", "Developer")
        );
    }

    @Test
    @DisplayName("Should throw exception when account is full")
    void joinCorporateAccount_AccountFull() {
        // Given
        testAccount.setCurrentEmployees(100);
        testAccount.setMaxEmployees(100);
        when(accountRepository.findByAccountCode("CORP-TEST123")).thenReturn(Optional.of(testAccount));

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                corporateService.joinCorporateAccount(1L, "CORP-TEST123", "Engineering", "Developer")
        );
    }

    @Test
    @DisplayName("Should get dashboard data")
    void getDashboard_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(employeeRepository.findByCorporateAccountId(1L)).thenReturn(java.util.Arrays.asList(
                CorporateEmployee.builder().userId(1L).isActive(true).build(),
                CorporateEmployee.builder().userId(2L).isActive(true).build()
        ));

        // When
        var result = corporateService.getDashboard(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Corp", result.getCompanyName());
        assertEquals(2, result.getActiveEmployees());
    }
}
