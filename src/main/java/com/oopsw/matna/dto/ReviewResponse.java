package com.oopsw.matna.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ReviewResponse {
    private Integer reviewNo;
    private String writerNickname;
    private String writerProfileImage;
    private String title;
    private String content;
    private String reviewImage;
    private Float rating;
    private Integer spicyLevel;
    private LocalDateTime inDate;

}
