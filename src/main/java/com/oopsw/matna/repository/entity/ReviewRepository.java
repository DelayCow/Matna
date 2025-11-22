package com.oopsw.matna.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Reviews, Integer> {
    List<Reviews> findByAuthor_MemberNoAndDelDateIsNull(Integer authorNo);
}
