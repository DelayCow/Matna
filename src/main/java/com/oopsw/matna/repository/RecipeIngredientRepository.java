package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {

    @EntityGraph(attributePaths = {"ingredient"})
    List<RecipeIngredient> findByRecipe(Recipe recipe);
    List<RecipeIngredient> findByIngredient_IngredientNo(Integer ingredientNo);
}