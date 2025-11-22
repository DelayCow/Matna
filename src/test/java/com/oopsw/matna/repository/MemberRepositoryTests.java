package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import com.oopsw.matna.vo.MemberProfileVO;
import com.oopsw.matna.vo.RecipeListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void getMemberProfileTest() {
        Integer memberNo = 5;
        Member m = memberRepository.findById(memberNo)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        System.out.println(MemberProfileVO.builder().nickname(m.getNickname()).imageUrl(m.getImageUrl()).point(m.getPoint()).build());
    }

}
