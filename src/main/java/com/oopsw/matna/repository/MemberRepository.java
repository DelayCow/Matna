package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



public interface MemberRepository extends JpaRepository<Member, Integer> {
    Member findByMemberNo(Integer memberNo);
    List<Member> memberNo(Integer memberNo);
}
