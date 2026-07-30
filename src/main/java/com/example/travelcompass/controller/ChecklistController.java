package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.config.MemberDetails;
import com.example.travelcompass.dto.request.ChecklistCreateRequest;
import com.example.travelcompass.dto.request.ChecklistUpdateRequest;
import com.example.travelcompass.dto.response.ChecklistResponse;
import com.example.travelcompass.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 로그인한 회원의 여행 준비물 체크리스트를 조회/추가/수정/삭제하는 REST API 컨트롤러.
 * 모든 메서드가 세션에 저장된 인증 정보(@AuthenticationPrincipal)를 통해 회원 본인의 체크리스트만 다루도록 한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checklists")
public class ChecklistController {

    // 체크리스트 CRUD(생성/조회/수정/삭제) 로직을 처리하는 서비스.
    private final ChecklistService checklistService;

    /**
     * 현재 로그인한 회원의 체크리스트 전체 목록을 조회한다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @return 체크리스트 항목 목록을 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping
    public ApiResponse<List<ChecklistResponse>> getChecklist(@AuthenticationPrincipal MemberDetails memberDetails) {
        // @AuthenticationPrincipal로 주입된 회원 ID를 이용해 본인의 체크리스트만 조회하도록 서비스에 위임한다.
        return ApiResponse.success(checklistService.getChecklist(memberDetails.getMemberId()));
    }

    /**
     * 체크리스트에 새로운 준비물 항목을 추가한다.
     *
     * @param memberDetails 로그인한 회원 정보 (항목의 소유자)
     * @param request       추가할 체크리스트 항목 정보를 담은 요청 DTO
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @PostMapping
    public ApiResponse<Void> addChecklistItem(@AuthenticationPrincipal MemberDetails memberDetails,
                                               @Valid @RequestBody ChecklistCreateRequest request) {
        // @Valid + @RequestBody : JSON 요청 본문을 객체로 변환하면서 동시에 값 검증까지 수행한다.
        checklistService.addChecklistItem(memberDetails.getMemberId(), request);
        return ApiResponse.success(null);
    }

    /**
     * 체크리스트 항목의 내용(완료 여부 등)을 수정한다.
     *
     * @param memberDetails 로그인한 회원 정보 (본인 소유 항목인지 확인용)
     * @param checklistId   수정할 체크리스트 항목의 식별자(PK)
     * @param request       수정할 내용을 담은 요청 DTO
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @PatchMapping("/{checklistId}")
    public ApiResponse<Void> updateChecklist(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @PathVariable Long checklistId,
                                              @RequestBody ChecklistUpdateRequest request) {
        // PATCH는 리소스의 일부 필드만 수정할 때 사용하는 HTTP 메서드(PUT은 전체 교체용으로 관례적으로 구분).
        checklistService.updateChecklist(memberDetails.getMemberId(), checklistId, request);
        return ApiResponse.success(null);
    }

    /**
     * 체크리스트 항목을 삭제한다.
     *
     * @param memberDetails 로그인한 회원 정보 (본인 소유 항목인지 확인용)
     * @param checklistId   삭제할 체크리스트 항목의 식별자(PK)
     * @return 별도 반환 데이터 없는 성공 응답(ApiResponse&lt;Void&gt;)
     */
    @DeleteMapping("/{checklistId}")
    public ApiResponse<Void> deleteChecklistItem(@AuthenticationPrincipal MemberDetails memberDetails,
                                                  @PathVariable Long checklistId) {
        checklistService.deleteChecklistItem(memberDetails.getMemberId(), checklistId);
        return ApiResponse.success(null);
    }

}
