package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.ReviewRepository;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.ReviewListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void getMyPageReviewListTest(){
        Integer memberNo = 11;
        List<Reviews> reviews = reviewRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        List<ReviewListVO> reviewList = reviews.stream()
                .map(review -> ReviewListVO.builder()
                        .reviewNo(review.getReviewNo())
                        .title(review.getTitle())
                        .imageUrl(review.getImageUrl())
                        .rating(review.getRating())
                        .inDate(review.getInDate())
                        .build()).collect(Collectors.toList());
        System.out.println(reviewList);
    }
}
