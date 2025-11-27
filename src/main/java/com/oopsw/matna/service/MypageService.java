package com.oopsw.matna.service;

import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.RecipeListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final RecipeRepository recipeRepository;

    public List<RecipeListVO> getMypageRecipeList(int memberNo){
        List<Recipe> recipes = recipeRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        List<RecipeListVO> recipeList = recipes.stream()
                .map(recipe -> RecipeListVO.builder()
                        .recipeNo(recipe.getRecipeNo())
                        .title(recipe.getTitle())
                        .averageRating(recipe.getAverageRating())
                        .reviewCount(recipe.getReviewCount())
                        .imageUrl(recipe.getImageUrl())
                        .build()).collect(Collectors.toList());
        return recipeList;
    }
}
