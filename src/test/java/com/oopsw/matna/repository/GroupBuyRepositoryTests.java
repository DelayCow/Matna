package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuy;
import com.oopsw.matna.vo.GroupBuyListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class GroupBuyRepositoryTests {
    @Autowired
    GroupBuyRepository groupBuyRepository;

    @Test
    public void getMyPageGroupBuyListTest(){
        Integer memberNo = 5;
        List<GroupBuy> groupBuys = groupBuyRepository.findByCreator_MemberNo(memberNo);
        List<GroupBuyListVO> groupBuyList = groupBuys.stream().map(groupBuy -> GroupBuyListVO.builder()
                .groupBuyNo(groupBuy.getGroupBuyNo())
                .title(groupBuy.getTitle())
                .imageUrl(groupBuy.getImageUrl())
                .status(groupBuy.getStatus()).build()).collect(Collectors.toList());
        System.out.println(groupBuyList);
    }
}
