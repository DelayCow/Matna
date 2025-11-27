package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.repository.RecipeRepository;
import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.RecipeListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MypageRestController {
    private final MypageService mypageService;

    @GetMapping("mypage")
    public List<RecipeListVO> getMypageRecipeList(){
        return mypageService.getMypageRecipeList();
    }
}
