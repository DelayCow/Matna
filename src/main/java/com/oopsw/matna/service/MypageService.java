package com.oopsw.matna.service;

import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.RecipeListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final RecipeRepository recipeRepository;

    public List<RecipeListVO> getMyPageRecipeList(){

        List<Recipe> recipeList = recipeRepository.findAll();
        List<RecipeListVO> mypageRecipeList = new ArrayList<>();

        for (Recipe recipe : recipeList) {
            RecipeListVO vo = RecipeListVO.builder()
                    .recipeNo(recipe.getRecipeNo())
                    .title(recipe.getTitle())
                    .imageUrl(recipe.getImageUrl())
                    .build();
        }
        return mypageRecipeList;
    }
}
