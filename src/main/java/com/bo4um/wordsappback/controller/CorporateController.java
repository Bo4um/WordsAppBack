package com.bo4um.wordsappback.controller;

import com.bo4um.wordsappback.service.CorporateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/corporate")
@RequiredArgsConstructor
@Tag(name = "Corporate Dashboard", description = "B2B корпоративное обучение")
@SecurityRequirement(name = "bearerAuth")
public class CorporateController {

    private final CorporateService corporateService;

    @PostMapping("/create")
    @Operation(summary = "Создать корпоративный аккаунт", description = "Создать B2B аккаунт для компании")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт создан"),
    })
    public ResponseEntity<?> createCorporateAccount(@RequestBody Map<String, Object> request) {
        String companyName = (String) request.get("companyName");
        String industry = (String) request.get("industry");
        Integer maxEmployees = (Integer) request.get("maxEmployees");

        var account = corporateService.createCorporateAccount(companyName, industry, maxEmployees);
        return ResponseEntity.ok(Map.of(
                "accountId", account.getId(),
                "accountCode", account.getAccountCode(),
                "message", "Share this code with employees: " + account.getAccountCode()
        ));
    }

    @PostMapping("/join")
    @Operation(summary = "Присоединиться к компании", description = "Войти в корпоративный аккаунт по коду")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Неверный код")
    })
    public ResponseEntity<?> joinCorporateAccount(
            @RequestBody Map<String, Object> request) {
        // userId would come from auth in production
        Long userId = 1L;
        String accountCode = (String) request.get("accountCode");
        String department = (String) request.get("department");
        String position = (String) request.get("position");

        var employee = corporateService.joinCorporateAccount(userId, accountCode, department, position);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "employeeId", employee.getId()
        ));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Дашборд компании", description = "Получить статистику компании")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<?> getDashboard(@RequestParam Long accountId) {
        return ResponseEntity.ok(corporateService.getDashboard(accountId));
    }

    @GetMapping("/my-company")
    @Operation(summary = "Моя компания", description = "Получить информацию о компании сотрудника")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ"),
    })
    public ResponseEntity<?> getMyCompany(@RequestParam Long userId) {
        var info = corporateService.getEmployeeCorporateInfo(userId);
        return info != null ? ResponseEntity.ok(info) : ResponseEntity.ok(Map.of("message", "Not in corporate account"));
    }
}
