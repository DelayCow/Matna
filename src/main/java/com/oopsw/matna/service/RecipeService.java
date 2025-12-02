package com.oopsw.matna.service;

import com.oopsw.matna.controller.recipe.RecipeRequest;
import com.oopsw.matna.repository.*;
import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.*;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final MemberRepository memberRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final ImageStorageService imageStorageService;
    private final RecipeAlternativeIngredientRepository recipeAlternativeIngredientRepository;
    private final ReviewsRepository reviewsRepository;
    private final GroupBuyRepository groupBuyRepository;

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
    public Integer addRecipe(RecipeRequest dto, MultipartFile thumbnailFile,
                             Map<String, MultipartFile> stepImages, Integer memberNo) throws IOException {
        Member author = memberRepository.findById(memberNo)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. (memberNo: " + memberNo + ")"));

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

    @Transactional
    public RecipeDetailVO getRecipeDetail(Integer recipeNo) {
        Recipe recipe = recipeRepository.findById(recipeNo).get();

        List<RecipeIngredient> rIngredients = recipeIngredientRepository.findByRecipe(recipe);

        List<RecipeStep> rSteps = recipeStepRepository.findByRecipeOrderByStepOrderAsc(recipe);

        List<RecipeAlternativeIngredient> alternatives =
                recipeAlternativeIngredientRepository.findByReview_Recipe_RecipeNo(recipeNo);

        RecipeDetailVO vo = new RecipeDetailVO();

        vo.setRecipeNo(recipe.getRecipeNo());
        vo.setTitle(recipe.getTitle());
        vo.setSummary(recipe.getSummary());
        vo.setCategory(recipe.getCategory());
        vo.setThumbnailUrl(recipe.getImageUrl());
        vo.setRating(recipe.getAverageRating());
        vo.setReviewCount(recipe.getReviewCount());
        vo.setServings(recipe.getServings());
        vo.setPrepTime(recipe.getPrepTime());
        vo.setDifficulty(recipe.getDifficulty());
        vo.setSpicyLevel(recipe.getSpicyLevel());
        vo.setInDate(recipe.getInDate());

        if (recipe.getAuthor() != null) {
            vo.setWriterNo(recipe.getAuthor().getMemberNo());
            vo.setWriterNickname(recipe.getAuthor().getNickname());
            vo.setWriterProfile(recipe.getAuthor().getImageUrl());
        }

        List<RecipeDetailVO.DetailIngredientVO> ingVOList = new ArrayList<>();
        for (RecipeIngredient ri : rIngredients) {
            RecipeDetailVO.DetailIngredientVO ingVO = new RecipeDetailVO.DetailIngredientVO();

            String originName = ri.getIngredient().getIngredientName();

            ingVO.setIngredientName(ri.getIngredient().getIngredientName());
            ingVO.setAmount(ri.getAmount());
            ingVO.setUnit(ri.getUnit());

            boolean hasActiveGroupBuy = groupBuyRepository
                    .existsByIngredient_IngredientNoAndStatus(ri.getRecipeIngredientNo(), "OPEN");

            ingVO.setIsGroupBuying(hasActiveGroupBuy);

            List<IngredientVO> altList = alternatives.stream()
                    .filter(alt -> alt.getOriginalIngredientName().equals(originName))
                    .map(alt -> {
                        // 1. 대체 재료 이름으로 Ingredient 엔티티를 찾습니다.
                        Ingredient alternativeIngredient = ingredientRepository
                                .findByIngredientNameAndDelDateIsNull(alt.getAlternativeIngredientName())
                                .orElse(null); // 찾지 못하면 null 처리 (공구 여부 확인 불가)

                        boolean hasAltGroupBuy = false;
                        if (alternativeIngredient != null) {
                            hasAltGroupBuy = groupBuyRepository
                                    .existsByIngredient_IngredientNoAndStatus(alternativeIngredient.getIngredientNo(), "open");
                        }

                        return IngredientVO.builder()
                                .ingredientName(alt.getAlternativeIngredientName())
                                .amount(alt.getAmount()).unit(alt.getUnit())
                                .isGroupBuying(hasAltGroupBuy).build();
                    })
                    .collect(Collectors.toList());

            ingVO.setAlternatives(altList);

            ingVOList.add(ingVO);

        }
        vo.setIngredients(ingVOList);

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
        List<ReviewsListVO> reviewsVOList = new ArrayList<>();
        for (Reviews rs : allReviews) {
            ReviewsListVO reviewVO = ReviewsListVO.builder()
                    .reviewNo(rs.getReviewNo())
                    .imageUrl(rs.getImageUrl())
                    .build();
            reviewsVOList.add(reviewVO);
        }
        vo.setReviews(reviewsVOList);
        long totalReviewCount = allReviews.size();

        Map<Integer, Long> spicyLevelCounts = allReviews.stream()
                .collect(Collectors.groupingBy(
                        Reviews::getSpicyLevel,
                        Collectors.counting()
                ));

        Map<Integer, Double> spicyLevelPercentages = new LinkedHashMap<>();

        if (totalReviewCount > 0) {
            for (int i = 0; i <= 5; i++) {
                long count = spicyLevelCounts.getOrDefault(i, 0L);
                double percentage = (double) count / totalReviewCount * 100.0;
                percentage = Math.round(percentage * 100.0) / 100.0;

                spicyLevelPercentages.put(i, percentage);
            }
        } else {
            for (int i = 0; i <= 5; i++) {
                spicyLevelPercentages.put(i, 0.0);
            }
        }
        vo.setSpicyLevelPercentages(spicyLevelPercentages);
        return vo;
    }

    @Transactional
    public Integer editRecipe(RecipeRequest dto, MultipartFile thumbnailFile,
                              Map<String, MultipartFile> stepImages, Integer memberNo) throws IOException {
        Recipe recipe = recipeRepository.findById(dto.getRecipeNo()).get();

        if (!recipe.getAuthor().getMemberNo().equals(memberNo)) {
            throw new IllegalArgumentException("레시피를 수정할 권한이 없습니다.");
        }

        String thumbnailUrl;
        String currentThumbnail = recipe.getImageUrl();

        if (dto.getThumnailUrl() != null && dto.getThumnailUrl().equals(currentThumbnail)) {
            thumbnailUrl = currentThumbnail;
        } else if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // 새 파일 업로드
            if (currentThumbnail != null) {
                imageStorageService.delete(currentThumbnail);
            }
            thumbnailUrl = imageStorageService.save(thumbnailFile, "recipe/thumbnails");
        } else {
            throw new IllegalArgumentException("썸네일 이미지는 필수입니다.");
        }

        recipe.setTitle(dto.getTitle());
        recipe.setSummary(dto.getSummary());
        recipe.setCategory(dto.getCategory());
        recipe.setImageUrl(thumbnailUrl);
        recipe.setSpicyLevel(dto.getSpicyLevel());
        recipe.setDifficulty(dto.getDifficulty());
        recipe.setCategory(dto.getCategory());
        recipe.setServings(dto.getServings());

        Recipe savedRecipe = recipeRepository.save(recipe);

        List<RecipeIngredient> existingIngredients = recipeIngredientRepository.findByRecipe(recipe);

        if (dto.getIngredient() == null || dto.getIngredient().isEmpty()) {
            throw new IllegalArgumentException("재료는 최소 1개 이상 필요합니다.");
        }

        if (existingIngredients.size() != dto.getIngredient().size()) {
            throw new IllegalArgumentException("재료 개수가 일치하지 않습니다.");
        }

        // 재료 값만 업데이트 (Setter 사용)
        for (int i = 0; i < existingIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = existingIngredients.get(i);
            IngredientVO newData = dto.getIngredient().get(i);

            // 재료가 변경되었는지 확인
            if (!recipeIngredient.getIngredient().getIngredientName().equals(newData.getIngredientName())) {
                // 다른 재료로 변경된 경우
                Ingredient ingredient = ingredientRepository.findByIngredientNameAndDelDateIsNull(newData.getIngredientName())
                        .orElseGet(() -> {
                            Ingredient newIngredient = Ingredient.builder()
                                    .ingredientName(newData.getIngredientName())
                                    .creator(recipe.getAuthor())
                                    .inDate(LocalDateTime.now())
                                    .build();
                            return ingredientRepository.save(newIngredient);
                        });
                recipeIngredient.setIngredient(ingredient);
            }

            // 수량/단위 업데이트 (Setter 사용)
            recipeIngredient.setAmount(newData.getAmount().floatValue());
            recipeIngredient.setUnit(newData.getUnit());
        }

        // 5. 기존 스텝 조회
        List<RecipeStep> existingSteps = recipeStepRepository.findByRecipeOrderByStepOrderAsc(recipe);

        if (dto.getStep() == null || dto.getStep().isEmpty()) {
            throw new IllegalArgumentException("레시피 단계는 최소 1개 이상 필요합니다.");
        }

        if (existingSteps.size() != dto.getStep().size()) {
            throw new IllegalArgumentException("레시피 단계 개수가 일치하지 않습니다.");
        }

        int newFileIndex = 0;

        // 스텝 값만 업데이트 (Setter 사용)
        for (int i = 0; i < existingSteps.size(); i++) {
            RecipeStep recipeStep = existingSteps.get(i);
            RecipeStepVO newData = dto.getStep().get(i);
            String oldImageUrl = recipeStep.getImageUrl();
            String stepImageUrl;

            // 이미지 처리
            if (newData.getImageUrl() != null && newData.getImageUrl().equals(oldImageUrl)) {
                // 기존 이미지 유지
                stepImageUrl = oldImageUrl;
            } else if (stepImages != null && newFileIndex < stepImages.size()) {
                // 새 파일 업로드
                MultipartFile stepImage = stepImages.get(newFileIndex++);
                if (stepImage == null || stepImage.isEmpty()) {
                    throw new IllegalArgumentException(newData.getStepOrder() + "번째 단계의 이미지는 필수입니다.");
                }
                // 기존 이미지 삭제
                if (oldImageUrl != null) {
                    imageStorageService.delete(oldImageUrl);
                }
                stepImageUrl = imageStorageService.save(stepImage, "recipe/steps");
            } else {
                throw new IllegalArgumentException(newData.getStepOrder() + "번째 단계의 이미지는 필수입니다.");
            }

            // 내용 및 이미지 업데이트 (Setter 사용)
            recipeStep.setContent(newData.getContent());
            recipeStep.setImageUrl(stepImageUrl);
        }

        return savedRecipe.getRecipeNo();
    }
}
