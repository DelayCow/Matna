package com.oopsw.matna.service;

import com.oopsw.matna.controller.recipe.RecipeRequest;
import com.oopsw.matna.dto.RecipeResponse;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.vo.IngredientVO;
import com.oopsw.matna.vo.MemberVO;
import com.oopsw.matna.vo.RecipeStepVO;
import com.oopsw.matna.vo.RecipeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class RecipeServiceTests {
    @Autowired
    private RecipeService recipeService;

    @Test
    public void getRecipeListTest() {
        Slice<RecipeVO> recipeList =recipeService.getRecipeList(null, null, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "reviewCount")));
        List<RecipeResponse> result = recipeList.stream().map(recipe -> RecipeResponse.builder()
                .recipeNo(recipe.getRecipeNo())
                .title(recipe.getTitle())
                .thumbnailUrl(recipe.getThumbnailUrl())
                .writerNickname(recipe.getWriterNickname())
                .writerProfile(recipe.getWriterProfile())
                .reviewCount(recipe.getReviewCount())
                .averageRating(recipe.getAverageRating())
                .servings(recipe.getServings())
                .prepTime(recipe.getPrepTime())
                .difficulty(recipe.getDifficulty())
                .spicyLevel(recipe.getSpicyLevel())
                .build()).collect(Collectors.toList());

        System.out.println("리뷰순 3개" + result);
        System.out.println("다음페이지유무" + recipeList.hasNext());
    }

    @Test
    void addRecipeTest() throws IOException {
        // Given
        RecipeRequest recipeDto = RecipeRequest.builder()
                .memberNo(5)
                .title("테스트").summary("테스트").category("korean").difficulty("easy")
                .prepTime(10).spicyLevel(0).servings(1)
                .build();

        // 재료
        List<IngredientVO> ingredients = new ArrayList<>();
        IngredientVO ingredient = IngredientVO.builder()
                .ingredientName("양파").amount(100.0f).unit("g")
                .build();

        ingredients.add(ingredient);
        recipeDto.setIngredient(ingredients);

        // 단계
        List<RecipeStepVO> steps = new ArrayList<>();
        RecipeStepVO step = RecipeStepVO.builder()
                .stepOrder(1).content("테스트").imageUrl("stepImage_1").build();
        steps.add(step);
        recipeDto.setStep(steps);

        // 실제 파일 경로에서 읽기 (경로는 실제 환경에 맞게 수정)
        String thumbnailPath = "src/main/resources/static/img/basil.jpg";
        String stepImagePath = "src/main/resources/static/img/burger.png";

        MultipartFile thumbnailFile = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
                "image/jpeg",
                new FileInputStream(thumbnailPath)
        );

        Map<String, MultipartFile> stepImages = new HashMap<>();
        stepImages.put("stepImage_1", new MockMultipartFile(
                "step1",
                "step1.jpg",
                "image/jpeg",
                new FileInputStream(stepImagePath)
        ));

        Integer recipeNo = recipeService.addRecipe(recipeDto, thumbnailFile, stepImages);
        System.out.println("등록된 레시피 번호: " + recipeNo);
    }

    @Test
    public void getRecipeDetail(){
        System.out.println(recipeService.getRecipeDetail(1));
    }
}
