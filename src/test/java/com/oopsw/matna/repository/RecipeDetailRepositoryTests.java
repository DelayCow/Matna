package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeAlternativeIngredient;
import com.oopsw.matna.repository.entity.RecipeIngredient;
import com.oopsw.matna.repository.entity.RecipeStep;
import com.oopsw.matna.vo.RecipeDetailVO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class RecipeDetailRepositoryTests {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    RecipeStepRepository recipeStepRepository;

    @Autowired
    RecipeAlternativeIngredientRepository recipeAlternativeIngredientRepository;

    @Test
    @Transactional
    void getRecipeDetailTest() {
        Integer targetRecipeNo = 1;

        Recipe recipe = recipeRepository.findById(targetRecipeNo)
                .get();


        List<RecipeIngredient> rIngredients = recipeIngredientRepository.findByRecipe(recipe);

        List<RecipeStep> rSteps = recipeStepRepository.findByRecipeOrderByStepOrderAsc(recipe);

        List<RecipeAlternativeIngredient> alternatives =
                recipeAlternativeIngredientRepository.findByReview_Recipe_RecipeNo(targetRecipeNo);

        RecipeDetailVO vo = new RecipeDetailVO();

        // 기본 정보
        vo.setRecipeNo(recipe.getRecipeNo());
        vo.setTitle(recipe.getTitle());
        vo.setSummary(recipe.getSummary());
        vo.setThumbnailUrl(recipe.getImageUrl());
        vo.setRating(recipe.getAverageRating());
        vo.setReviewCount(recipe.getReviewCount());
        vo.setServings(recipe.getServings());
        vo.setPrepTime(recipe.getPrepTime());
        vo.setDifficulty(recipe.getDifficulty());
        vo.setSpicyLevel(recipe.getSpicyLevel());
        vo.setInDate(recipe.getInDate());

        // 작성자 정보
        if (recipe.getAuthor() != null) {
            vo.setWriterNickname(recipe.getAuthor().getNickname());
            vo.setWriterProfile(recipe.getAuthor().getImageUrl());
        }

        // 재료 리스트 변환
        List<RecipeDetailVO.DetailIngredientVO> ingVOList = new ArrayList<>();
        for (RecipeIngredient ri : rIngredients) {
            RecipeDetailVO.DetailIngredientVO ingVO = new RecipeDetailVO.DetailIngredientVO();

            String originName = ri.getIngredient().getIngredientName();

            ingVO.setName(ri.getIngredient().getIngredientName()); // 재료 테이블의 이름
            ingVO.setAmount(ri.getAmount());
            ingVO.setUnit(ri.getUnit());

            List<RecipeDetailVO.AlternativeVO> altList = alternatives.stream()
                    .filter(alt -> alt.getOriginalIngredientName().equals(originName)) // 이름이 같은 것만 필터링
                    .map(alt -> new RecipeDetailVO.AlternativeVO(
                            alt.getAlternativeIngredientName(),
                            alt.getAmount(),
                            alt.getUnit()
                    ))
                    .collect(Collectors.toList());

            ingVO.setAlternatives(altList); // VO에 넣어주기

            ingVOList.add(ingVO);

        }
        vo.setIngredients(ingVOList);

        // 순서 리스트 변환
        List<RecipeDetailVO.DetailStepVO> stepVOList = new ArrayList<>();
        for (RecipeStep rs : rSteps) {
            RecipeDetailVO.DetailStepVO stepVO = new RecipeDetailVO.DetailStepVO();
            stepVO.setStepOrder(rs.getStepOrder());
            stepVO.setContent(rs.getContent());
            stepVO.setImageUrl(rs.getImageUrl());
            stepVOList.add(stepVO);
        }
        vo.setSteps(stepVOList);



        System.out.println("=========================================");
        System.out.println(" [레시피 상세 정보]");
        System.out.println(" - 제목: " + vo.getTitle());
        System.out.println(" - 작성자: " + vo.getWriterNickname());
        System.out.println(" - 설명: " + vo.getSummary());
        System.out.println(" - 평점: " + vo.getRating() + " (후기 " + vo.getReviewCount() + "개)");
        System.out.println("-----------------------------------------");

        System.out.println(" [재료 목록]");
        for (RecipeDetailVO.DetailIngredientVO ing : vo.getIngredients()) {
            System.out.printf(" - %s %s%s\n", ing.getName(), ing.getAmount(), ing.getUnit());

            if (ing.getAlternatives() != null && !ing.getAlternatives().isEmpty()) {
                for (RecipeDetailVO.AlternativeVO alt : ing.getAlternatives()) {
                    System.out.printf("   └─ [대체] %s %s%s\n", alt.getName(), alt.getAmount(), alt.getUnit());
                }
            }
        }
        System.out.println("-----------------------------------------");

        System.out.println(" [조리 순서]");
        for (RecipeDetailVO.DetailStepVO step : vo.getSteps()) {
            System.out.println(" Step " + step.getStepOrder() + ". " + step.getContent());
            System.out.println("   (이미지: " + step.getImageUrl() + ")");
        }
        System.out.println("=========================================");

    }

}
