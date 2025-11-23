package com.oopsw.matna.repository;

import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.vo.GroupBuyParticipantVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class GroupBuyParticipantRepositoryTest {
    @Autowired
    private GroupBuyParticipantRepository groupBuyParticipantRepository;

    @Test
    public void addSharedDataTest(){
        //response에서는 string으로 받고 controller에서 넘겨줄때 타입 바꿔서 넘겨주기
        String dateStringWithTime = "2025-11-11 15:30:00";//브라우저에서 넘어올때 초도 0으로 채워서 보내기
        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime receiveDate = LocalDateTime.parse(dateStringWithTime, formatterWithTime);
        GroupBuyParticipantVO sharedData = GroupBuyParticipantVO.builder()
                .groupBuyNo(11)
                .participantNo(15)
                .receiveDate(receiveDate)
                .build();
        GroupBuyParticipant participant = groupBuyParticipantRepository.findByGroupBuy_GroupBuyNoAndParticipant_MemberNo(sharedData.getGroupBuyNo(), sharedData.getParticipantNo());
        participant.setReceiveDate(sharedData.getReceiveDate());
        groupBuyParticipantRepository.save(participant);
    }
}
