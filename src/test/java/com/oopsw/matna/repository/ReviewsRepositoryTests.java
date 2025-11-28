package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeAlternativeIngredient;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.ReviewsListVO;
import com.oopsw.matna.vo.ReviewsVO;
import com.oopsw.matna.vo.ReviewsRegisterVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class ReviewsRepositoryTests {

    @Autowired
    ReviewsRepository reviewsRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RecipeAlternativeIngredientRepository recipeAlternativeIngredientRepository;

    @Test
    public void getMyPageReviewListTest(){
        Integer memberNo = 11;
        List<Reviews> reviews = reviewsRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        List<ReviewsListVO> reviewList = reviews.stream()
                .map(review -> ReviewsListVO.builder()
                        .reviewNo(review.getReviewNo())
                        .title(review.getTitle())
                        .imageUrl(review.getImageUrl())
                        .rating(review.getRating())
                        .inDate(review.getInDate())
                        .build()).collect(Collectors.toList());
        System.out.println(reviewList);
    }

    @Test
    @Transactional
    @Rollback(false)
    public void removeReview(){
        Integer reviewNo = 2;
        Reviews review = reviewsRepository.findById(reviewNo).get();
        Recipe recipe = review.getRecipe();

       int recipeReviewCount = recipe.getReviewCount();
       float recipeAverageRating = recipe.getAverageRating();

        review.setDelDate(LocalDateTime.now());
        reviewsRepository.save(review);

        recipe.removeRating(review.getRating());
        recipe.setUpdateDate(LocalDateTime.now());
        recipeRepository.save(recipe);


        System.out.println(">>> 후기 삭제 완료 (No." + reviewNo + ")");
        System.out.println(">>> 레시피 후기 수 변경: " + recipeReviewCount + " -> " + recipe.getReviewCount());


    }

    @Test
    @Transactional
    @Rollback(false)
    void registerReviewTest() {

        Integer writerNo = 11;
        Integer recipeNo = 1;

        ReviewsRegisterVO vo = new ReviewsRegisterVO();
        vo.setWriterNo(writerNo);
        vo.setRecipeNo(recipeNo);
        vo.setTitle("리뷰 테스트");
        vo.setContent("테스트!");
        vo.setRating(5.0f);
        vo.setSpicyLevel(1);
        vo.setReviewImage("img.jpg");


        List<ReviewsRegisterVO.AlternativeRegisterVO> altList = new ArrayList<>();
        ReviewsRegisterVO.AlternativeRegisterVO alt = new ReviewsRegisterVO.AlternativeRegisterVO();
        alt.setOriginalIngredientName("돼지고기(목살)");
        alt.setAlternativeIngredientName("대체재료테스트_3");
        alt.setAmount(100f);
        alt.setUnit("g");
        altList.add(alt);
        vo.setAlternatives(altList);


        Member writer = memberRepository.findById(vo.getWriterNo()).get();
        Recipe recipe = recipeRepository.findById(vo.getRecipeNo()).get();

        // (1) 후기 저장
        Reviews newReview = Reviews.builder()
                .author(writer)
                .recipe(recipe)
                .title(vo.getTitle())
                .content(vo.getContent())
                .rating(vo.getRating())
                .spicyLevel(vo.getSpicyLevel())
                .imageUrl(vo.getReviewImage())
                .likesCount(0)
                .inDate(LocalDateTime.now())
                .build();
        reviewsRepository.save(newReview);

        // (2) 대체 재료 저장
        if (vo.getAlternatives() != null) {
            for (ReviewsRegisterVO.AlternativeRegisterVO altVO : vo.getAlternatives()) {
                RecipeAlternativeIngredient altEntity = RecipeAlternativeIngredient.builder()
                        .review(newReview)
                        .originalIngredientName(altVO.getOriginalIngredientName())
                        .alternativeIngredientName(altVO.getAlternativeIngredientName())
                        .amount(altVO.getAmount())
                        .unit(altVO.getUnit())
                        .build();
                recipeAlternativeIngredientRepository.save(altEntity);
            }
        }

        // 후기 수 상승
        recipe.addRating(vo.getRating());

        recipe.setUpdateDate(LocalDateTime.now());

        recipeRepository.save(recipe);


        System.out.println(">>> 후기 등록 완료. ID: " + newReview.getReviewNo());
        System.out.println(">>> 레시피 후기 수 변경: " + recipe.getReviewCount());


    }

    //홈에서 레시피 후기 10개 조회
    @Test
    void getRecentReviewsTest() {


        List<Reviews> entities = reviewsRepository.findTop10ByDelDateIsNullOrderByInDateDesc();


        List<ReviewsVO> voList = new ArrayList<>();

        for (Reviews r : entities) {
            ReviewsVO vo = new ReviewsVO();


            vo.setReviewNo(r.getReviewNo());
            vo.setTitle(r.getTitle());
//            vo.setContent(r.getContent());
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



        for (ReviewsVO vo : voList) {
            System.out.println("후기 번호: " + vo.getReviewNo());
            System.out.println("제목: " + vo.getTitle());
//            System.out.println("내용: " + vo.getContent());
            System.out.println("사진 URL: " + vo.getReviewImage());
            System.out.println("별점: " + vo.getRating());
            System.out.println("맵기: " + vo.getSpicyLevel());
            System.out.println("작성일: " + vo.getInDate());

            System.out.println("작성자 닉네임: " + vo.getWriterNickname());
            System.out.println("작성자 프로필: " + vo.getWriterProfileImage());
            System.out.println("------------------------------");
        }

    }

    //추가! 마이페이지 에서 본인 번호로 리뷰 가져오기
    @Test
    void getMypageReviewsTest() {

        Integer memberNo = 5;

        List<ReviewsListVO> entities = reviewsRepository.findReviewsListVOByRecipe_RecipeNoAndDelDateIsNullOrderByInDateDesc(memberNo);

        List<ReviewsListVO> reviewsListVOList = new ArrayList<>();

        List<ReviewsListVO> voList = reviewsListVOList.stream()
                .map(r -> ReviewsListVO.builder()
                        .reviewNo(r.getReviewNo())
                        .title(r.getTitle())
                        // .content(r.getContent()) // 주석된 부분은 필요시 해제
                        .imageUrl(r.getImageUrl())
                        .rating(r.getRating())
                        .inDate(r.getInDate())
                        .build())
                .collect(Collectors.toList());

        reviewsListVOList.add((ReviewsListVO) voList);

        System.out.println(reviewsListVOList);

        }

    // 특정 레시피 후기 찾기
    @Test
    void getRecipeReviewsTest() {


        Recipe recipe = recipeRepository.findById(1).get();

         List<Reviews> ent = reviewsRepository.findTop10ByRecipeAndDelDateIsNullOrderByInDateDesc(recipe);

        List<ReviewsVO> voList = new ArrayList<>();

        for (Reviews r : ent) {
            ReviewsVO vo = new ReviewsVO();


            vo.setReviewNo(r.getReviewNo());
            vo.setTitle(r.getTitle());
//            vo.setContent(r.getContent());
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



        for (ReviewsVO vo : voList) {
            System.out.println("후기 번호: " + vo.getReviewNo());
            System.out.println("제목: " + vo.getTitle());
//            System.out.println("내용: " + vo.getContent());
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
