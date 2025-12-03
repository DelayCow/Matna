package com.oopsw.matna.service;

import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean isDuplicatedId(String memberId){
        boolean result = false;
        Member member = memberRepository.findByMemberId(memberId);
        if(member != null) result = true;
        return result;
    };

    public boolean isDuplicatedNickname(String nickname){
        return memberRepository.existsByNickname(nickname);
    }

    public boolean addMember(MemberVO memberVO){
        if(memberRepository.existsByNickname(memberVO.getNickname()) || memberRepository.findByMemberId(memberVO.getMemberId()) != null){
            return false;
        }
        return memberRepository.save(Member.builder()
                .memberId(memberVO.getMemberId())
                .password(bCryptPasswordEncoder.encode(memberVO.getPassword()))
                .nickname(memberVO.getNickname())
                .accountName(memberVO.getAccountName())
                .accountNumber(memberVO.getAccountNumber())
                .bank(memberVO.getBank())
                .address(memberVO.getAddress())
                .role("ROLE_USER")
                .point(0)
                .build()) != null;
    }
}
