package com.bo4um.wordsappback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PragmaticCorrectionResponse {
    private String originalText;
    private String correctedText;
    private String errorType;
    private String explanation;
    private List<String> suggestedAlternatives;
    private Integer severityLevel;
    
    @JsonProperty("isFormalAppropriate")
    private Boolean isFormalAppropriate;
    
    @JsonProperty("isToneAppropriate")
    private Boolean isToneAppropriate;
    
    private String culturalNote;
    private LocalDateTime timestamp;

    public Boolean getIsFormalAppropriate() { return isFormalAppropriate; }
    public void setIsFormalAppropriate(Boolean value) { this.isFormalAppropriate = value; }
    public Boolean getIsToneAppropriate() { return isToneAppropriate; }
    public void setIsToneAppropriate(Boolean value) { this.isToneAppropriate = value; }
}
