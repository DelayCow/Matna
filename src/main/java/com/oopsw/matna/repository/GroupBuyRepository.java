package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuy, Integer> {
    GroupBuy findByGroupBuyNo(Integer groupBuyNo);

}