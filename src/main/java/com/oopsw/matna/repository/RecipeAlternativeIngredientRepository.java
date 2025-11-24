package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.RecipeAlternativeIngredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface RecipeAlternativeIngredientRepository extends CrudRepository<RecipeAlternativeIngredient, Integer> {

    List<RecipeAlternativeIngredient> findByReview_Recipe_RecipeNo(Integer recipeNo);
}
