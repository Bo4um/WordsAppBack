package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.RoleplayScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleplayScenarioRepository extends JpaRepository<RoleplayScenario, Long> {
    List<RoleplayScenario> findByIsActiveTrueOrderBySortOrder();
    List<RoleplayScenario> findByLanguageAndIsActiveTrue(String language);
}
