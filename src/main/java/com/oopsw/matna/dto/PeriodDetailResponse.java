package com.oopsw.matna.dto;

import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@ToString
public class PeriodDetailResponse {
    private PeriodGroupBuyDetailVO groupBuyDetail;
    private List<ParticipantInfo> participants;
    private List<RecipeInfo> recipes;

    @Data
    @Builder
    public static class ParticipantInfo {
        private String nickname;
        private String profileUrl;
        private LocalDateTime participatedDate;
    }

    @Data
    @Builder
    public static class RecipeInfo {
        private String title;
        private String imageUrl;
        private String authorNickname;
        private LocalDateTime inDate;
    }
}
