package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    List<Ingredient> findByIngredientNameContaining(String keyword);
}
