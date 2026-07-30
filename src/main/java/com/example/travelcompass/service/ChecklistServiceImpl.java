package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.ChecklistCreateRequest;
import com.example.travelcompass.dto.request.ChecklistUpdateRequest;
import com.example.travelcompass.dto.response.ChecklistResponse;
import com.example.travelcompass.entity.Checklist;
import com.example.travelcompass.mapper.ChecklistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ChecklistService의 구현체. MyBatis Mapper(ChecklistMapper)를 통해 DB와 직접 통신하며,
 * 엔티티(Checklist)와 화면에 내려줄 DTO(ChecklistResponse) 간의 변환도 이 클래스에서 담당한다.
 */
@Service // 이 클래스를 스프링이 관리하는 빈(Bean)으로 등록해서, 다른 곳에서 의존성 주입으로 사용할 수 있게 함
@RequiredArgsConstructor // final 필드(checklistMapper)를 매개변수로 받는 생성자를 롬복이 자동 생성 (생성자 주입)
public class ChecklistServiceImpl implements ChecklistService {

    // 회원가입 시 자동으로 채워줄 기본 준비물 목록. 값이 바뀔 일이 없으므로 static final 상수로 선언.
    private static final List<String> DEFAULT_ITEMS = List.of("여권", "환전", "유심", "여행자보험");

    private final ChecklistMapper checklistMapper;

    /**
     * 회원의 체크리스트 목록을 조회하고, DB 엔티티(Checklist)를 화면 응답용 DTO(ChecklistResponse)로 변환한다.
     * @param memberId 조회할 회원의 ID
     * @return 체크리스트 항목 목록
     */
    @Override
    public List<ChecklistResponse> getChecklist(Long memberId) {
        // Mapper가 돌려준 Entity 리스트를 stream으로 순회하며 응답 DTO로 하나씩 변환(매핑)한다.
        return checklistMapper.findAllByMemberId(memberId).stream()
                .map(checklist -> ChecklistResponse.builder()
                        .id(checklist.getId())
                        .itemName(checklist.getItemName())
                        .checked(checklist.isChecked())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 새 체크리스트 항목을 추가한다.
     * @param memberId 항목을 추가할 회원의 ID
     * @param request 추가할 항목명이 담긴 요청 DTO
     */
    @Override
    @Transactional // DB에 쓰기(insert)가 일어나므로 트랜잭션 처리. 실패 시 자동 롤백된다.
    public void addChecklistItem(Long memberId, ChecklistCreateRequest request) {
        checklistMapper.insert(Checklist.builder()
                .memberId(memberId)
                .itemName(request.getItemName())
                .checked(false) // 새로 추가하는 항목은 항상 미완료(체크 안됨) 상태로 시작
                .build());
    }

    /**
     * 체크리스트 항목의 체크 여부를 수정한다. id와 memberId를 함께 조건으로 걸어
     * 본인 소유 항목만 수정되도록 Mapper 쿼리 단계에서부터 보호한다.
     * @param memberId 수정을 요청한 회원의 ID
     * @param checklistId 수정할 체크리스트 항목의 ID
     * @param request 변경할 체크 여부가 담긴 요청 DTO
     */
    @Override
    @Transactional
    public void updateChecklist(Long memberId, Long checklistId, ChecklistUpdateRequest request) {
        // UPDATE 쿼리의 영향받은 행(row) 수를 검사해서, 0건이면 존재하지 않거나 남의 소유임을 감지한다.
        requireAffected(checklistMapper.updateCheckedByIdAndMemberId(checklistId, memberId, request.isChecked()),
                ErrorCode.CHECKLIST_NOT_FOUND);
    }

    /**
     * 체크리스트 항목을 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID
     * @param checklistId 삭제할 체크리스트 항목의 ID
     */
    @Override
    @Transactional
    public void deleteChecklistItem(Long memberId, Long checklistId) {
        requireAffected(checklistMapper.deleteByIdAndMemberId(checklistId, memberId), ErrorCode.CHECKLIST_NOT_FOUND);
    }

    /**
     * 회원가입 직후 호출되어, 정해진 기본 준비물 항목들을 회원의 체크리스트에 한 번에 만들어준다.
     * @param memberId 기본 항목을 생성해줄 회원의 ID
     */
    @Override
    @Transactional // 여러 건의 insert가 발생하므로 하나의 트랜잭션으로 묶어 원자성을 보장
    public void createDefaults(Long memberId) {
        // DEFAULT_ITEMS 목록을 순회하며 항목마다 별도의 insert 쿼리를 실행한다.
        for (String itemName : DEFAULT_ITEMS) {
            checklistMapper.insert(Checklist.builder()
                    .memberId(memberId)
                    .itemName(itemName)
                    .checked(false)
                    .build());
        }
    }

    /**
     * 소유권 검증(id + memberId)이 걸린 update/delete 쿼리는 영향받은 행이 0건이면
     * "존재하지 않음"과 "내 소유가 아님"을 구분하지 않고 동일하게 NOT_FOUND로 응답한다.
     * (다른 회원의 데이터 존재 여부를 노출하지 않기 위한 의도적인 설계)
     */
    private void requireAffected(int affectedRows, ErrorCode errorCode) {
        if (affectedRows == 0) {
            throw new BusinessException(errorCode);
        }
    }

}
