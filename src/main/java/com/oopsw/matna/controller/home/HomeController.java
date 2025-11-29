package com.oopsw.matna.controller.home;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/recipe")
    public String recipe() {
        return "recipeList";
    }
//
//    @GetMapping("/groupBuy")
//    public String groupBuy() {
//        return "groupBuyList";
//    }
//
//    @GetMapping("/mypage")
//    public String mypage() {
//        return "mypage";
//    }
}
