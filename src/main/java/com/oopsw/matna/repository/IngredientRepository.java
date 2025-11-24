package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    List<Ingredient> findByIngredientNameContaining(String keyword);

    List<Ingredient> findByIngredientNameContainingAndDelDateIsNull(String keyword);

    boolean existsByIngredientName(String newIngredientName);
}
