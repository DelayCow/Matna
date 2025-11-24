package com.oopsw.matna.Repository;

import com.oopsw.matna.repository.GroupBuyListDAO;
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
        List<GroupBuyListVO> participantList = groupBuyListDAO.getParticipantList(19);
        System.out.println(participantList);
    }

    @Test
    public void getCreateGroupBuyListTest() {
        List<GroupBuyListVO> participantList = groupBuyListDAO.getCreateGroupBuyList(19);
        System.out.println(participantList);
    }
}
