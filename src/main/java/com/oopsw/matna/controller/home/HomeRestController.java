package com.oopsw.matna.controller.home;

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

    @PostMapping("/member")
    public ResponseEntity<Map<String, Boolean>> addMember(@RequestBody MemberRequset memberRequest) {
        if(memberService.addMember(MemberVO.builder()
                .memberId(memberRequest.getMemberId())
                .password(memberRequest.getPassword())
                .nickname(memberRequest.getNickname())
                .accountName(memberRequest.getAccountName())
                .accountNumber(memberRequest.getAccountNumber())
                .address(memberRequest.getAddress())
                .bank(memberRequest.getBank())
                .build())){
            return ResponseEntity.ok().body(Map.of("message", true));
        };
        return ResponseEntity.badRequest().body(Map.of("message", false));
    }
}
