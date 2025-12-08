package com.oopsw.matna.controller.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.dto.ReviewResponse;
import com.oopsw.matna.service.ReviewService;
import com.oopsw.matna.vo.ReviewsRegisterVO;
import com.oopsw.matna.vo.ReviewsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public List<ReviewResponse> getRecipeReviews(@PathVariable Integer recipeNo){
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
                .build()).collect(Collectors.toList());
    }

    @GetMapping("/{reviewNo}")
    public ReviewResponse getReviewDetail(@PathVariable Integer reviewNo){
        ReviewsVO reviewsVO = reviewService.getReviewDetail(reviewNo);
        return ReviewResponse.builder()
                .reviewNo(reviewsVO.getReviewNo())
                .title(reviewsVO.getTitle())
                .content(reviewsVO.getContent())
                .reviewImage(reviewsVO.getReviewImage())
                .writerNickname(reviewsVO.getWriterNickname())
                .writerProfileImage(reviewsVO.getWriterProfileImage())
                .inDate(reviewsVO.getInDate())
                .rating(reviewsVO.getRating())
                .spicyLevel(reviewsVO.getSpicyLevel())
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
    public ResponseEntity<?> addReview(@RequestPart("reviewRequest") String reviewRequestJson,
                                       @RequestPart(value = "reviewImage", required = false) MultipartFile reviewImage) throws IOException {
        ReviewsRegisterVO reviewRegister = objectMapper.readValue(reviewRequestJson, ReviewsRegisterVO.class);
        Integer reviewNo = reviewService.addReview(reviewRegister, reviewImage);
        return ResponseEntity.ok(Map.of(
                "recipeNo", reviewNo,
                "message", "리뷰를 등록했습니다."
        ));
    }
}
