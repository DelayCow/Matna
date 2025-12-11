package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewsVO {

    private Integer reviewNo;
    private String title;
    private String content;
    private String reviewImage;
    private Float rating;
    private Integer spicyLevel;
    private LocalDateTime inDate;
    private Integer writerNo;
    private String writerNickname;
    private String writerProfileImage;

    private List<ReviewsRegisterVO.AlternativeRegisterVO> alternatives;

}
