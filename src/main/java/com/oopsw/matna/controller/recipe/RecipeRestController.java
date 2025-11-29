package com.oopsw.matna.controller.recipe;

import com.oopsw.matna.dto.RecipeResponse;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.service.RecipeService;
import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeRestController {
    private final RecipeService recipeService;

    @GetMapping("/scroll")
    public List<RecipeResponse> getRecipeList(
            @RequestParam(required = false) Integer spicyLevel,
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 8)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "inDate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "reviewCount", direction = Sort.Direction.DESC)
            })
            Pageable pageable) {
        List<RecipeVO> recipeList = recipeService.getRecipeList(spicyLevel, keyword, pageable);
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
        return result;
    }
}
