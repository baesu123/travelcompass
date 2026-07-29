package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.dto.response.TimezoneResponse;
import com.example.travelcompass.service.TimezoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timezones")
public class TimezoneController {

    private final TimezoneService timezoneService;

    @GetMapping("/{countryCode}")
    public ApiResponse<TimezoneResponse> getTimezone(@PathVariable String countryCode) {
        return ApiResponse.success(timezoneService.getTimezone(countryCode));
    }

}
