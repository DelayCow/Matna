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
        List<RecipeListVO> resultRecipeList = new ArrayList<>();
        for (Recipe r : recipes) {
            resultRecipeList.add(new RecipeListVO(r.getRecipeNo(),r.getTitle(),r.getAverageRating(), r.getImageUrl(), r.getReviewCount()));
        }
        return resultRecipeList;
    }
}
