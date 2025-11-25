package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    List<Ingredient> findAll();

    List<Ingredient> findByIngredientNameContaining(String keyword);
    List<Ingredient> findByIngredientNo(Integer ingredientNo);
    List<Ingredient> findAllByApproveDateIsNull();


    boolean existsByIngredientName(String name); // 재료 중복 검사

    List<Ingredient> findByIngredientNameContainingAndDelDateIsNull(String keyword);

}
