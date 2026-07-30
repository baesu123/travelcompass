package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.dto.response.TimezoneResponse;
import com.example.travelcompass.service.TimezoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 국가 코드를 기준으로 해당 국가의 시간대(타임존) 정보를 제공하는 REST API 컨트롤러.
 * 여행지와 현재 위치의 시차를 계산하는 데 사용되는 기초 데이터를 반환한다.
 */
// @RestController : 반환값을 View가 아닌 JSON 응답 바디로 그대로 내려주는 API 컨트롤러 어노테이션.
// @RequestMapping("/api/timezones") : 이 컨트롤러의 모든 엔드포인트 공통 경로 prefix.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timezones")
public class TimezoneController {

    // 실제 시간대 계산/조회 로직(내부 데이터 또는 외부 API 호출)을 담당하는 서비스.
    private final TimezoneService timezoneService;

    /**
     * 국가 코드로 해당 국가의 시간대 정보를 조회한다.
     *
     * @param countryCode ISO 국가 코드 등 국가를 식별하는 값 (예: "KR", "US")
     * @return 조회된 시간대 정보를 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping("/{countryCode}")
    public ApiResponse<TimezoneResponse> getTimezone(@PathVariable String countryCode) {
        // @PathVariable : URL 경로 중 {countryCode} 부분의 실제 값을 메서드 파라미터로 바인딩한다.
        // 예) GET /api/timezones/KR 요청 시 countryCode = "KR"
        return ApiResponse.success(timezoneService.getTimezone(countryCode));
    }

}
