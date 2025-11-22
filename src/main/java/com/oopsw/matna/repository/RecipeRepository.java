package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    @EntityGraph(attributePaths = {"author"})

    List<Recipe>findAllByDelDateIsNullOrderByRecipeNoDesc();
}
