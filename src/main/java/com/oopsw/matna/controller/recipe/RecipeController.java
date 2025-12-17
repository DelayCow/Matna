package com.oopsw.matna.controller.recipe;

import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.service.RecipeService;
import com.oopsw.matna.vo.RecipeDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    
    @GetMapping("/add")
    public String addRecipe(){
        return "recipeRegister";
    }

    @GetMapping("/detail/{recipeNo}")
    public String recipeDetail(@PathVariable("recipeNo") String recipeNo){
        return "recipeDetail";
    }

    @GetMapping("/edit/{recipeNo}")
    public String editRecipe(@PathVariable("recipeNo") String recipeNo){
        return "editRecipe";
    }
}
