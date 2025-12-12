package com.oopsw.matna.controller.review;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @GetMapping("/add/{recipeNo}")
    public String addReview(@PathVariable Integer recipeNo) {
        return "reviewRegister";
    }

    @GetMapping("/recipe/{recipeNo}")
    public String getReviewList(@PathVariable Integer recipeNo) {
        return "reviewList";
    }

    @GetMapping("/detail/{reviewNo}")
    public String getReviewDetail(@PathVariable Integer reviewNo){
        return "reviewDetail";
    }

    @GetMapping("/edit/{recipeNo}")
    public String editReview(@PathVariable Integer recipeNo) {
        return "editReview";
    }
}
