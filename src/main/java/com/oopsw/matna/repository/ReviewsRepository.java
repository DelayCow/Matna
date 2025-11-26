package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.Reviews;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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


    List<Reviews> findByAuthor_MemberNoAndDelDateIsNull(Integer authorNo);
}
