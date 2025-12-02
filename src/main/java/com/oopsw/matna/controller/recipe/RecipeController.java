package com.oopsw.matna.controller.recipe;

import com.oopsw.matna.service.RecipeService;
import com.oopsw.matna.vo.RecipeDetailVO;
import lombok.RequiredArgsConstructor;
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
    public String recipeDetail(@PathVariable("recipeNo") String recipeNo, Model model){
        Integer memberNo = 5;
        Integer recipeNoInt = Integer.parseInt(recipeNo);
        RecipeDetailVO recipeDetailVO = recipeService.getRecipeDetail(recipeNoInt);
        model.addAttribute("recipeDetail", recipeDetailVO);
        model.addAttribute("currentMemberNo", memberNo);
        return "recipeDetail";
    }

    @GetMapping("/edit/{recipeNo}")
    public String editRecipe(@PathVariable("recipeNo") String recipeNo, Model model){
        //멤버번호랑 작성자 번호 같은지 확인필요
        Integer memberNo = 5;
        Integer recipeNoInt = Integer.parseInt(recipeNo);
        RecipeDetailVO recipeDetailVO = recipeService.getRecipeDetail(recipeNoInt);
        if(recipeDetailVO.getWriterNo().equals(memberNo)){
            model.addAttribute("recipeDetail", recipeDetailVO);
            model.addAttribute("currentMemberNo", memberNo);
            return "editRecipe";
        }
        return "redirect:/recipe/detail/"+recipeNoInt;
    }
}
