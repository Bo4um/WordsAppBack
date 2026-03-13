package com.bo4um.wordsappback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "characters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String sex;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] image;

    @Column(length = 1000)
    private String description;

    private Boolean isSystem = false;

    private Boolean isActive = true;

    private Integer sortOrder;
}
