package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupBuyRepository extends JpaRepository<GroupBuy,Integer> {
    GroupBuy findByGroupBuyNo(Integer groupBuyNo);
    List<GroupBuy> findByCreator_MemberNo(Integer creatorNo);
    List<GroupBuy> findByIngredient_IngredientNo(Integer ingredientNo);
}