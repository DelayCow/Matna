package com.oopsw.matna.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ReviewsListVO {
    private Integer reviewNo;
    private String title;
    private String imageUrl;
    private Float rating;
    private LocalDateTime inDate;
}
