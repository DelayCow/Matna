package com.oopsw.matna.service;

import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.RecipeAlternativeIngredientRepository;
import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.repository.ReviewsRepository;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeAlternativeIngredient;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.RecipeVO;
import com.oopsw.matna.vo.ReviewsRegisterVO;
import com.oopsw.matna.vo.ReviewsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final RecipeRepository recipeRepository;
    private final ReviewsRepository reviewsRepository;
    private final MemberRepository memberRepository;
    private final RecipeAlternativeIngredientRepository recipeAlternativeIngredientRepository;
    private final ImageStorageService imageStorageService;

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
                vo.setWriterNo(r.getAuthor().getMemberNo());
                vo.setWriterNickname(r.getAuthor().getNickname());
                vo.setWriterProfileImage(r.getAuthor().getImageUrl());
            }
            List<RecipeAlternativeIngredient> alternativeIngredients = recipeAlternativeIngredientRepository.findByReview_ReviewNo(r.getReviewNo());
            List<ReviewsRegisterVO.AlternativeRegisterVO> alternativeVoList = new ArrayList<>();

            for (RecipeAlternativeIngredient a : alternativeIngredients) {
                ReviewsRegisterVO.AlternativeRegisterVO altVo = new ReviewsRegisterVO.AlternativeRegisterVO();
                altVo.setOriginalIngredientName(a.getOriginalIngredientName());
                altVo.setAlternativeIngredientName(a.getAlternativeIngredientName());
                altVo.setAmount(a.getAmount());
                altVo.setUnit(a.getUnit());
                alternativeVoList.add(altVo);
            }
            vo.setAlternatives(alternativeVoList);

            voList.add(vo);
        }
        return voList;
    }

    public ReviewsVO getReviewDetail(Integer reviewNo) {
        Reviews reviews = reviewsRepository.findByReviewNoAndDelDateIsNull(reviewNo).get();
        List<RecipeAlternativeIngredient> alternativeIngredients = recipeAlternativeIngredientRepository.findByReview_ReviewNo(reviews.getReviewNo());
        List<ReviewsRegisterVO.AlternativeRegisterVO> alternativeVoList = new ArrayList<>();

        for (RecipeAlternativeIngredient a : alternativeIngredients) {
            ReviewsRegisterVO.AlternativeRegisterVO altVo = new ReviewsRegisterVO.AlternativeRegisterVO();
            altVo.setOriginalIngredientName(a.getOriginalIngredientName());
            altVo.setAlternativeIngredientName(a.getAlternativeIngredientName());
            altVo.setAmount(a.getAmount());
            altVo.setUnit(a.getUnit());
            alternativeVoList.add(altVo);
        }

        return ReviewsVO.builder()
                .reviewNo(reviews.getReviewNo())
                .reviewImage(reviews.getImageUrl())
                .title(reviews.getTitle())
                .content(reviews.getContent())
                .rating(reviews.getRating())
                .spicyLevel(reviews.getSpicyLevel())
                .inDate(reviews.getInDate())
                .writerNo(reviews.getAuthor().getMemberNo())
                .writerNickname(reviews.getAuthor().getNickname())
                .writerProfileImage(reviews.getAuthor().getImageUrl())
                .alternatives(alternativeVoList)
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

    @Transactional
    public Integer addReview(Integer writerNo, ReviewsRegisterVO vo, MultipartFile reviewImage) throws IOException {
        Member writer = memberRepository.findById(writerNo)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. (memberNo: " + writerNo + ")"));
        Recipe recipe = recipeRepository.findById(vo.getRecipeNo())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 레시피번호입니다. (recipeNo: " + vo.getRecipeNo() + ")"));

        String reviewImageUrl = null;
        if (reviewImage == null || reviewImage.isEmpty()) {
            throw new IllegalArgumentException("리뷰 이미지는 필수입니다.");
        }
        reviewImageUrl = imageStorageService.save(reviewImage, "review");

        Reviews review = Reviews.builder()
                .author(writer)
                .recipe(recipe)
                .title(vo.getTitle())
                .content(vo.getContent())
                .rating(vo.getRating())
                .spicyLevel(vo.getSpicyLevel())
                .imageUrl(reviewImageUrl)
                .likesCount(0)
                .inDate(LocalDateTime.now())
                .build();

        Reviews newReview = reviewsRepository.save(review);

        if (vo.getAlternatives() != null) {
            for (ReviewsRegisterVO.AlternativeRegisterVO altVO : vo.getAlternatives()) {
                RecipeAlternativeIngredient altEntity = RecipeAlternativeIngredient.builder()
                        .review(review)
                        .originalIngredientName(altVO.getOriginalIngredientName())
                        .alternativeIngredientName(altVO.getAlternativeIngredientName())
                        .amount(altVO.getAmount())
                        .unit(altVO.getUnit())
                        .build();
                recipeAlternativeIngredientRepository.save(altEntity);
            }
        }

        float totalScore = recipe.getAverageRating() * recipe.getReviewCount();
        recipe.setReviewCount(recipe.getReviewCount() + 1);
        recipe.setAverageRating((totalScore + vo.getRating()) / recipe.getReviewCount() );

        recipeRepository.save(recipe);

        return newReview.getReviewNo();
    }

    @Transactional
    public Integer editReview(Integer writerNo, ReviewsRegisterVO vo, MultipartFile reviewImage) throws IOException {
        Reviews review = reviewsRepository.findById(vo.getReviewNo())
                .orElseThrow(()-> new NoSuchElementException("존재하지 않는 리뷰입니다. (reviewNo: " + vo.getReviewNo() + ")"));
        Recipe recipe = recipeRepository.findById(vo.getRecipeNo())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 레시피번호입니다. (recipeNo: " + vo.getRecipeNo() + ")"));

        if(!review.getAuthor().getMemberNo().equals(writerNo)) {
            throw new IllegalArgumentException("리뷰를 수정할 권한이 없습니다.");
        }

        String reviewImageUrl;
        String currentReviewImage = review.getImageUrl();

        if (vo.getReviewImage() != null && vo.getReviewImage().equals(currentReviewImage)) {
            reviewImageUrl = currentReviewImage;
        } else if (reviewImage != null && !reviewImage.isEmpty()) {
            if (currentReviewImage != null) {
                imageStorageService.delete(currentReviewImage);
            }
            reviewImageUrl = imageStorageService.save(reviewImage, "review");
        } else {
            throw new IllegalArgumentException("리뷰 이미지는 필수입니다.");
        }

        review.setTitle(vo.getTitle());
        review.setContent(vo.getContent());
        review.setRating(vo.getRating());
        review.setSpicyLevel(vo.getSpicyLevel());
        review.setImageUrl(reviewImageUrl);
        review.setUpdateDate(LocalDateTime.now());

        Reviews savedReview = reviewsRepository.save(review);

        float totalScore = recipe.getAverageRating() * recipe.getReviewCount();
        recipe.setAverageRating((totalScore - review.getRating() + vo.getRating()) / recipe.getReviewCount() );

        recipeRepository.save(recipe);

        return savedReview.getReviewNo();
    }

    @Transactional
    public void removeReview(Integer memberNo, Integer reviewNo) {
        Reviews review = reviewsRepository.findById(reviewNo).get();

        if(!review.getAuthor().getMemberNo().equals(memberNo)) throw new IllegalArgumentException("후기를 삭제할 권한이 없습니다.");

        if(review.getDelDate() != null) throw new NoSuchElementException("이미 삭제된 리뷰입니다");

        Recipe recipe = review.getRecipe();

        review.setDelDate(LocalDateTime.now());
        reviewsRepository.save(review);

        float totalScore = recipe.getAverageRating() * recipe.getReviewCount();
        recipe.setReviewCount(recipe.getReviewCount() - 1);

        if (recipe.getReviewCount() > 0) {
            recipe.setAverageRating((totalScore - review.getRating()) / recipe.getReviewCount());
        } else {
            recipe.setAverageRating(0.0f);
        }

        recipeRepository.save(recipe);

    }
}
