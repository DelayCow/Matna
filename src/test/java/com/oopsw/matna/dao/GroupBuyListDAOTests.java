package com.oopsw.matna.dao;

import com.oopsw.matna.vo.GroupBuyListVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GroupBuyListDAOTests {
    @Autowired
    GroupBuyListDAO groupBuyListDAO;

    @Test
    public void getParticipantListTest() {
        List<GroupBuyListVO> participantList = groupBuyListDAO.getParticipantList(19, "ALL");

        System.out.println(participantList);
    }

    @Test
    public void getHostListTest() {
        List<GroupBuyListVO> hostList = groupBuyListDAO.getHostList(19, "ALL");

        System.out.println(hostList);
    }

    @Test
    public void getCreateGroupBuyListTest() {
        List<GroupBuyListVO> participantList = groupBuyListDAO.getCreateGroupBuyList(19);
        System.out.println(participantList);
    }
}
