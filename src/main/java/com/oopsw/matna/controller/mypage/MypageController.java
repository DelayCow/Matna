package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.repository.GroupBuyRepository;
import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MypageController {
    @GetMapping({"/mypage/{memberNo}", "/mypage"})
    public String mypage(@AuthenticationPrincipal PrincipalDetails principalDetails,
                         @PathVariable(required = false) Integer memberNo,
                         Model model) {
        Integer targetMemberNo;
        if (memberNo == null) {
            targetMemberNo = principalDetails.getMemberNo();
        } else {
            targetMemberNo = memberNo;
        }
        boolean isOwner = principalDetails.getMemberNo().equals(targetMemberNo);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("memberNo", targetMemberNo);
        return "mypage";
    }

    // 공동구매 상세 페이지 매핑 두개로 나눠야 함
//    @GetMapping({"/groupBuy"})
//    public String groupBuyDetail(@RequestParam("no") int no, Model model) {
//
//        if()
//
//        return "groupBuy/detail";
//    }


}


