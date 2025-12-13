package com.oopsw.matna.controller.groupbuy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.dto.*;
import com.oopsw.matna.repository.entity.Ingredient;
import com.oopsw.matna.repository.entity.PeriodGroupBuy;
import com.oopsw.matna.service.PeriodGroupBuyService;
import com.oopsw.matna.vo.IngredientVO;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import com.oopsw.matna.vo.PeroidGroupBuyCreateVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final ObjectMapper objectMapper;

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
                        .periodGroupBuyNo(vo.getPeriodGroupBuyNo())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/detail/{periodGroupBuyNo}")
    public ResponseEntity<PeriodDetailResponse> getPeriodGroupBuyDetail(@PathVariable Integer periodGroupBuyNo) {
        try {
            Map<String, Object> serviceResultMap = periodGroupBuyService.getPeriodGroupBuyDetail(periodGroupBuyNo);
            PeriodGroupBuyDetailVO detailVO = (PeriodGroupBuyDetailVO) serviceResultMap.get("groupBuyDetail");

//            @SuppressWarnings("unchecked")
            List<Map<String, Object>> participantMapList = (List<Map<String, Object>>) serviceResultMap.get("participants");
            List<PeriodDetailResponse.ParticipantInfo> participantList = participantMapList.stream()
                    .map(map -> PeriodDetailResponse.ParticipantInfo.builder()
                            .nickname((String) map.get("nickname"))
                            .profileUrl((String) map.get("profileUrl"))
                            .participatedDate((LocalDateTime) map.get("participatedDate"))
                            .build())
                    .collect(Collectors.toList());

//            @SuppressWarnings("unchecked")
            List<Map<String, Object>> recipeMapList = (List<Map<String, Object>>) serviceResultMap.get("recipes");
            List<PeriodDetailResponse.RecipeInfo> recipeList = recipeMapList.stream()
                    .map(map -> PeriodDetailResponse.RecipeInfo.builder()
                            .recipeNo((Integer) map.get("recipeNo"))
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

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> addPeriodGroupBuy(
            @RequestPart("periodRegisterRequest") String registerRequestJson,
            @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile) {
        Map<String, Object> response = new HashMap<>();
        try {
            // JSON 문자열을 Request 객체로 변환
            PeriodRegisterRequest request = objectMapper.readValue(
                    registerRequestJson,
                    PeriodRegisterRequest.class
            );

            // Service 호출 (이미지 파일과 함께 전달)
            PeriodGroupBuy periodGroupBuy = periodGroupBuyService.addPeriodGroupBuy(request, thumbnailFile);

            response.put("success", true);
            response.put("message", "공동구매가 성공적으로 등록되었습니다.");
            response.put("data", Map.of(
                    "periodGroupBuyNo", periodGroupBuy.getPeriodGroupBuyNo(),
                    "groupBuyNo", periodGroupBuy.getGroupBuy().getGroupBuyNo(),
                    "title", periodGroupBuy.getGroupBuy().getTitle(),
                    "imageUrl", periodGroupBuy.getGroupBuy().getImageUrl()
            ));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "공동구매 등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
