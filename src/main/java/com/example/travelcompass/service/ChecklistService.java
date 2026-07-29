package com.example.travelcompass.service;

import com.example.travelcompass.dto.request.ChecklistUpdateRequest;
import com.example.travelcompass.dto.response.ChecklistResponse;

import java.util.List;

public interface ChecklistService {

    List<ChecklistResponse> getChecklist(Long memberId);

    void updateChecklist(Long memberId, Long checklistId, ChecklistUpdateRequest request);

}
