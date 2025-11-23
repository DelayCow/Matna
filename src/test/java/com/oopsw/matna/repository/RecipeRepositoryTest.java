package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.RecipeListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RecipeRepositoryTest {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    RecipeStepRepository recipeStepRepository;
    @Autowired
    RecipeIngredientRepository recipeIngredientRepository;

    @Test
    void findAllByDelDateIsNullOrderByRecipeNoDesc() {
        List<Recipe> entities = recipeRepository.findAllByDelDateIsNullOrderByRecipeNoDesc();


        List<RecipeListVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeListVO vo = new RecipeListVO();


            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }

            vo.setRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
            vo.setServings(recipe.getServings());
            vo.setPrepTime(recipe.getPrepTime());
            vo.setDifficulty(recipe.getDifficulty());
            vo.setSpicyLevel(recipe.getSpicyLevel());


            voList.add(vo);
        }



        for (RecipeListVO vo : voList) {
            System.out.println(vo);
        }


    }

    @Test
    void findAllByDelDateIsNullOrderByReviewCountDesc() {

        List<Recipe> entities = recipeRepository.findAllByDelDateIsNullOrderByReviewCountDesc();

        List<RecipeListVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeListVO vo = new RecipeListVO();


            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }


            vo.setRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
             vo.setServings(recipe.getServings());
             vo.setPrepTime(recipe.getPrepTime());
             vo.setDifficulty(recipe.getDifficulty());
             vo.setSpicyLevel(recipe.getSpicyLevel());


            voList.add(vo);
        }


        for (RecipeListVO vo : voList) {
            System.out.println(vo);
        }


    }

    @Test
    void findBySpicyLevelAndDelDateIsNullOrderByRecipeNoDesc(){

        int spicyLevel = 1;
        List<Recipe> entities = recipeRepository.findBySpicyLevelAndDelDateIsNullOrderByRecipeNoDesc(spicyLevel);

        List<RecipeListVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeListVO vo = new RecipeListVO();


            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }


            vo.setRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
            vo.setServings(recipe.getServings());
            vo.setPrepTime(recipe.getPrepTime());
            vo.setDifficulty(recipe.getDifficulty());
            vo.setSpicyLevel(recipe.getSpicyLevel());


            voList.add(vo);
        }


        for (RecipeListVO vo : voList) {
            System.out.println(vo);
        }



    }

    @Test
    void RecipeInsertAllTest(){
        Member writer = memberRepository.save(Member.builder()
                .memberId("Test_Id")
                .nickname("테스트용닉네임")
                .password("1234").roll("USER").point(0).inDate(LocalDateTime.now())
                .build());

        Ingredient onion = ingredientRepository.save(Ingredient.builder()
                .ingredientName("양파").creator(writer).inDate(LocalDateTime.now()).build());

        Ingredient gogi = ingredientRepository.save(Ingredient.builder()
                .ingredientName("베이컨").creator(writer).inDate(LocalDateTime.now()).build());

        Recipe recipe = Recipe.builder()
                .author(writer)
                .title("테스트 레시피")
                .summary("테스트 레시피")
                .category("korean").servings(1).prepTime(15).difficulty("easy").spicyLevel(1)
                .imageUrl("Test.jpg").inDate(LocalDateTime.now())
                .scrapCount(0).reviewCount(0).averageRating(0.0f)
                .build();

        Recipe savedRecipe = recipeRepository.save(recipe);

        RecipeIngredient link1 = RecipeIngredient.builder()
                .recipe(savedRecipe)
                .ingredient(onion)
                .amount(1.0f).unit("개")
                .build();


        RecipeIngredient link2 = RecipeIngredient.builder()
                .recipe(savedRecipe)
                .ingredient(gogi)
                .amount(200.0f).unit("g")
                .build();

        recipeIngredientRepository.save(link1);
        recipeIngredientRepository.save(link2);

        RecipeStep step1 = RecipeStep.builder()
                .recipe(savedRecipe) // ★ 레시피 연결
                .stepOrder(1)
                .content("양파를 테스트합니다요")
                .imageUrl("step1.jpg")
                .build();

        // Step 2
        RecipeStep step2 = RecipeStep.builder()
                .recipe(savedRecipe)
                .stepOrder(2)
                .content("베이컨을 넣습니다")
                .imageUrl("step2.jpg")
                .build();

        recipeStepRepository.save(step1);
        recipeStepRepository.save(step2);


    }





}



