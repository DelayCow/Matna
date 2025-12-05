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
    public String recipeDetail(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("recipeNo") String recipeNo, Model model){
        Integer recipeNoInt = Integer.parseInt(recipeNo);
        RecipeDetailVO recipeDetailVO = recipeService.getRecipeDetail(recipeNoInt);
        model.addAttribute("recipeDetail", recipeDetailVO);
        model.addAttribute("currentMemberNo", principalDetails.getMemberNo());
        return "recipeDetail";
    }

    @GetMapping("/edit/{recipeNo}")
    public String editRecipe(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("recipeNo") String recipeNo, Model model){
        Integer memberNo = principalDetails.getMemberNo();
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
