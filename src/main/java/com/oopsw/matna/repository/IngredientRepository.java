package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Ingredient;

import com.oopsw.matna.vo.IngredientVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    List<Ingredient> findByIngredientNameContainingAndDelDateIsNull(String keyword);

    boolean existsByIngredientName(String name); // 재료 중복 검사

}
