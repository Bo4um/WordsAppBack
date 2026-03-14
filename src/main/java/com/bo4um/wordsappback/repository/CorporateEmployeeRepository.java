package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.CorporateEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorporateEmployeeRepository extends JpaRepository<CorporateEmployee, Long> {
    List<CorporateEmployee> findByCorporateAccountId(Long corporateAccountId);
    List<CorporateEmployee> findByUserId(Long userId);
}
