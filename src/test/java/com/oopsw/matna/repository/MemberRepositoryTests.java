package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.MemberProfileVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void getMemberProfileTest() {
        Integer memberNo = 5;
        Member m = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        System.out.println(MemberProfileVO.builder().nickname(m.getNickname()).imageUrl(m.getImageUrl()).point(m.getPoint()).build());
    }

//    @Test
//    @Transactional
//    @Commit
//    public void encodeAllPasswords() {
//        List<Member> members = memberRepository.findAll();
//        for (Member member : members) {
//            String plainPassword = member.getPassword();
//            String encodedPassword = bCryptPasswordEncoder.encode(plainPassword);
//            member.setPassword(encodedPassword);
//
//        }
//        memberRepository.flush();
//    }

    @Test
    public void isTruePasswordTest() {
        Integer memberNo = 5;
        String password = "member_1";
        Member m = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        boolean isMatched = bCryptPasswordEncoder.matches(password, m.getPassword());
        System.out.println(isMatched);
    }

}
