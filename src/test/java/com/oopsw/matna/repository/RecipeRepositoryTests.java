package com.oopsw.matna.repository;


import com.oopsw.matna.repository.entity.*;
import com.oopsw.matna.vo.RecipeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class RecipeRepositoryTests {

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


        List<RecipeVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeVO vo = new RecipeVO();


            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }

            vo.setAverageRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
            vo.setServings(recipe.getServings());
            vo.setPrepTime(recipe.getPrepTime());
            vo.setDifficulty(recipe.getDifficulty());
            vo.setSpicyLevel(recipe.getSpicyLevel());


            voList.add(vo);
        }



        for (RecipeVO vo : voList) {
            System.out.println(vo);
        }


    }

    @Test
    void findAllByDelDateIsNullOrderByReviewCountDesc() {

        List<Recipe> entities = recipeRepository.findAllByDelDateIsNullOrderByReviewCountDesc();

        List<RecipeVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeVO vo = new RecipeVO();


            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }


            vo.setAverageRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
             vo.setServings(recipe.getServings());
             vo.setPrepTime(recipe.getPrepTime());
             vo.setDifficulty(recipe.getDifficulty());
             vo.setSpicyLevel(recipe.getSpicyLevel());


            voList.add(vo);
        }


        for (RecipeVO vo : voList) {
            System.out.println(vo);
        }


    }

    @Test
    void findBySpicyLevelAndDelDateIsNullOrderByRecipeNoDesc(){

        int spicyLevel = 1;
        List<Recipe> entities = recipeRepository.findBySpicyLevelAndDelDateIsNullOrderByRecipeNoDesc(spicyLevel);

        List<RecipeVO> voList = new ArrayList<>();

        for (Recipe recipe : entities) {
            RecipeVO vo = new RecipeVO();


            vo.setRecipeNo(recipe.getRecipeNo());
            vo.setTitle(recipe.getTitle());
            vo.setThumbnailUrl(recipe.getImageUrl());


            if (recipe.getAuthor() != null) {
                vo.setWriterNickname(recipe.getAuthor().getNickname());
                vo.setWriterProfile(recipe.getAuthor().getImageUrl());
            }


            vo.setAverageRating(recipe.getAverageRating());
            vo.setReviewCount(recipe.getReviewCount());
            vo.setServings(recipe.getServings());
            vo.setPrepTime(recipe.getPrepTime());
            vo.setDifficulty(recipe.getDifficulty());
            vo.setSpicyLevel(recipe.getSpicyLevel());


            voList.add(vo);
        }


        for (RecipeVO vo : voList) {
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

    @Test
    public void getMyPageRecipeListTest(){
        Integer memberNo = 5;
        List<Recipe> recipes = recipeRepository.findByAuthor_MemberNoAndDelDateIsNull(memberNo);
        List<RecipeVO> recipeList = recipes.stream()
                .map(recipe -> RecipeVO.builder()
                        .recipeNo(recipe.getRecipeNo())
                        .title(recipe.getTitle())
                        .averageRating(recipe.getAverageRating())
                        .reviewCount(recipe.getReviewCount())
                        .thumbnailUrl(recipe.getImageUrl())
                        .difficulty(recipe.getDifficulty())
                        .prepTime(recipe.getPrepTime())
                        .servings(recipe.getServings())
                        .spicyLevel(recipe.getSpicyLevel())
                        .build()).collect(Collectors.toList());
        System.out.println(recipeList);
    }

    @Test
    public void removeRecipe(){
        Integer recipeNo = 12;
        Recipe recipe = recipeRepository.findById(recipeNo).get();
        recipe.setDelDate(LocalDateTime.now());
        recipeRepository.save(recipe);
    }


    @Test
    public void AllSearchWithFiltersTest(){
        List<Recipe> list1 = recipeRepository.findWithFilters(
                null, null, Sort.by(Sort.Direction.DESC, "recipeNo"));

        // 2. [상황 2] "후기순" 선택 + "맵기 1단계" 필터
        List<Recipe> list2 = recipeRepository.findWithFilters(
                1, null, Sort.by(Sort.Direction.DESC, "reviewCount"));

        // 3. [상황 3] "김치" 검색 + "맵기 2단계" + "최신순" 유지
        List<Recipe> list3 = recipeRepository.findWithFilters(
                2, "김치", Sort.by(Sort.Direction.DESC, "recipeNo"));
        //최신순 이면서 맵기 4단계
        List<Recipe> list4 = recipeRepository.findWithFilters(
                3, null, Sort.by(Sort.Direction.DESC, "recipeNo"));

        // 결과 확인
        System.out.println("--- 상황 1 결과 최신순--");
        for (Recipe r : list1) {
            System.out.println("제목: " + r.getTitle() + " / 맵기: " + r.getSpicyLevel() + " / 후기수: " + r.getReviewCount() + "평점" + r.getAverageRating() + "인분" + r.getServings() + "조리시간" + r.getPrepTime() + "난이도" + r.getDifficulty() + "썸네일" + r.getImageUrl() + "아 근데 이러면 vo 만든 보람이" + r.getInDate());
        }

        System.out.println("--- 상황 2 결과 (후기순 + 맵기1) ---");
        for (Recipe r : list2) {
            System.out.println("제목: " + r.getTitle() + " / 맵기: " + r.getSpicyLevel() + " / 후기수: " + r.getReviewCount());
        }

        System.out.println("--- 상황 3 결과 검색 + 맵기) ---");
        for (Recipe r : list3) {
            System.out.println("제목: " + r.getTitle() + " / 맵기: " + r.getSpicyLevel() + " / 후기수: " + r.getReviewCount());
        }

        System.out.println("--- 상황 2 결과 (최신순 + 맵기4) ---");
        for (Recipe r : list4) {
            System.out.println("제목: " + r.getTitle() + " / 맵기: " + r.getSpicyLevel() + " / 후기수: " + r.getReviewCount());
        }
    }





}



