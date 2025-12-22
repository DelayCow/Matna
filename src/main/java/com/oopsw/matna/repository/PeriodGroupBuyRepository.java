package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.PeriodGroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PeriodGroupBuyRepository extends JpaRepository<PeriodGroupBuy, Integer> {
    PeriodGroupBuy findByGroupBuy(GroupBuy groupBuy);

    Integer periodGroupBuyNo(Integer periodGroupBuyNo);

    GroupBuy groupBuy(GroupBuy groupBuy);

    Optional<PeriodGroupBuy> findByGroupBuy_GroupBuyNo(Integer groupBuyNo);

}
