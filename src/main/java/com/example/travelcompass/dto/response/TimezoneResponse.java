package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 특정 국가의 시차 정보(현지 시각, 한국 시각, 시차)를 클라이언트에게 응답으로 내려주기
 * 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 원본 응답이 아니라, 외부 타임존
 * 정보를 조회한 뒤 서비스에서 가공한 결과를 담는다.)
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @Builder : TimezoneResponse.builder()...build() 형태로 가독성 좋게 객체를 생성.
 * - @AllArgsConstructor : 모든 필드를 받는 생성자를 자동 생성 (Builder가 내부적으로 사용).
 */
@Getter
@Builder
@AllArgsConstructor
public class TimezoneResponse {

    // 국가 코드 (예: "KR")
    private String countryCode;
    // 해당 국가의 타임존 식별자 (예: "Asia/Seoul")
    private String timezone;
    // 해당 국가의 현재(조회 시점) 현지 시각 문자열
    private String localTime;
    // 같은 시점의 한국 시각 문자열 (비교용)
    private String koreaTime;
    // 한국 시각 대비 시차 (시간 단위, 예: +9.0, -5.0 등)
    private double timeDifferenceHours;

}
