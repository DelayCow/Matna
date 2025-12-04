package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.IngredientVO;
import com.oopsw.matna.vo.RecipeDetailVO;
import com.oopsw.matna.vo.RecipeStepVO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    ReviewsRepository reviewsRepository;

    @Test
    @Transactional
    void getRecipeDetailTest() {
        Integer targetRecipeNo = 1;

        Recipe recipe = recipeRepository.findById(targetRecipeNo).get();

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

            ingVO.setIngredientName(ri.getIngredient().getIngredientName()); // 재료 테이블의 이름
            ingVO.setAmount(ri.getAmount());
            ingVO.setUnit(ri.getUnit());

            List<IngredientVO> altList = alternatives.stream()
                    .filter(alt -> alt.getOriginalIngredientName().equals(originName)) // 이름이 같은 것만 필터링
                    .map(alt -> IngredientVO.builder()
                            .ingredientName(alt.getAlternativeIngredientName())
                            .amount(alt.getAmount())
                            .unit(alt.getUnit())
                            .build())
                    .collect(Collectors.toList());

            ingVO.setAlternatives(altList); // VO에 넣어주기

            ingVOList.add(ingVO);

        }
        vo.setIngredients(ingVOList);

        // 순서 리스트 변환
        List<RecipeStepVO> stepVOList = new ArrayList<>();
        for (RecipeStep rs : rSteps) {
            RecipeStepVO stepVO = RecipeStepVO.builder()
                    .stepOrder(rs.getStepOrder())
                    .content(rs.getContent())
                    .imageUrl(rs.getImageUrl())
                    .build();
            stepVOList.add(stepVO);
        }
        vo.setSteps(stepVOList);

        List<Reviews> allReviews = reviewsRepository.findByRecipeAndDelDateIsNullOrderByInDateDesc(recipe);
        long totalReviewCount = allReviews.size();

        Map<Integer, Long> spicyLevelCounts = allReviews.stream()
                .collect(Collectors.groupingBy(
                        Reviews::getSpicyLevel, // spicyLevel로 그룹화
                        Collectors.counting()   // 그룹별 개수 세기
                ));

        // 3. 백분율 계산 및 결과 Map 생성
        Map<Integer, Double> spicyLevelPercentages = new LinkedHashMap<>();

        if (totalReviewCount > 0) {
            // 레벨 0부터 5까지 순서대로 처리
            for (int i = 0; i <= 5; i++) {
                // 해당 레벨의 개수를 가져옵니다. (없으면 0)
                long count = spicyLevelCounts.getOrDefault(i, 0L);

                // 백분율 계산: (개수 / 전체 수) * 100.0
                double percentage = (double) count / totalReviewCount * 100.0;

                // 소수점 2째 자리까지 반올림
                percentage = Math.round(percentage * 100.0) / 100.0;

                spicyLevelPercentages.put(i, percentage);
            }
        } else {
            // 후기가 없을 경우, 모두 0.0%로 설정
            for (int i = 0; i <= 5; i++) {
                spicyLevelPercentages.put(i, 0.0);
            }
        }
        System.out.println("=========================================");
        System.out.println(" [레시피 상세 정보]");
        System.out.println(" - 제목: " + vo.getTitle());
        System.out.println(" - 작성자: " + vo.getWriterNickname());
        System.out.println(" - 설명: " + vo.getSummary());
        System.out.println(" - 평점: " + vo.getRating() + " (후기 " + vo.getReviewCount() + "개)");
        System.out.println("-----------------------------------------");

        System.out.println(" [재료 목록]");
        for (RecipeDetailVO.DetailIngredientVO ing : vo.getIngredients()) {
            System.out.printf(" - %s %s%s\n", ing.getIngredientName(), ing.getAmount(), ing.getUnit());

            if (ing.getAlternatives() != null && !ing.getAlternatives().isEmpty()) {
                for (IngredientVO alt : ing.getAlternatives()) {
                    System.out.printf("   └─ [대체] %s %s%s\n", alt.getIngredientName(), alt.getAmount(), alt.getUnit());
                }
            }
        }
        System.out.println("-----------------------------------------");

        System.out.println(" [조리 순서]");
        for (RecipeStepVO step : vo.getSteps()) {
            System.out.println(" Step " + step.getStepOrder() + ". " + step.getContent());
            System.out.println("   (이미지: " + step.getImageUrl() + ")");
        }

        System.out.println("-----------------------------------------");
        System.out.println(" [후기 매운맛 레벨 분포 (총 " + totalReviewCount + "개)]");
        if (totalReviewCount == 0) {
            System.out.println(" - 해당 레시피에 작성된 후기가 없습니다.");
        } else {
            for (Map.Entry<Integer, Double> entry : spicyLevelPercentages.entrySet()) {
                System.out.printf(" - 레벨 %d: %.2f%%\n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("=========================================");

    }

}
