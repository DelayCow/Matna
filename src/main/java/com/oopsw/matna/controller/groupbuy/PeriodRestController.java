package com.oopsw.matna.controller.groupbuy;

import com.oopsw.matna.dto.PeriodDetailInfo;
import com.oopsw.matna.dto.PeriodDetailResponse;
import com.oopsw.matna.dto.PeriodListResponse;
import com.oopsw.matna.service.PeriodGroupBuyService;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/periodGroupBuy")
public class PeriodRestController {
    private final PeriodGroupBuyService periodGroupBuyService;

    @GetMapping("/home")
    public ResponseEntity<List<PeriodListResponse>> getPeriodGroupBuyList(
            @RequestParam(required = false) String keyword, @RequestParam(required = false) String orderBy) {
        Map<String, Object> params = new HashMap<>();
        if (keyword != null && !keyword.isEmpty()) {
            params.put("keyword", keyword);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            params.put("orderBy", orderBy);
        }

        List<PeriodGroupBuyHomeVO> voList = periodGroupBuyService.getPeriodGroupBuyHome(params);
        List<PeriodListResponse> responseList = voList.stream()
                .map(vo -> PeriodListResponse.builder()
                        .title(vo.getTitle())
                        .nickname(vo.getNickname())
                        .creatorImageUrl(vo.getCreatorImageUrl())
                        .groupBuyImageUrl(vo.getGroupBuyImageUrl())
                        .inDate(vo.getInDate())
                        .dueDate(vo.getDueDate())
                        .minPricePerPerson(vo.getMinPricePerPerson())
                        .maxPricePerPerson(vo.getMaxPricePerPerson())
                        .shareLocation(vo.getShareLocation())
                        .participants(vo.getParticipants())
                        .maxParticipants(vo.getMaxParticipants())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/detail/{periodGroupBuyNo}")
    public ResponseEntity<PeriodDetailResponse> getPeriodGroupBuyDetail(@PathVariable Integer periodGroupBuyNo) {
        try {
            Map<String, Object> serviceResultMap = periodGroupBuyService.getPeriodGroupBuyDetail(periodGroupBuyNo);
            PeriodGroupBuyDetailVO detailVO = (PeriodGroupBuyDetailVO) serviceResultMap.get("groupBuyDetail");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> participantMapList = (List<Map<String, Object>>) serviceResultMap.get("participants");
            List<PeriodDetailResponse.ParticipantInfo> participantList = participantMapList.stream()
                    .map(map -> PeriodDetailResponse.ParticipantInfo.builder()
                            .nickname((String) map.get("nickname"))
                            .profileUrl((String) map.get("profileUrl"))
                            .participatedDate((LocalDateTime) map.get("participatedDate"))
                            .build())
                    .collect(Collectors.toList());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recipeMapList = (List<Map<String, Object>>) serviceResultMap.get("recipes");
            List<PeriodDetailResponse.RecipeInfo> recipeList = recipeMapList.stream()
                    .map(map -> PeriodDetailResponse.RecipeInfo.builder()
                            .title((String) map.get("title"))
                            .imageUrl((String) map.get("imageUrl"))
                            .authorNickname((String) map.get("authorNickname"))
                            .inDate((LocalDateTime) map.get("inDate"))
                            .build())
                    .collect(Collectors.toList());

            PeriodDetailResponse response = PeriodDetailResponse.builder()
                    .groupBuyDetail(detailVO)
                    .participants(participantList)
                    .recipes(recipeList)
                    .build();

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("Error fetching group buy detail: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
