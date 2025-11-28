package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.dto.RecipeListResponse;
import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.MemberProfileVO;
import com.oopsw.matna.vo.MemberVO;
import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageRestController {
    private final MypageService mypageService;

    @GetMapping("/{memberNo}/recipe")
    public List<RecipeListResponse> getMypageRecipeList(@PathVariable("memberNo") int memberNo) {
        List<RecipeVO> recipelist = mypageService.getMypageRecipeList(memberNo);
        List<RecipeListResponse> result = recipelist.stream()
                .map(recipe -> RecipeListResponse.builder()
                        .id(recipe.getRecipeNo())
                        .title(recipe.getTitle())
                        .rating(recipe.getAverageRating())
                        .image(recipe.getThumbnailUrl())
                        .reviewCount(recipe.getReviewCount())
                        .difficulty(recipe.getDifficulty())
                        .time(recipe.getPrepTime())
                        .serving(recipe.getServings())
                        .spicy(recipe.getSpicyLevel())
                        .build()).collect(Collectors.toList());
        return result;
    }

    @GetMapping("/{memberNo}/profile")
    public MemberProfileListResponse getMypageProfileList(@PathVariable("memberNo") int memberNo) {

        MemberProfileListResponse memberProfile = mypageService.getMypageMember(memberNo);

        return MemberProfileListResponse.builder()
                .nickname(memberProfile.getNickname())
                .imageUrl(memberProfile.getImageUrl())
                .points(memberProfile.getPoints())
                .build();
    }

    @PostMapping("/{recipeNo}/recipe")
    public void removeRecipe(@PathVariable("recipeNo") int recipeNo) {

        List<RecipeVO> recipe = mypageService.getMypageRecipeList(recipeNo);

        mypageService.removeMypageRecipe(recipeNo);
    }

}
