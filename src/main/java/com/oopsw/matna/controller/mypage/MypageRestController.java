package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.dto.MypageResponse;
import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.RecipeListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class MypageRestController {
    private final MypageService mypageService;

    @GetMapping("/api/mypage/{memberNo}/recipes")
    public ResponseEntity<MypageResponse> getMypageRecipeList(@PathVariable Integer memberNo){
        RecipeListVO recipeListVO = (RecipeListVO) mypageService.getMypageRecipeList(memberNo);
        if(recipeListVO == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        MypageResponse response = MypageResponse.builder().build();

        return ResponseEntity.status(200).body(response);
    }
}
