package com.oopsw.matna.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeListVO {

    private Integer recipeNo;       // 레시피 번호
    private String title;           // 제목
    private String thumbnailUrl;    // 썸네일 이미지 URL
    private String writerNickname;  // 작성자 닉네임 (Member 테이블)
    private String writerProfile;   // 작성자 프로필 사진 (Member 테이블)
    private Float rating;           // 별점
    private Integer reviewCount;    // 후기 수
    private Integer servings;       // 인분
    private Integer prepTime;       // 조리 시간
    private String difficulty;      // 난이도
    private Integer spicyLevel;

}
