package com.oopsw.matna.service;

import com.oopsw.matna.vo.ReviewsRegisterVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    void addReview() throws IOException {
        ReviewsRegisterVO vo = new ReviewsRegisterVO();
        vo.setWriterNo(23);
        vo.setRecipeNo(15);
        vo.setTitle("리뷰 테스트");
        vo.setContent("테스트!");
        vo.setRating(4.0f);
        vo.setSpicyLevel(2);
        vo.setReviewImage("img.jpg");

        List<ReviewsRegisterVO.AlternativeRegisterVO> altList = new ArrayList<>();
        ReviewsRegisterVO.AlternativeRegisterVO alt = new ReviewsRegisterVO.AlternativeRegisterVO();
        alt.setOriginalIngredientName("돼지고기(목살)");
        alt.setAlternativeIngredientName("대체재료테스트_3");
        alt.setAmount(100f);
        alt.setUnit("g");
        altList.add(alt);
        vo.setAlternatives(altList);
        //이미지 mock데이터
        String thumbnailPath = "src/main/resources/static/img/basil.jpg";
        MultipartFile thumbnailFile = new MockMultipartFile(
                "thumbnail",
                "thumbnail.jpg",
                "image/jpeg",
                new FileInputStream(thumbnailPath)
        );

        Integer reviewNo = reviewService.addReview(vo, thumbnailFile);
        System.out.println("등록된 리뷰 번호: " + reviewNo);

    }
}
