package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    @EntityGraph(attributePaths = {"author"})
    List<Recipe> findAllByDelDateIsNullOrderByRecipeNoDesc();

    @EntityGraph(attributePaths = {"author"})
    List<Recipe> findAllByDelDateIsNullOrderByReviewCountDesc();

    @EntityGraph(attributePaths = {"author"})
    List<Recipe> findBySpicyLevelAndDelDateIsNullOrderByRecipeNoDesc(Integer spicyLevel);

    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT r FROM Recipe r " +
            "WHERE r.delDate IS NULL " +
            "AND (:spicyLevel IS NULL OR r.spicyLevel = :spicyLevel) " +
            "AND (:keyword IS NULL OR r.title LIKE %:keyword%)")
    List<Recipe> findWithFilters(
            @Param("spicyLevel") Integer spicyLevel,
            @Param("keyword") String keyword,
            Sort sort
    );

    List<Recipe> findByAuthor_MemberNoAndDelDateIsNull(Integer authorNo);

}
