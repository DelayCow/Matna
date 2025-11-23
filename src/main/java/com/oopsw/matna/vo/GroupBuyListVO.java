package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupBuyListVO {
    private Integer groupBuyNo;
    private String title;
    private String status;
    private String imageUrl;
}
