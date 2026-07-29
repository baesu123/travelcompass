package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.config.MemberDetails;
import com.example.travelcompass.dto.response.CountryDetailResponse;
import com.example.travelcompass.service.CountryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryFacadeService countryFacadeService;

    @GetMapping("/{countryCode}")
    public ApiResponse<CountryDetailResponse> getCountryDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                 @PathVariable String countryCode) {
        Long memberId = memberDetails != null ? memberDetails.getMemberId() : null;
        String nickname = memberDetails != null ? memberDetails.getNickname() : null;
        return ApiResponse.success(countryFacadeService.getCountryDetail(countryCode, memberId, nickname));
    }

}
