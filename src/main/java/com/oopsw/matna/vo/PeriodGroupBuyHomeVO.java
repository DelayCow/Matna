package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
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
    private Long groupBuyNo;
    private String ingredientName;
}
