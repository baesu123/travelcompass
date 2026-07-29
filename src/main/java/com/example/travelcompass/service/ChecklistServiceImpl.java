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
        int updated = checklistMapper.updateCheckedByIdAndMemberId(checklistId, memberId, request.isChecked());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.CHECKLIST_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteChecklistItem(Long memberId, Long checklistId) {
        int deleted = checklistMapper.deleteByIdAndMemberId(checklistId, memberId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.CHECKLIST_NOT_FOUND);
        }
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

}
