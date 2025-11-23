package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Member findByMemberNo(int memberNo);
    List<Member> memberNo(Integer memberNo);
}
