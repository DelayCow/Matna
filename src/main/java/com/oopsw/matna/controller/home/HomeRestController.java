package com.oopsw.matna.controller.home;

import com.oopsw.matna.controller.recipe.RecipeRequest;
import com.oopsw.matna.service.MemberService;
import com.oopsw.matna.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class HomeRestController {
    private final MemberService memberService;

    @GetMapping("/check/id")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestParam String memberId) {
        Boolean isDuplicated = memberService.isDuplicatedId(memberId);
        return ResponseEntity.ok().body(Map.of("result", isDuplicated));
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        Boolean isDuplicated = memberService.isDuplicatedNickname(nickname);
        return ResponseEntity.ok().body(Map.of("result", isDuplicated));
    }
}
