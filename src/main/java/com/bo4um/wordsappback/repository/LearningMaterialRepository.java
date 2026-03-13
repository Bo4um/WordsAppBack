package com.bo4um.wordsappback.repository;

import com.bo4um.wordsappback.entity.LearningMaterial;
import com.bo4um.wordsappback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {
    List<LearningMaterial> findByUserOrderByUploadedAtDesc(User user);
    List<LearningMaterial> findByUserAndStatus(User user, LearningMaterial.MaterialStatus status);
    void deleteByUser(User user);
}
