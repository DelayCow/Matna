package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageController {


    private final MypageService mypageService;


    @GetMapping("/mypage")
    public String mypage() {

            return "mypage";
    }
}


