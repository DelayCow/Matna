package com.oopsw.matna.vo;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
public class PeriodGroupBuyHomeVO {
    private String title;
    private String nickname;
    private String creatorImageUrl;
    private String groupBuyImageUrl;
    private LocalDateTime inDate;

    private LocalDateTime dueDate;
    private int purchasePeriodDays;
    private LocalDateTime finalPurchaseDeadline;

    private int minPricePerPerson;
    private int maxPricePerPerson;
    private String shareLocation;
    private String shareDetailAddress;
    private int participants;
    private int maxParticipants;
    private Integer groupBuyNo;
    private String ingredientName;
}
