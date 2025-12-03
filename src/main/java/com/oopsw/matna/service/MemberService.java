package com.oopsw.matna.service;

import com.oopsw.matna.repository.MemberRepository;
import com.oopsw.matna.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public boolean isDuplicatedId(String memberId){
        boolean result = false;
        Member member = memberRepository.findByMemberId(memberId);
        if(member != null) result = true;
        return result;
    };

    public boolean isDuplicatedNickname(String nickname){
        return memberRepository.existsByNickname(nickname);
    }
}
