package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.PeriodGroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PeriodGroupBuyRepository extends JpaRepository<PeriodGroupBuy, Integer> {
    PeriodGroupBuy findByPeriodGroupBuyNo(int periodGroupBuyNo);

    PeriodGroupBuy findByGroupBuyNo(GroupBuy groupBuy);
}
