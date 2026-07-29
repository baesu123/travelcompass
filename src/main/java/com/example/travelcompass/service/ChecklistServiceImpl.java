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

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private static final List<String> DEFAULT_ITEMS = List.of("여권", "환전", "유심", "여행자보험");

    private final ChecklistMapper checklistMapper;

    @Override
    public List<ChecklistResponse> getChecklist(Long memberId) {
        return checklistMapper.findAllByMemberId(memberId).stream()
                .map(checklist -> ChecklistResponse.builder()
                        .id(checklist.getId())
                        .itemName(checklist.getItemName())
                        .checked(checklist.isChecked())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addChecklistItem(Long memberId, ChecklistCreateRequest request) {
        checklistMapper.insert(Checklist.builder()
                .memberId(memberId)
                .itemName(request.getItemName())
                .checked(false)
                .build());
    }

    @Override
    @Transactional
    public void updateChecklist(Long memberId, Long checklistId, ChecklistUpdateRequest request) {
        requireAffected(checklistMapper.updateCheckedByIdAndMemberId(checklistId, memberId, request.isChecked()),
                ErrorCode.CHECKLIST_NOT_FOUND);
    }

    @Override
    @Transactional
    public void deleteChecklistItem(Long memberId, Long checklistId) {
        requireAffected(checklistMapper.deleteByIdAndMemberId(checklistId, memberId), ErrorCode.CHECKLIST_NOT_FOUND);
    }

    @Override
    @Transactional
    public void createDefaults(Long memberId) {
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
