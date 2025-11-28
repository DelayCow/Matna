package com.oopsw.matna.service;

import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.ReviewsRepository;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final RecipeRepository recipeRepository;

    private final MemberRepository memberRepository;

    private final ReviewsRepository reviewsRepository;

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

    public List<ReviewsListVO> getMypageReviewsList(Integer memberNo) {

        List<ReviewsListVO> entities = reviewsRepository.findReviewsListVOByRecipe_RecipeNoAndDelDateIsNullOrderByInDateDesc(memberNo);

        List<ReviewsListVO> reviewsListVOList = new ArrayList<>();

        List<ReviewsListVO> voList = reviewsListVOList.stream()
                .map(r -> ReviewsListVO.builder()
                        .reviewNo(r.getReviewNo())
                        .title(r.getTitle())
                        // .content(r.getContent()) // 주석된 부분은 필요시 해제
                        .imageUrl(r.getImageUrl())
                        .rating(r.getRating())
                        .inDate(r.getInDate())
                        .build())
                .collect(Collectors.toList());

            reviewsListVOList.add((ReviewsListVO) voList);

            return reviewsListVOList;
    }
}
