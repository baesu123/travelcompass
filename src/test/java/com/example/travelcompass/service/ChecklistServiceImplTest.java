package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.ChecklistCreateRequest;
import com.example.travelcompass.dto.request.ChecklistUpdateRequest;
import com.example.travelcompass.dto.response.ChecklistResponse;
import com.example.travelcompass.entity.Checklist;
import com.example.travelcompass.mapper.ChecklistMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChecklistServiceImplTest {

    private final ChecklistMapper checklistMapper = mock(ChecklistMapper.class);
    private final ChecklistServiceImpl checklistService = new ChecklistServiceImpl(checklistMapper);

    @Test
    void 회원가입시_기본_체크리스트_4개가_생성된다() {
        checklistService.createDefaults(10L);

        verify(checklistMapper, times(4)).insert(any(Checklist.class));
    }

    @Test
    void 체크리스트_목록을_조회한다() {
        when(checklistMapper.findAllByMemberId(10L)).thenReturn(List.of(
                Checklist.builder().id(1L).memberId(10L).itemName("여권").checked(true).build()
        ));

        List<ChecklistResponse> responses = checklistService.getChecklist(10L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getItemName()).isEqualTo("여권");
        assertThat(responses.get(0).isChecked()).isTrue();
    }

    @Test
    void 커스텀_항목을_추가한다() {
        ChecklistCreateRequest request = new ChecklistCreateRequest();
        request.setItemName("우산");

        checklistService.addChecklistItem(10L, request);

        verify(checklistMapper, times(1)).insert(any(Checklist.class));
    }

    @Test
    void 완료_여부를_토글한다() {
        when(checklistMapper.updateCheckedByIdAndMemberId(1L, 10L, true)).thenReturn(1);

        ChecklistUpdateRequest request = new ChecklistUpdateRequest();
        request.setChecked(true);

        checklistService.updateChecklist(10L, 1L, request);

        verify(checklistMapper, times(1)).updateCheckedByIdAndMemberId(1L, 10L, true);
    }

    @Test
    void 본인_소유가_아닌_체크리스트_수정은_예외가_발생한다() {
        when(checklistMapper.updateCheckedByIdAndMemberId(1L, 10L, true)).thenReturn(0);

        ChecklistUpdateRequest request = new ChecklistUpdateRequest();
        request.setChecked(true);

        assertThatThrownBy(() -> checklistService.updateChecklist(10L, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.CHECKLIST_NOT_FOUND);
    }

    @Test
    void 본인_소유가_아닌_체크리스트_삭제는_예외가_발생한다() {
        when(checklistMapper.deleteByIdAndMemberId(1L, 10L)).thenReturn(0);

        assertThatThrownBy(() -> checklistService.deleteChecklistItem(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.CHECKLIST_NOT_FOUND);
    }

}
