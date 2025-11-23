package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.repository.entity.PeriodGroupBuy;
import com.oopsw.matna.repository.entity.QuantityGroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuantityGroupBuyRepository extends JpaRepository<QuantityGroupBuy,Integer> {
    QuantityGroupBuy findByGroupBuyNo(GroupBuy groupBuy);
}
