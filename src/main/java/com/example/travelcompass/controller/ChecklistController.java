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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checklists")
public class ChecklistController {

    private final ChecklistService checklistService;

    @GetMapping
    public ApiResponse<List<ChecklistResponse>> getChecklist(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.success(checklistService.getChecklist(memberDetails.getMemberId()));
    }

    @PostMapping
    public ApiResponse<Void> addChecklistItem(@AuthenticationPrincipal MemberDetails memberDetails,
                                               @Valid @RequestBody ChecklistCreateRequest request) {
        checklistService.addChecklistItem(memberDetails.getMemberId(), request);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{checklistId}")
    public ApiResponse<Void> updateChecklist(@AuthenticationPrincipal MemberDetails memberDetails,
                                              @PathVariable Long checklistId,
                                              @RequestBody ChecklistUpdateRequest request) {
        checklistService.updateChecklist(memberDetails.getMemberId(), checklistId, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{checklistId}")
    public ApiResponse<Void> deleteChecklistItem(@AuthenticationPrincipal MemberDetails memberDetails,
                                                  @PathVariable Long checklistId) {
        checklistService.deleteChecklistItem(memberDetails.getMemberId(), checklistId);
        return ApiResponse.success(null);
    }

}
