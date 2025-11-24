package com.oopsw.matna.repository;


import com.oopsw.matna.repository.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
public class MemberRepositoryTests {
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void findAllByRoll() {
        System.out.println(memberRepository.findAllByRollOrderByMemberNoDesc("USER"));
    }

    @Test
    public void findByNickname() {
        System.out.println(memberRepository.findByNicknameContaining("감자"));
    }

    @Test
    public void findByMemberId() {
        System.out.println(memberRepository.findByMemberIdContaining("member_1"));
    }

    @Test
    public void findAllByRollAndInDateBetween(){
        LocalDateTime start = LocalDateTime.of(2025, 11, 20, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 11, 24, 23, 59);
        System.out.println(memberRepository.findAllByRollAndInDateBetween("USER", start, end));
    }

    @Test
    @Transactional
    public void updateBanDate() {
        Member member = memberRepository.findByMemberId("member_18")
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        LocalDateTime banDate = LocalDateTime.of(2025, 11, 25, 0, 0);
        member.setBanDate(banDate);

        //@Transactional 안에서 수정했으므로 save() 없이도 DB 반영됨
    }
}
