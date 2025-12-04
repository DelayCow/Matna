package com.oopsw.matna.service;

import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.ReviewsRepository;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.RecipeVO;
import com.oopsw.matna.vo.ReviewsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final RecipeRepository recipeRepository;
    private final ReviewsRepository reviewsRepository;

    public List<ReviewsVO> getRecipeReviews(Integer recipeNo) {
        Recipe recipe = recipeRepository.findById(recipeNo).get();
        List<Reviews> ent = reviewsRepository.findTop10ByRecipeAndDelDateIsNullOrderByInDateDesc(recipe);

        List<ReviewsVO> voList = new ArrayList<>();

        for (Reviews r : ent) {
            ReviewsVO vo = new ReviewsVO();
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
        return voList;
    }

    public ReviewsVO getReviewDetail(Integer reviewNo) {
        Reviews reviews = reviewsRepository.findByReviewNoAndDelDateIsNull(reviewNo).get();

        return ReviewsVO.builder()
                .reviewNo(reviews.getReviewNo())
                .reviewImage(reviews.getImageUrl())
                .title(reviews.getTitle())
                .content(reviews.getContent())
                .rating(reviews.getRating())
                .spicyLevel(reviews.getSpicyLevel())
                .inDate(reviews.getInDate())
                .writerNickname(reviews.getAuthor().getNickname())
                .writerProfileImage(reviews.getAuthor().getImageUrl())
                .build();
    }

    public List<ReviewsVO> getRecentReviews(){
        List<Reviews> ent = reviewsRepository.findTop10ByDelDateIsNullOrderByInDateDesc();
        List<ReviewsVO> voList = new ArrayList<>();

        for (Reviews r : ent) {
            ReviewsVO vo = new ReviewsVO();
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
        return voList;

    }
}
