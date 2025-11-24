package com.oopsw.matna.Repository;



import com.oopsw.matna.repository.ReviewsRepository;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.ReviewListVO;
import com.oopsw.matna.vo.ReviewVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ReviewsRepositoryTest {

    @Autowired
    ReviewsRepository reviewsRepository;

    @Test
    public void getMyPageReviewListTest(){
        Integer memberNo = 11;
        List<Reviews> reviews = reviewsRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
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

    @Test
    public void removeReview(){
        Integer reviewNo = 18;
        Reviews review = reviewsRepository.findById(reviewNo).get();
        review.setDelDate(LocalDateTime.now());
        reviewsRepository.save(review);
    }

    @Test
    void getRecentReviewsTest() {

        List<Reviews> entities = reviewsRepository.findTop10ByDelDateIsNullOrderByInDateDesc();




        List<ReviewVO> voList = new ArrayList<>();

        for (Reviews r : entities) {
            ReviewVO vo = new ReviewVO();


            vo.setReviewNo(r.getReviewNo());
            vo.setTitle(r.getTitle());
            vo.setContent(r.getContent());
            vo.setReviewImage(r.getImageUrl());
            vo.setRating(r.getRating());
            vo.setSpicyLevel(r.getSpicyLevel());
            vo.setInDate(r.getInDate());


            if (r.getAuthor() != null) {
                vo.setWriterNickname(r.getAuthor().getNickname());
                vo.setWriterProfileImage(r.getAuthor().getImageUrl());
            }

            voList.add(vo);
        }



        for (ReviewVO vo : voList) {
            System.out.println("후기 번호: " + vo.getReviewNo());
            System.out.println("제목: " + vo.getTitle());
            System.out.println("내용: " + vo.getContent());
            System.out.println("사진 URL: " + vo.getReviewImage());
            System.out.println("별점: " + vo.getRating());
            System.out.println("맵기: " + vo.getSpicyLevel());
            System.out.println("작성일: " + vo.getInDate());

            System.out.println("작성자 닉네임: " + vo.getWriterNickname());
            System.out.println("작성자 프로필: " + vo.getWriterProfileImage());
            System.out.println("------------------------------");
        }

    }
}
