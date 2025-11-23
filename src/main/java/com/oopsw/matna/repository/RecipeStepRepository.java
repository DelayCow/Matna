package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import com.oopsw.matna.repository.entity.RecipeStep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepRepository extends CrudRepository<RecipeStep, Integer> {

    List<RecipeStep> findByRecipeOrderByStepOrderAsc(Recipe recipe);
}
