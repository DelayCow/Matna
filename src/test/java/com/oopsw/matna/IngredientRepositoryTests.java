package com.oopsw.matna;

import com.oopsw.matna.repository.IngredientRepository;
import com.oopsw.matna.repository.entity.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class IngredientRepositoryTests {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Transactional //lazy loading 해결하기 위해 넣음.
    @Test
    public void findAll() {
        System.out.println(ingredientRepository.findAll());
    }

    @Transactional
    @Test
    public void findByIngredientNameContaining() {
        System.out.println(ingredientRepository.findByIngredientNameContaining("고기"));
    }

    @Transactional
    @Test
    @Commit
    public void updateDelDate() {
        Ingredient ingredient = ingredientRepository.findById(50)
                .orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));

        ingredient.setDelDate(LocalDateTime.now());

        // @Transactional 안에서 수정했으므로 save() 호출 없이 DB 반영
    }

    @Transactional
    @Test
    public void findAllByApproveDateIsNull() {
        System.out.println(ingredientRepository.findAllByApproveDateIsNull());
    }

    @Transactional
    @Test
    @Commit
    public void approve() {
        Ingredient ingredient = ingredientRepository.findById(46)
                .orElseThrow(() -> new RuntimeException("재료가 존재하지 않습니다."));

        ingredient.setApproveDate(LocalDateTime.now());
    }

}
