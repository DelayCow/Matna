package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.vo.RecipeListVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MypageController {
    @GetMapping("/api/{memberNo}/recipes")
    public String mypage(@RequestParam Integer memberNo, Model model){
        model.addAttribute("memberNo",memberNo);
        return "mypage";
    }
}
