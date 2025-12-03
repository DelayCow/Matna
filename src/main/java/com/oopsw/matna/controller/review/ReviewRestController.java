package com.oopsw.matna.controller.review;

import com.oopsw.matna.dto.ReviewResponse;
import com.oopsw.matna.service.ReviewService;
import com.oopsw.matna.vo.ReviewsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewRestController {
    private final ReviewService reviewService;

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
}
