package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.CorporateAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorporateAccountRepository extends JpaRepository<CorporateAccount, Long> {
    Optional<CorporateAccount> findByAccountCode(String accountCode);
}
