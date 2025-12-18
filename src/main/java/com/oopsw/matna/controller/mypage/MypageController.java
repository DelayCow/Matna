package com.oopsw.matna.controller.mypage;

import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.repository.GroupBuyRepository;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.service.MypageService;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import com.oopsw.matna.vo.MemberVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MypageController {

    @Autowired
    private MypageService mypageService;

    @GetMapping({"/mypage/{memberNo}", "/mypage"})
    public String mypage(@PathVariable(required = false) Integer memberNo) {
        return "mypage";
    }


//    @GetMapping("/mypage/point/charge")
//    public String pointChargePage(@AuthenticationPrincipal PrincipalDetails principalDetails,
//                                  Model model) {
//
//        int memberNo = principalDetails.getMemberNo();
//        MemberVO member = mypageService.getMemberInfo(memberNo);
//
//        model.addAttribute("member", member);
//
//        return "mypagePoint";
//    }

    @GetMapping("/mypage/point/charge")
    public String mypagePointCharge() {

        return "mypagePoint";
    }



//    @GetMapping("/mypage/{memberNo}/myinfoEdit")
//    public String infoEdit (@PathVariable Integer memberNo, Model model) {
//
//        MemberVO member = mypageService.getMemberInfo(memberNo);
//
//        model.addAttribute("member", member);
//
//        return "myinfoEdit";
//    }

    @GetMapping("/mypage/{memberNo}/myinfoEdit")
    public String myInfoEditPage(@PathVariable Integer memberNo) {

        return "myInfoEdit";
    }


}


