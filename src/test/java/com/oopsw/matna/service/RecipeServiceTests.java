package com.oopsw.matna.service;

import com.oopsw.matna.dto.RecipeResponse;
import com.oopsw.matna.vo.RecipeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class RecipeServiceTests {
    @Autowired
    private RecipeService recipeService;

    @Test
    public void getRecipeList() {
        Slice<RecipeVO> recipeList =recipeService.getRecipeList(null, null, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "reviewCount")));
        List<RecipeResponse> result = recipeList.stream().map(recipe -> RecipeResponse.builder()
                .recipeNo(recipe.getRecipeNo())
                .title(recipe.getTitle())
                .thumbnailUrl(recipe.getThumbnailUrl())
                .writerNickname(recipe.getWriterNickname())
                .writerProfile(recipe.getWriterProfile())
                .reviewCount(recipe.getReviewCount())
                .averageRating(recipe.getAverageRating())
                .servings(recipe.getServings())
                .prepTime(recipe.getPrepTime())
                .difficulty(recipe.getDifficulty())
                .spicyLevel(recipe.getSpicyLevel())
                .build()).collect(Collectors.toList());

        System.out.println("리뷰순 3개" + result);
        System.out.println("다음페이지유무" + recipeList.hasNext());
    }

}
