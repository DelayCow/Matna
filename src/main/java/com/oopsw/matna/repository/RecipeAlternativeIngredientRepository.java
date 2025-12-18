package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.RecipeAlternativeIngredient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface RecipeAlternativeIngredientRepository extends CrudRepository<RecipeAlternativeIngredient, Integer> {

    List<RecipeAlternativeIngredient> findByReview_Recipe_RecipeNoAndReview_DelDateIsNull(Integer recipeNo);
    List<RecipeAlternativeIngredient> findByReview_ReviewNo(Integer reviewNo);
}
