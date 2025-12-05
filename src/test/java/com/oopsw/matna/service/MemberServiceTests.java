package com.oopsw.matna.service;

import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.MemberVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberServiceTests {
    @Autowired
    private MemberService memberService;

    @Test
    public void isDuplicatedIdTest(){
        System.out.println(memberService.isDuplicatedId("member_18"));//true
        System.out.println(memberService.isDuplicatedId("member_20"));//false
    }
    @Test
    public void isDuplicatedNicknameTest(){
        System.out.println(memberService.isDuplicatedNickname("마리오"));//true
        System.out.println(memberService.isDuplicatedNickname("김찰스"));//false
    }

    @Test
    public void addMemberTest(){
        MemberVO memberVO = MemberVO.builder()
                .memberId("test10")
                .password("test11")
                .nickname("테스트10")
                .accountName("테스트")
                .accountNumber("302-1234-5678-01")
                .bank("신한은행")
                .address("서울시 금천구 독산동")
                .build();
        System.out.println(memberService.addMember(memberVO));
    }
}
