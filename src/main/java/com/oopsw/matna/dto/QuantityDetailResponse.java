package com.oopsw.matna.dto;

import com.oopsw.matna.vo.QuantityGroupBuyDetailVO;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@ToString
public class QuantityDetailResponse {
    private QuantityGroupBuyDetailVO groupBuyDetail;
    private List<ParticipantInfo> participant;
    private List<RecipeInfo> recipes;

    @Data
    @Builder
    public static class ParticipantInfo {
        private Integer groupParticipantNo;
        private Integer memberNo;
        private String nickname;
        private String profileUrl;
        private LocalDateTime participatedDate;
        private Integer myQuantity;
    }

    @Data
    @Builder
    public static class RecipeInfo {
        private Integer recipeNo;
        private String title;
        private String imageUrl;
        private String authorNickname;
        private LocalDateTime inDate;
    }

}