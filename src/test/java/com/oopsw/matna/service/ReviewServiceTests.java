package com.oopsw.matna.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReviewServiceTests {
    @Autowired
    private ReviewService reviewService;

    @Test
    void getRecipeReviewsTest(){
        System.out.println(reviewService.getRecipeReviews(1));
    }

    @Test
    void getReviewDetail(){
        System.out.println(reviewService.getReviewDetail(1));
    }

    @Test
    void getRecentReview(){System.out.println(reviewService.getRecentReviews());}
}
