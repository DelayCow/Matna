package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.Reviews;
import com.oopsw.matna.vo.ReviewsListVO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ReviewsRepository extends CrudRepository<Reviews, Integer> {

    //상세 페이지
    @EntityGraph(attributePaths = {"author"})
    List<Reviews> findByRecipeAndDelDateIsNullOrderByInDateDesc(Recipe recipe);

    // 그냥 최신순
    @EntityGraph(attributePaths = {"author"})
    List<Reviews> findTop10ByDelDateIsNullOrderByInDateDesc();

    // 특정 레시피 후기
    @EntityGraph(attributePaths = {"author"})
    List<Reviews> findTop10ByRecipeAndDelDateIsNullOrderByInDateDesc(Recipe recipe);

    // 추가 마이페이지 리뷰 목록
    @EntityGraph(attributePaths = {"author"})
    List<ReviewsListVO> findReviewsListVOByRecipe_RecipeNoAndDelDateIsNullOrderByInDateDesc(Integer recipeNo);


    List<Reviews> findByAuthor_MemberNoAndDelDateIsNull(Integer authorNo);
}
