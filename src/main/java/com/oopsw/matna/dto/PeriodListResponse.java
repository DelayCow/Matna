package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class PeriodListResponse {
    private String title;
    private String nickname;
    private String creatorImageUrl;
    private String groupBuyImageUrl;

    private LocalDateTime inDate;
    private LocalDateTime dueDate;

    private Integer minPricePerPerson;
    private Integer maxPricePerPerson;
    private String shareLocation;

    private Integer participants;
    private Integer maxParticipants;
    private Integer periodGroupBuyNo;
}
