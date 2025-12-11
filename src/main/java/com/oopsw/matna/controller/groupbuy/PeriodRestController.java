package com.oopsw.matna.controller.groupbuy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oopsw.matna.auth.PrincipalDetails;
import com.oopsw.matna.dto.*;
import com.oopsw.matna.repository.entity.GroupBuyParticipant;
import com.oopsw.matna.repository.entity.PeriodGroupBuy;
import com.oopsw.matna.service.PeriodGroupBuyService;
import com.oopsw.matna.vo.PeriodGroupBuyDetailVO;
import com.oopsw.matna.vo.PeriodGroupBuyHomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> addParticipantToPeriodGroupBuy(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody GroupBuyParticipantRequest request) {

        Integer currentMemberNo = principalDetails.getMemberNo();
        request.setParticipantNo(currentMemberNo);
        GroupBuyParticipant participant = periodGroupBuyService.addParticipantToPeriodGroupBuy(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "기간공구 참여가 성공적으로 완료되었습니다.");
        response.put("data", Map.of(
                "groupParticipantNo", participant.getGroupParticipantNo(),
                "groupBuyNo", participant.getGroupBuy().getGroupBuyNo(),
                "participantNo", participant.getParticipant().getMemberNo(),
                "participantDate", participant.getParticipatedDate()
        ));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/closedAndRefund/{groupBuyNo}")
    public ResponseEntity<Map<String, Object>> editGroupBuyStatusAndRefund(
            @PathVariable Integer groupBuyNo){
        Map<String, Object> response = new HashMap<>();

        try {
            periodGroupBuyService.editGroupBuyStatusAndRefund(groupBuyNo);

            response.put("success", true);
            response.put("message", groupBuyNo + "번 기간공구가 성공적으로 마감되었으며, 정산 및 환불 처리가 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", "상태 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "공동구매 마감 및 환불 처리 중 예상치 못한 서버 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PutMapping("/cancelParticipant/{groupBuyParticipantNo}")
    public ResponseEntity<Map<String, Object>> editCancelParticipantGroupBuy(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Integer groupBuyParticipantNo) {

        Integer currentMemberNo = principalDetails.getMemberNo();
        periodGroupBuyService.editCancelParticipantGroupBuy(groupBuyParticipantNo, currentMemberNo);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "공동구매 참여가 성공적으로 취소되었습니다."
        ));
    }

    @PutMapping("/cancelCreator/{groupBuyNo}")
    public ResponseEntity<Map<String, Object>> editPeriodCreatorCancelAndRefund(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Integer groupBuyNo,
            @RequestBody Map<String, String> request) {

        Integer currentMemberNo = principalDetails.getMemberNo();
        String cancelReason = request.get("cancelReason");

        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "취소 사유를 입력해주세요."));
        }

        periodGroupBuyService.editPeriodCreatorCancelAndRefund(groupBuyNo, currentMemberNo, cancelReason);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "공동구매가 취소되고 모든 참여자에게 환불이 완료되었습니다."
        ));
    }



}
