package com.bo4um.wordsappback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResponse {
    private Integer rank;
    private Integer score;
    private Integer totalUsers;
}
