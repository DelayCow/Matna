package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.vo.RecipeListVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {

    @GetMapping("/mypage")
    public String mypage(Model model) {

//        RecipeListVO recipeListVO = mypage.get

        return "mypage";
    }
}


