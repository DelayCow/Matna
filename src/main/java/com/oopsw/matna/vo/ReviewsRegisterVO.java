package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//.등록용임
public class ReviewsRegisterVO {

        private Integer reviewNo;
        private Integer recipeNo;
        private Integer writerNo;
        private String title;
        private String content;
        private Float rating;
        private Integer spicyLevel;
        private String reviewImage;


        private List<AlternativeRegisterVO> alternatives;

        @Data
        public static class AlternativeRegisterVO {
            private String originalIngredientName;
            private String alternativeIngredientName;
            private Float amount;
            private String unit;
        }
    }

