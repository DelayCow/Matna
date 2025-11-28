package com.oopsw.matna.service;

import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.MemberProfileVO;
import com.oopsw.matna.vo.MemberVO;
import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final RecipeRepository recipeRepository;

    private final MemberRepository memberRepository;

    public List<RecipeVO> getMypageRecipeList(Integer memberNo) {


        List<Recipe> recipes = recipeRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        return recipes.stream()
                .map(recipe -> RecipeVO.builder()
                        .recipeNo(recipe.getRecipeNo())
                        .title(recipe.getTitle())
                        .averageRating(recipe.getAverageRating())
                        .reviewCount(recipe.getReviewCount())
                        .thumbnailUrl(recipe.getImageUrl())
                        .difficulty(recipe.getDifficulty())
                        .prepTime(recipe.getPrepTime())
                        .servings(recipe.getServings())
                        .spicyLevel(recipe.getSpicyLevel())
                        .build()).collect(Collectors.toList());
    }

    public MemberProfileListResponse getMypageMember(Integer memberNo) {

        Member m = memberRepository.findById(memberNo).get();

        return MemberProfileListResponse.builder()
                .nickname(m.getNickname())
                .imageUrl(m.getImageUrl())
                .points(m.getPoint())
                .build();

    }

    public void removeMypageRecipe(Integer recipeNo) {
        Recipe recipe = recipeRepository.findById(recipeNo).get();

        recipe.setDelDate(LocalDateTime.now());
        recipeRepository.save(recipe);

    }
}
