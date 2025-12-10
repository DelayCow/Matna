package com.oopsw.matna.controller.review;

import com.oopsw.matna.service.RecipeService;
import com.oopsw.matna.vo.RecipeDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final RecipeService recipeService;

    @GetMapping("/add")
    public String addReview(@RequestParam Integer recipeNo, Model model) {
        RecipeDetailVO recipeDetailVO = recipeService.getRecipeDetail(recipeNo);
        model.addAttribute("recipeDetail", recipeDetailVO);
        return "reviewRegister";
    }
}
