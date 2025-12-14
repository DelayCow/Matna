package com.oopsw.matna.controller.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.dto.ReviewResponse;
import com.oopsw.matna.service.ReviewService;
import com.oopsw.matna.vo.ReviewsRegisterVO;
import com.oopsw.matna.vo.ReviewsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewRestController {
    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;

    @GetMapping("/recipe/{recipeNo}")
    public List<ReviewResponse> getRecipeReviews(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Integer recipeNo){
        List<ReviewsVO> reviews = reviewService.getRecipeReviews(recipeNo);
        return reviews.stream().map(reviewsVO -> ReviewResponse.builder()
                .reviewNo(reviewsVO.getReviewNo())
                .title(reviewsVO.getTitle())
                .content(reviewsVO.getContent())
                .reviewImage(reviewsVO.getReviewImage())
                .writerNickname(reviewsVO.getWriterNickname())
                .writerProfileImage(reviewsVO.getWriterProfileImage())
                .inDate(reviewsVO.getInDate())
                .rating(reviewsVO.getRating())
                .spicyLevel(reviewsVO.getSpicyLevel())
                .alternatives(reviewsVO.getAlternatives())
                .writer(principalDetails.getMemberNo().equals(reviewsVO.getWriterNo()))
                .build()).collect(Collectors.toList());
    }

    @GetMapping("/{reviewNo}")
    public ReviewResponse getReviewDetail(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Integer reviewNo){
        ReviewsVO reviewsVO = reviewService.getReviewDetail(reviewNo);
        return ReviewResponse.builder()
                .recipeNo(reviewsVO.getRecipeNo())
                .reviewNo(reviewsVO.getReviewNo())
                .title(reviewsVO.getTitle())
                .content(reviewsVO.getContent())
                .reviewImage(reviewsVO.getReviewImage())
                .writerNickname(reviewsVO.getWriterNickname())
                .writerProfileImage(reviewsVO.getWriterProfileImage())
                .inDate(reviewsVO.getInDate())
                .rating(reviewsVO.getRating())
                .spicyLevel(reviewsVO.getSpicyLevel())
                .alternatives(reviewsVO.getAlternatives())
                .writer(principalDetails.getMemberNo().equals(reviewsVO.getWriterNo()))
                .build();
    }

    @GetMapping("/recent")
    public List<ReviewResponse> getRecentReviews(){
        List<ReviewsVO> reviews = reviewService.getRecentReviews();
        return reviews.stream().map(reviewsVO -> ReviewResponse.builder()
                .reviewNo(reviewsVO.getReviewNo())
                .title(reviewsVO.getTitle())
                .content(reviewsVO.getContent())
                .reviewImage(reviewsVO.getReviewImage())
                .writerNickname(reviewsVO.getWriterNickname())
                .writerProfileImage(reviewsVO.getWriterProfileImage())
                .inDate(reviewsVO.getInDate())
                .rating(reviewsVO.getRating())
                .spicyLevel(reviewsVO.getSpicyLevel())
                .build()).collect(Collectors.toList());
    }

    @PostMapping("/{recipeNo}")
    public ResponseEntity<?> addReview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @RequestPart("reviewRequest") String reviewRequestJson,
                                       @RequestPart(value = "reviewImage") MultipartFile reviewImage) throws IOException {
        ReviewsRegisterVO reviewRegister = objectMapper.readValue(reviewRequestJson, ReviewsRegisterVO.class);
        Integer reviewNo = reviewService.addReview(principalDetails.getMemberNo(), reviewRegister, reviewImage);
        return ResponseEntity.ok(Map.of(
                "reviewNo", reviewNo,
                "message", "리뷰를 등록했습니다."
        ));
    }

    @PutMapping("/{recipeNo}")
    public ResponseEntity<?> editReview(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @RequestPart("reviewRequest") String reviewRequestJson,
                                       @RequestPart(value = "reviewImage", required = false) MultipartFile reviewImage) throws IOException {
        ReviewsRegisterVO reviewRegister = objectMapper.readValue(reviewRequestJson, ReviewsRegisterVO.class);
        Integer reviewNo = reviewService.editReview(principalDetails.getMemberNo(), reviewRegister, reviewImage);
        return ResponseEntity.ok(Map.of(
                "reviewNo", reviewNo,
                "message", "리뷰가 수정되었습니다."
        ));
    }

    @DeleteMapping("/{recipeNo}")
    public ResponseEntity<?> removeReview(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("reviewNo") int reviewNo) {
        reviewService.removeReview(principalDetails.getMemberNo(), reviewNo);
        return ResponseEntity.ok(Map.of(
                "reviewNo", reviewNo,
                "message", "리뷰가 삭제되었습니다."
        ));
    }
}
