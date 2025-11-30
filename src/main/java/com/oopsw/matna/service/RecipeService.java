package com.oopsw.matna.service;

import com.oopsw.matna.controller.recipe.RecipeRequest;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.IngredientVO;
import com.oopsw.matna.vo.RecipeStepVO;
import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final ImageStorageService imageStorageService;

    public Slice<RecipeVO> getRecipeList(Integer spiceLevel, String keyword, Pageable pageable) {
        Slice<Recipe> recipes = recipeRepository.findWithFilters(spiceLevel, keyword, pageable);

        Slice<RecipeVO> recipeList = recipes.map(recipe -> RecipeVO.builder()
                .recipeNo(recipe.getRecipeNo())
                .title(recipe.getTitle())
                .thumbnailUrl(recipe.getImageUrl())
                .writerNickname(recipe.getAuthor().getNickname())
                .writerProfile(recipe.getAuthor().getImageUrl())
                .reviewCount(recipe.getReviewCount())
                .averageRating(recipe.getAverageRating())
                .servings(recipe.getServings())
                .prepTime(recipe.getPrepTime())
                .difficulty(recipe.getDifficulty())
                .spicyLevel(recipe.getSpicyLevel())
                .build());

        return recipeList;
    }

    @Transactional
    public Integer addRecipe(RecipeRequest dto,
                             MultipartFile thumbnailFile,
                             Map<String, MultipartFile> stepImages) throws IOException {
        Member author = memberRepository.findById(dto.getMemberNo())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. (memberNo: " + dto.getMemberNo() + ")"));

        String thumbnailUrl = null;
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            throw new IllegalArgumentException("썸네일 이미지는 필수입니다.");
        }
        thumbnailUrl = imageStorageService.save(thumbnailFile, "recipe/thumbnails");

        Recipe recipe = Recipe.builder()
                .author(author)
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .category(dto.getCategory())
                .imageUrl(thumbnailUrl)
                .spicyLevel(dto.getSpicyLevel())
                .prepTime(dto.getPrepTime())
                .servings(dto.getServings())
                .difficulty(dto.getDifficulty())
                .inDate(LocalDateTime.now())
                .scrapCount(0)
                .reviewCount(0)
                .averageRating(0.0f)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        if (dto.getIngredient() == null || dto.getIngredient().isEmpty()) {
            throw new IllegalArgumentException("재료는 최소 1개 이상 필요합니다.");
        }

        for (IngredientVO ingredient : dto.getIngredient()) {
            Ingredient recipeIngredient = ingredientRepository.findByIngredientNameAndDelDateIsNull(ingredient.getIngredientName())
                    .orElseGet(() -> {
                        Ingredient newIngredient = Ingredient.builder()
                                .ingredientName(ingredient.getIngredientName())
                                .creator(author)
                                .inDate(LocalDateTime.now())
                                .build();
                        return ingredientRepository.save(newIngredient);
                    });

            RecipeIngredient recipeIngredients = RecipeIngredient.builder()
                    .recipe(savedRecipe)
                    .ingredient(recipeIngredient)
                    .amount(ingredient.getAmount().floatValue())
                    .unit(ingredient.getUnit())
                    .build();
            recipeIngredientRepository.save(recipeIngredients);
        }

        if (dto.getStep() == null || dto.getStep().isEmpty()) {
            throw new IllegalArgumentException("레시피 단계는 최소 1개 이상 필요합니다.");
        }

        for (RecipeStepVO step : dto.getStep()) {
            String stepImageUrl = null;

            String imageKey = step.getImageUrl();
            if (imageKey != null && stepImages.containsKey(imageKey)) {
                MultipartFile stepImage = stepImages.get(imageKey);
                if (stepImage != null && !stepImage.isEmpty()) {
                    stepImageUrl = imageStorageService.save(stepImage, "recipe/steps");
                }
            }

            if (stepImageUrl == null) {
                throw new IllegalArgumentException(step.getStepOrder() + "번째 단계의 이미지는 필수입니다.");
            }

            RecipeStep recipeStep = RecipeStep.builder()
                    .recipe(savedRecipe)
                    .stepOrder(step.getStepOrder())
                    .content(step.getContent())
                    .imageUrl(stepImageUrl)
                    .build();
            recipeStepRepository.save(recipeStep);
        }

        return savedRecipe.getRecipeNo();
    }
}
