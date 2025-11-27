package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.service.MypageService;

import com.oopsw.matna.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MypageRestController {

    private final MypageService mypageService;

    @GetMapping("/mypage/{memberNo}")
    public List<RecipeVO> mypageList(@PathVariable("memberNo") int memberNo) {

        return mypageService.getMyPageRecipeList(memberNo);
    }

}
