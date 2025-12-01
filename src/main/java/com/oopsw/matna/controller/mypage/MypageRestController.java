package com.oopsw.matna.controller.mypage;

import java.util.Map;
import com.oopsw.matna.dto.MemberProfileListResponse;
import com.oopsw.matna.dto.RecipeListResponse;
import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

        return mypageService.getMypageMember(memberNo);
    }

    @PostMapping("/{recipeNo}/recipe")
    public void removeRecipe(@PathVariable("recipeNo") int recipeNo) {


        mypageService.removeMypageRecipe(recipeNo);
    }

    @GetMapping("/{memberNo}/reviewList")
    public void getMypageReviewList(@PathVariable("memberNo") int memberNo) {

        mypageService.getMypageReviewsList(memberNo);
    }

    @PostMapping("/{reviewNo}/review")
    public void removeReview(@PathVariable("reviewNo") int reviewNo) {

        mypageService.removeReviews(reviewNo);
    }

    @PostMapping("/groupbuy/shared")
    public void confirmShareReceive(@RequestBody GroupBuyParticipantVO sharedData) {

        mypageService.editShareGroupBuy(sharedData);
    }

    @PostMapping("/payment")
    public void registerPayment(@RequestBody GroupBuyVO paymentData) {

        mypageService.editPayment(paymentData);
    }

    @PostMapping("/groupbuy/arrival")
    public void registerArrival(@RequestBody GroupBuyVO deliveryData) {

        mypageService.addArrival(deliveryData);
    }

    @DeleteMapping("/getout/{memberNo}")
    public LocalDateTime deleteMember(@PathVariable("memberNo") int memberNo) {

        return mypageService.removeMember(memberNo);
    }

    @PostMapping("/checkModal/checkPassword")
    public boolean checkPassword(@RequestBody Map<String, Object> requestData) {

        Integer memberNo = (Integer) requestData.get("memberNo");
        String password = (String) requestData.get("password");

        return mypageService.checkPassword(memberNo, password);
    }

    @GetMapping("/{memberNo}/info")
    public MemberVO getMemberInfo(@PathVariable("memberNo") int memberNo) {

        return mypageService.getMemberInfo(memberNo);
    }

    @PutMapping("/{memberNo}/infoEdit")
    public void updateProfile(@PathVariable("memberNo") int memberNo, @RequestBody MemberVO editData) {

        editData.setMemberNo(memberNo);

        mypageService.updateMemberProfile(editData);
    }

    // MypageRestController.java

    @PostMapping("/point/refund")
    public ResponseEntity<?> refundPoint(@RequestBody Map<String, Integer> requestData) {
        int memberNo = requestData.get("memberNo");
        int amount = requestData.get("amount");

        try {
            int remainingPoint = mypageService.refundPoint(memberNo, amount);
            return ResponseEntity.ok(remainingPoint);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/report/member")
    public void reportMember(@RequestBody AllReportVO reportVO) {

        mypageService.addReportMember(reportVO);
    }

    @PostMapping("/report/group")
    public void reportGroup(@RequestBody AllReportVO reportVO) {

        mypageService.addReportGroupBuy(reportVO);
    }

    @PostMapping("/point/charge")
    public ResponseEntity<?> chargePoint(@RequestBody Map<String, Integer> requestData) {
        int memberNo = requestData.get("memberNo");
        int amount = requestData.get("amount");

        try {
            int resultPoint = mypageService.chargePoint(memberNo, amount);
            return ResponseEntity.ok(resultPoint);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
