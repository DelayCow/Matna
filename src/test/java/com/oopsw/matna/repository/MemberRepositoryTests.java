package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.MemberProfileVO;
import com.oopsw.matna.vo.MemberVO;
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

    @Test
    public void getMemberTest() {
        Integer memberNo = 5;
        Member m = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        MemberVO member = MemberVO.builder()
                .memberNo(m.getMemberNo())
                .memberId(m.getMemberId())
                .accountName(m.getAccountName())
                .nickname(m.getNickname())
                .bank(m.getBank())
                .accountNumber(m.getAccountNumber())
                .inDate(m.getInDate())
                .delDate(m.getDelDate())
                .roll(m.getRoll())
                .banDate(m.getBanDate())
                .point(m.getPoint())
                .imageUrl(m.getImageUrl())
                .address(m.getAddress())
                .build();
        System.out.println(member);
    }

    @Test
    @Transactional
    @Commit
    public void editMemberProfileTest() {
        MemberVO editMember = MemberVO.builder()
                .memberNo(5)
                .nickname("마리오")
                .password("member_1")
                .imageUrl("mario.jpg")
                .bank("농협은행")
                .accountNumber("302-1234-5678-01")
                .accountName("김찰스")
                .address("서울시 금천구 독산동 ")
                .build();

        Member m = memberRepository.findById(editMember.getMemberNo())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        m.setNickname(editMember.getNickname());
        m.setPassword(bCryptPasswordEncoder.encode(editMember.getPassword()));
        m.setImageUrl(editMember.getImageUrl());
        m.setBank(editMember.getBank());
        m.setAccountNumber(editMember.getAccountNumber());
        m.setAccountName(editMember.getAccountName());
        m.setAddress(editMember.getAddress());
    }

}
