package com.oopsw.matna.Repository;

import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.RecipeListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class RecipeRepositoryTests {
    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void getMyPageRecipeListTest(){
        Integer memberNo = 5;
        List<Recipe> recipes = recipeRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        List<RecipeListVO> recipeList = recipes.stream()
                .map(recipe -> RecipeListVO.builder()
                        .recipeNo(recipe.getRecipeNo())
                        .title(recipe.getTitle())
                        .averageRating(recipe.getAverageRating())
                        .reviewCount(recipe.getReviewCount())
                        .imageUrl(recipe.getImageUrl())
                        .build()).collect(Collectors.toList());
        System.out.println(recipeList);
    }

    @Test
    public void removeRecipe(){
        Integer recipeNo = 12;
        Recipe recipe = recipeRepository.findById(recipeNo).get();
        recipe.setDelDate(LocalDateTime.now());
        recipeRepository.save(recipe);
    }

}
