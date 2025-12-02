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

    private int minPricePerPerson;
    private int maxPricePerPerson;
    private String shareLocation;

    private int participants;
    private int maxParticipants;
}
