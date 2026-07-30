package com.example.travelcompass.service;

import com.example.travelcompass.dto.request.ChecklistCreateRequest;
import com.example.travelcompass.dto.request.ChecklistUpdateRequest;
import com.example.travelcompass.dto.response.ChecklistResponse;

import java.util.List;

/**
 * 회원별 "여행 준비물 체크리스트" 기능을 정의하는 서비스 인터페이스.
 * 회원가입 시 기본 항목을 자동 생성하고, 이후 회원이 직접 항목을 추가/수정/삭제할 수 있게 한다.
 */
public interface ChecklistService {

    /**
     * 특정 회원의 체크리스트 전체 목록을 조회한다.
     * @param memberId 조회할 회원의 ID
     * @return 체크리스트 항목 목록(ChecklistResponse)
     */
    List<ChecklistResponse> getChecklist(Long memberId);

    /**
     * 회원의 체크리스트에 새 항목을 추가한다.
     * @param memberId 항목을 추가할 회원의 ID
     * @param request 추가할 항목명이 담긴 요청 DTO
     */
    void addChecklistItem(Long memberId, ChecklistCreateRequest request);

    /**
     * 체크리스트 항목의 체크 여부(완료 상태)를 수정한다.
     * @param memberId 수정을 요청한 회원의 ID(본인 소유 항목인지 검증용)
     * @param checklistId 수정할 체크리스트 항목의 ID
     * @param request 변경할 체크 여부가 담긴 요청 DTO
     */
    void updateChecklist(Long memberId, Long checklistId, ChecklistUpdateRequest request);

    /**
     * 체크리스트 항목을 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID(본인 소유 항목인지 검증용)
     * @param checklistId 삭제할 체크리스트 항목의 ID
     */
    void deleteChecklistItem(Long memberId, Long checklistId);

    /**
     * 신규 회원가입 시 기본 준비물 항목(여권, 환전 등)을 자동으로 생성한다.
     * @param memberId 기본 항목을 생성해줄 신규 회원의 ID
     */
    void createDefaults(Long memberId);

}
