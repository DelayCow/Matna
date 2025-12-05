package com.oopsw.matna.controller.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.dto.RecipeResponse;
import com.oopsw.matna.service.ImageStorageService;
import com.oopsw.matna.service.RecipeService;
import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecipeRestController {
    private final RecipeService recipeService;
    private final ImageStorageService imageStorageService;
    private final ObjectMapper objectMapper;

    @GetMapping("/recipes")
    public Slice<RecipeResponse> getRecipeList(
            @RequestParam(required = false) Integer spicyLevel,
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "inDate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "reviewCount", direction = Sort.Direction.DESC)
            })
            Pageable pageable) {
        Slice<RecipeVO> recipeList = recipeService.getRecipeList(spicyLevel, keyword, pageable);
        Slice<RecipeResponse> result = recipeList.map(recipe -> RecipeResponse.builder()
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
                .build());
        return result;
    }

    @PostMapping("/recipes")
    public ResponseEntity<?> addRecipe(@AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart("recipeRequest") String recipeRequestJson,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam Map<String, MultipartFile> stepImages) throws IOException {
        RecipeRequest recipeRequest = objectMapper.readValue(recipeRequestJson, RecipeRequest.class);
        Integer recipeNo = recipeService.addRecipe(recipeRequest, thumbnailFile, stepImages, principalDetails.getMemberNo());

        return ResponseEntity.ok(Map.of(
                "recipeNo", recipeNo,
                "message", "레시피를 등록했습니다."
        ));
    }

    @PutMapping("/recipes")
    public ResponseEntity<?> editRecipe(@AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestPart("recipeRequest") String recipeRequestJson,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam Map<String, MultipartFile> stepImages) throws IOException {
        RecipeRequest recipeRequest = objectMapper.readValue(recipeRequestJson, RecipeRequest.class);
        Integer recipeNo = recipeService.editRecipe(recipeRequest, thumbnailFile, stepImages, principalDetails.getMemberNo());

        return ResponseEntity.ok(Map.of(
                "recipeNo", recipeNo,
                "message", "레시피를 수정했습니다."
        ));
    }

    @DeleteMapping("/recipes/{recipeNo}")
    public ResponseEntity<?> deleteRecipe(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Integer recipeNo) {
        recipeService.removeRecipe(principalDetails.getMemberNo(), recipeNo);
        return ResponseEntity.ok(Map.of(
                "recipeNo", recipeNo,
                "message", "레시피를 삭제했습니다."
        ));
    }
//    @PostMapping("/images") //이미지 업로드 테스트 컨트롤러
//    public ResponseEntity<?> addRecipeImage(
//            @RequestPart(value="image") MultipartFile imageFile
//    ) throws IOException {
//        String imageUrl = null;
//        if (imageFile == null || imageFile.isEmpty()) {
//            throw new IllegalArgumentException("썸네일 이미지는 필수입니다.");
//        }
//        imageUrl = imageStorageService.save(imageFile, "/recipe/thumbnails");
//        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
//    }

}
